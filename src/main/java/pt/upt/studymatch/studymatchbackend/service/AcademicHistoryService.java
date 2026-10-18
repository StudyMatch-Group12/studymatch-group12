package pt.upt.studymatch.studymatchbackend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.upt.studymatch.studymatchbackend.dto.EnrollmentRequestDTO;
import pt.upt.studymatch.studymatchbackend.model.CourseUnit;
import pt.upt.studymatch.studymatchbackend.model.Enrollment;
import pt.upt.studymatch.studymatchbackend.model.Estudante;
import pt.upt.studymatch.studymatchbackend.repository.CourseUnitRepository;
import pt.upt.studymatch.studymatchbackend.repository.EnrollmentRepository;
import pt.upt.studymatch.studymatchbackend.repository.EstudanteRepository;

import java.util.List;

@Service
public class AcademicHistoryService {

    private final EnrollmentRepository enrollmentRepository;
    private final CourseUnitRepository courseUnitRepository;
    private final EstudanteRepository estudanteRepository;

    public AcademicHistoryService(EnrollmentRepository enrollmentRepository,
                                  CourseUnitRepository courseUnitRepository,
                                  EstudanteRepository estudanteRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.courseUnitRepository = courseUnitRepository;
        this.estudanteRepository = estudanteRepository;
    }

    @Transactional
    public Enrollment registerEnrollment(Long estudanteId, EnrollmentRequestDTO request) {
        if (request.getGrade() == null || request.getGrade() < 0.0 || request.getGrade() > 20.0) {
            throw new IllegalArgumentException("A classificacao deve estar compreendida entre 0 e 20 valores.");
        }

        Estudante estudante = estudanteRepository.findById(estudanteId)
                .orElseThrow(() -> new IllegalArgumentException("Estudante nao encontrado com o ID: " + estudanteId));

        CourseUnit courseUnit = courseUnitRepository.findById(request.getCourseUnitId())
                .orElseThrow(() -> new IllegalArgumentException("Unidade Curricular nao encontrada com o ID: " + request.getCourseUnitId()));

        boolean exists = enrollmentRepository
                .findByEstudanteIdAndCourseUnitIdAndAcademicYear(estudanteId, request.getCourseUnitId(), request.getAcademicYear())
                .isPresent();

        if (exists) {
            throw new IllegalStateException("O estudante ja possui uma inscricao registada nesta UC para o ano letivo " + request.getAcademicYear());
        }

        Enrollment enrollment = new Enrollment(estudante, courseUnit, request.getAcademicYear(), request.getGrade());
        return enrollmentRepository.save(enrollment);
    }

    @Transactional(readOnly = true)
    public List<Enrollment> getStudentHistory(Long estudanteId) {
        if (!estudanteRepository.existsById(estudanteId)) {
            throw new IllegalArgumentException("Estudante nao encontrado com o ID: " + estudanteId);
        }
        return enrollmentRepository.findByEstudanteId(estudanteId);
    }
}