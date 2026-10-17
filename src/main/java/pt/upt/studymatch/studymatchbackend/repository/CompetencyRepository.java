package pt.upt.studymatch.studymatchbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pt.upt.studymatch.studymatchbackend.model.Competency;

import java.util.Optional;

@Repository
public interface CompetencyRepository extends JpaRepository<Competency, Long> {
    Optional<Competency> findByNameIgnoreCase(String name);
}
