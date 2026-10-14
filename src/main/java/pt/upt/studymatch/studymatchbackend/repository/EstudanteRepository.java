package pt.upt.studymatch.studymatchbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pt.upt.studymatch.studymatchbackend.model.Estudante;

import java.util.Optional;

@Repository
public interface EstudanteRepository extends JpaRepository<Estudante, Long> {
    Optional<Estudante> findByNumeroEstudante(String numeroEstudante);
    Optional<Estudante> findByEmail(String email);
}
