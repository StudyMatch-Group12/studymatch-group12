package pt.upt.studymatch.studymatchbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pt.upt.studymatch.studymatchbackend.model.Enrollment;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    List<Enrollment> findByEstudanteId(Long estudanteId);
    Optional<Enrollment> findByEstudanteIdAndCourseUnitIdAndAcademicYear(Long estudanteId, Long courseUnitId, String academicYear);
}