package pt.upt.studymatch.studymatchbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pt.upt.studymatch.studymatchbackend.model.PerfilCompetencias;

import java.util.Optional;

@Repository
public interface PerfilCompetenciasRepository
        extends JpaRepository<PerfilCompetencias, Long> {

    Optional<PerfilCompetencias> findByEstudanteNumeroEstudante(
            String numeroEstudante
    );
}