package pt.upt.studymatch.studymatchbackend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pt.upt.studymatch.studymatchbackend.dto.CompetenciaPerfilResponse;
import pt.upt.studymatch.studymatchbackend.model.*;
import pt.upt.studymatch.studymatchbackend.repository.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PerfilCompetenciasService {

    private final NivelCompetenciaRepository nivelCompetenciaRepository;
    private final PerfilCompetenciasRepository perfilCompetenciasRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final CompetenciaPesoRepository competenciaPesoRepository;
    private final EstudanteRepository estudanteRepository;
    private final CompetenciaService competenciaService;

    public PerfilCompetenciasService(
            NivelCompetenciaRepository nivelCompetenciaRepository,
            PerfilCompetenciasRepository perfilCompetenciasRepository,
            EnrollmentRepository enrollmentRepository,
            CompetenciaPesoRepository competenciaPesoRepository,
            EstudanteRepository estudanteRepository,
            CompetenciaService competenciaService) {

        this.nivelCompetenciaRepository = nivelCompetenciaRepository;
        this.perfilCompetenciasRepository = perfilCompetenciasRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.competenciaPesoRepository = competenciaPesoRepository;
        this.estudanteRepository = estudanteRepository;
        this.competenciaService = competenciaService;
    }

    public List<CompetenciaPerfilResponse> consultarPerfil(
            String numeroEstudante) {

        List<NivelCompetencia> niveis =
                nivelCompetenciaRepository
                        .findByPerfilCompetenciasEstudanteNumeroEstudante(
                                numeroEstudante
                        );

        return niveis.stream()
                .map(nivel -> new CompetenciaPerfilResponse(
                        nivel.getCompetencia().getNome(),
                        nivel.getCompetencia().getDescricao(),
                        nivel.getNivel(),
                        nivel.getOrigem()
                ))
                .toList();
    }

    @Transactional
    public void recalcularPerfil(Long estudanteId) {

        Estudante estudante = estudanteRepository.findById(estudanteId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Estudante não encontrado com o ID: " + estudanteId
                        )
                );

        List<Enrollment> historico =
                enrollmentRepository.findByEstudanteId(estudanteId);

        Map<Long, Double> somaPonderada = new HashMap<>();
        Map<Long, Double> somaPesos = new HashMap<>();
        Map<Long, Competencia> competencias = new HashMap<>();

        for (Enrollment enrollment : historico) {

            if (enrollment.getGrade() == null) {
                continue;
            }

            List<CompetenciaPeso> pesos =
                    competenciaPesoRepository.findByCourseUnitId(
                            enrollment.getCourseUnit().getId()
                    );

            for (CompetenciaPeso competenciaPeso : pesos) {

                Competencia competencia = competenciaPeso.getCompetencia();
                Long competenciaId = competencia.getId();
                double peso = competenciaPeso.getPeso();

                somaPonderada.merge(
                        competenciaId,
                        enrollment.getGrade() * peso,
                        Double::sum
                );

                somaPesos.merge(
                        competenciaId,
                        peso,
                        Double::sum
                );

                competencias.putIfAbsent(
                        competenciaId,
                        competencia
                );
            }
        }

        PerfilCompetencias perfil =
                perfilCompetenciasRepository
                        .findByEstudanteNumeroEstudante(
                                estudante.getNumeroEstudante()
                        )
                        .orElseGet(() ->
                                new PerfilCompetencias(
                                        LocalDateTime.now(),
                                        estudante
                                )
                        );

        perfil.setDataAtualizacao(LocalDateTime.now());

        // Remove os níveis antigos antes de guardar o recálculo.
        perfil.getNiveis().clear();

        for (Long competenciaId : somaPonderada.keySet()) {

            double totalPeso = somaPesos.get(competenciaId);

            if (totalPeso <= 0) {
                continue;
            }

            double media =
                    somaPonderada.get(competenciaId) / totalPeso;

            int nivel =
                    competenciaService.converterParaNivel(media);

            NivelCompetencia nivelCompetencia =
                    new NivelCompetencia(
                            nivel,
                            "HISTORICO_ACADEMICO",
                            competencias.get(competenciaId),
                            perfil
                    );

            perfil.adicionarNivel(nivelCompetencia);
        }

        perfilCompetenciasRepository.save(perfil);
    }
}