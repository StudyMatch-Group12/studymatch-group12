package pt.upt.studymatch.studymatchbackend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.upt.studymatch.studymatchbackend.dto.EnrollmentRequestDTO;
import pt.upt.studymatch.studymatchbackend.model.CourseUnit;
import pt.upt.studymatch.studymatchbackend.model.Enrollment;
import pt.upt.studymatch.studymatchbackend.repository.CourseUnitRepository;
import pt.upt.studymatch.studymatchbackend.service.AcademicHistoryService;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
public class AcademicHistoryController {

    private final AcademicHistoryService academicHistoryService;
    private final CourseUnitRepository courseUnitRepository;

    public AcademicHistoryController(AcademicHistoryService academicHistoryService,
                                     CourseUnitRepository courseUnitRepository) {
        this.academicHistoryService = academicHistoryService;
        this.courseUnitRepository = courseUnitRepository;
    }

    @PostMapping("/api/estudantes/{estudanteId}/enrollments")
    public ResponseEntity<?> registerEnrollment(@PathVariable Long estudanteId,
                                                @RequestBody EnrollmentRequestDTO request) {
        try {
            Enrollment saved = academicHistoryService.registerEnrollment(estudanteId, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro interno ao registar histórico.");
        }
    }

    @GetMapping("/api/estudantes/{estudanteId}/enrollments")
    public ResponseEntity<?> getStudentHistory(@PathVariable Long estudanteId) {
        try {
            List<Enrollment> history = academicHistoryService.getStudentHistory(estudanteId);
            return ResponseEntity.ok(history);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/api/course-units")
    public ResponseEntity<List<CourseUnit>> getAllCourseUnits() {
        List<CourseUnit> units = courseUnitRepository.findAll();
        return ResponseEntity.ok(units);
    }
}