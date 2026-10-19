package pt.upt.studymatch.studymatchbackend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.upt.studymatch.studymatchbackend.dto.ColdStartRequest;
import pt.upt.studymatch.studymatchbackend.model.ColdStartProfile;
import pt.upt.studymatch.studymatchbackend.model.Estudante;
import pt.upt.studymatch.studymatchbackend.repository.*;
import java.util.Map;

@Service
public class ColdStartService {
    private final EstudanteRepository estudanteRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final CompetenciaRepository competenciaRepository;
    private final ColdStartProfileRepository coldStartProfileRepository;
    private final PerfilCompetenciasService perfilCompetenciasService;

    public ColdStartService(EstudanteRepository estudanteRepository, EnrollmentRepository enrollmentRepository,
                            CompetenciaRepository competenciaRepository, ColdStartProfileRepository coldStartProfileRepository,
                            PerfilCompetenciasService perfilCompetenciasService) {
        this.estudanteRepository = estudanteRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.competenciaRepository = competenciaRepository;
        this.coldStartProfileRepository = coldStartProfileRepository;
        this.perfilCompetenciasService = perfilCompetenciasService;
    }

    @Transactional
    public void criarPerfil(Long estudanteId, ColdStartRequest request) {
        Estudante estudante = estudanteRepository.findById(estudanteId)
                .orElseThrow(() -> new IllegalArgumentException("Estudante não encontrado."));
        if (enrollmentRepository.findByEstudanteId(estudanteId).stream().anyMatch(e -> e.getGrade() != null)) {
            throw new IllegalStateException("O estudante já possui classificações académicas.");
        }
        if (coldStartProfileRepository.findByEstudanteId(estudanteId).isPresent()) {
            throw new IllegalStateException("O estudante já possui um perfil provisório.");
        }
        if (request == null || request.getNiveis() == null || request.getNiveis().isEmpty()) {
            throw new IllegalArgumentException("Indique pelo menos uma competência.");
        }
        Map<Long, Integer> niveis = request.getNiveis();
        for (Map.Entry<Long, Integer> assessment : niveis.entrySet()) {
            if (assessment.getKey() == null || assessment.getValue() == null
                    || assessment.getValue() < 1 || assessment.getValue() > 5
                    || !competenciaRepository.existsById(assessment.getKey())) {
                throw new IllegalArgumentException("Competência ou nível inválido.");
            }
        }
        String interesses = request.getInteresses() == null ? "" : request.getInteresses().trim();
        String papel = request.getPreferenciaPapel() == null ? "" : request.getPreferenciaPapel().trim();
        if (interesses.length() > 255 || papel.length() > 255) {
            throw new IllegalArgumentException("Interesses e preferência devem ter até 255 caracteres.");
        }
        coldStartProfileRepository.save(new ColdStartProfile(estudante, interesses, papel, niveis));
        perfilCompetenciasService.recalcularPerfil(estudanteId);
    }
}
