package pt.upt.studymatch.studymatchbackend.service;

import org.springframework.stereotype.Service;

import pt.upt.studymatch.studymatchbackend.dto.CompetenciaPerfilResponse;
import pt.upt.studymatch.studymatchbackend.model.NivelCompetencia;
import pt.upt.studymatch.studymatchbackend.repository.NivelCompetenciaRepository;

import java.util.List;

@Service
public class PerfilCompetenciasService {

    private final NivelCompetenciaRepository nivelCompetenciaRepository;

    public PerfilCompetenciasService(
            NivelCompetenciaRepository nivelCompetenciaRepository) {

        this.nivelCompetenciaRepository = nivelCompetenciaRepository;
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
}