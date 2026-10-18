package pt.upt.studymatch.studymatchbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pt.upt.studymatch.studymatchbackend.model.CompetenciaPeso;

import java.util.List;

@Repository
public interface CompetenciaPesoRepository
        extends JpaRepository<CompetenciaPeso, Long> {

    List<CompetenciaPeso> findByCourseUnitId(Long courseUnitId);

    List<CompetenciaPeso> findByCompetenciaId(Long competenciaId);
}