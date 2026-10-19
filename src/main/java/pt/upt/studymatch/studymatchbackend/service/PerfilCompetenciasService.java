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
    private final ColdStartProfileRepository coldStartProfileRepository;
    private final CompetenciaRepository competenciaRepository;

    public PerfilCompetenciasService(
            NivelCompetenciaRepository nivelCompetenciaRepository,
            PerfilCompetenciasRepository perfilCompetenciasRepository,
            EnrollmentRepository enrollmentRepository,
            CompetenciaPesoRepository competenciaPesoRepository,
            EstudanteRepository estudanteRepository,
            CompetenciaService competenciaService,
            ColdStartProfileRepository coldStartProfileRepository,
            CompetenciaRepository competenciaRepository) {

        this.nivelCompetenciaRepository = nivelCompetenciaRepository;
        this.perfilCompetenciasRepository = perfilCompetenciasRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.competenciaPesoRepository = competenciaPesoRepository;
        this.estudanteRepository = estudanteRepository;
        this.competenciaService = competenciaService;
        this.coldStartProfileRepository = coldStartProfileRepository;
        this.competenciaRepository = competenciaRepository;
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
        Map<Long, Integer> contagemNotas = new HashMap<>();

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
                contagemNotas.merge(competenciaId, 1, Integer::sum);
            }
        }

        Map<Long, Integer> autoavaliacao = coldStartProfileRepository.findByEstudanteId(estudanteId)
                .map(ColdStartProfile::getNiveis)
                .orElseGet(Map::of);
        competenciaRepository.findAllById(autoavaliacao.keySet())
                .forEach(competencia -> competencias.putIfAbsent(competencia.getId(), competencia));

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

        for (Long competenciaId : competencias.keySet()) {
            double totalPeso = somaPesos.getOrDefault(competenciaId, 0.0);
            Integer nivelInicial = autoavaliacao.get(competenciaId);
            if (totalPeso <= 0 && nivelInicial == null) continue;

            int nivelAcademico = totalPeso > 0
                    ? competenciaService.converterParaNivel(somaPonderada.get(competenciaId) / totalPeso)
                    : 0;
            int notas = contagemNotas.getOrDefault(competenciaId, 0);
            int nivel = nivelInicial == null ? nivelAcademico
                    : notas == 0 ? nivelInicial
                    : (int) Math.round((nivelInicial + notas * nivelAcademico) / (double) (notas + 1));
            String origem = nivelInicial == null ? "HISTORICO_ACADEMICO"
                    : notas == 0 ? "AUTOAVALIACAO" : "MISTO";

            NivelCompetencia nivelCompetencia =
                    new NivelCompetencia(
                            nivel,
                            origem,
                            competencias.get(competenciaId),
                            perfil
                    );

            perfil.adicionarNivel(nivelCompetencia);
        }

        perfilCompetenciasRepository.save(perfil);
    }
}
