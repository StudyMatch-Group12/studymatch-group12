package pt.upt.studymatch.studymatchbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pt.upt.studymatch.studymatchbackend.model.NivelCompetencia;

import java.util.List;

@Repository
public interface NivelCompetenciaRepository
        extends JpaRepository<NivelCompetencia, Long> {

    List<NivelCompetencia>
    findByPerfilCompetenciasEstudanteNumeroEstudante(String numeroEstudante);
}