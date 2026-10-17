package pt.upt.studymatch.studymatchbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pt.upt.studymatch.studymatchbackend.model.CompetencyLevel;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompetencyLevelRepository extends JpaRepository<CompetencyLevel, Long> {
    List<CompetencyLevel> findByStudent_IdOrderByCompetency_NameAsc(Long studentId);
    Optional<CompetencyLevel> findByStudent_IdAndCompetency_Id(Long studentId, Long competencyId);
}
