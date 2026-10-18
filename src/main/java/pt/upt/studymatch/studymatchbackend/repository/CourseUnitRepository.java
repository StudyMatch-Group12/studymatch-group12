package pt.upt.studymatch.studymatchbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pt.upt.studymatch.studymatchbackend.model.CourseUnit;

import java.util.Optional;

@Repository
public interface CourseUnitRepository extends JpaRepository<CourseUnit, Long> {
    Optional<CourseUnit> findByCode(String code);
}