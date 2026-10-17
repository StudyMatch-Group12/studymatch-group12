package pt.upt.studymatch.studymatchbackend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pt.upt.studymatch.studymatchbackend.dto.EnrollmentRequestDTO;
import pt.upt.studymatch.studymatchbackend.model.CourseUnit;
import pt.upt.studymatch.studymatchbackend.model.Enrollment;
import pt.upt.studymatch.studymatchbackend.model.Estudante;
import pt.upt.studymatch.studymatchbackend.repository.CourseUnitRepository;
import pt.upt.studymatch.studymatchbackend.repository.EnrollmentRepository;
import pt.upt.studymatch.studymatchbackend.repository.EstudanteRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AcademicHistoryServiceTest {

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @Mock
    private CourseUnitRepository courseUnitRepository;

    @Mock
    private EstudanteRepository estudanteRepository;

    @InjectMocks
    private AcademicHistoryService academicHistoryService;

    private Estudante estudante;
    private CourseUnit courseUnit;
    private EnrollmentRequestDTO validRequest;

    @BeforeEach
    void setUp() {
        estudante = new Estudante("Tiago", "tiago@upt.pt", "pass123", "12345", "Engenharia Informatica");
        courseUnit = new CourseUnit("EI001", "Programacao Orientada a Objetos", 1, 2, 6);
        validRequest = new EnrollmentRequestDTO(1L, "2025/2026", 16.5);
    }

    @Test
    @DisplayName("Deve registar uma inscricao com sucesso quando os dados forem validos")
    void shouldRegisterEnrollmentSuccessfully() {
        when(estudanteRepository.findById(1L)).thenReturn(Optional.of(estudante));
        when(courseUnitRepository.findById(1L)).thenReturn(Optional.of(courseUnit));
        when(enrollmentRepository.findByEstudanteIdAndCourseUnitIdAndAcademicYear(1L, 1L, "2025/2026"))
                .thenReturn(Optional.empty());

        Enrollment savedEnrollment = new Enrollment(estudante, courseUnit, "2025/2026", 16.5);
        when(enrollmentRepository.save(any(Enrollment.class))).thenReturn(savedEnrollment);

        Enrollment result = academicHistoryService.registerEnrollment(1L, validRequest);

        assertNotNull(result);
        assertEquals(16.5, result.getGrade());
        assertEquals("2025/2026", result.getAcademicYear());
        verify(enrollmentRepository, times(1)).save(any(Enrollment.class));
    }

    @Test
    @DisplayName("Deve lancar excecao quando a nota for superior a 20")
    void shouldThrowExceptionWhenGradeAbove20() {
        validRequest.setGrade(20.5);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                academicHistoryService.registerEnrollment(1L, validRequest)
        );

        assertTrue(exception.getMessage().contains("0 e 20"));
        verify(enrollmentRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lancar excecao quando a nota for negativa")
    void shouldThrowExceptionWhenGradeIsNegative() {
        validRequest.setGrade(-1.0);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                academicHistoryService.registerEnrollment(1L, validRequest)
        );

        assertTrue(exception.getMessage().contains("0 e 20"));
        verify(enrollmentRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve impedir inscricao duplicada na mesma UC e mesmo ano letivo")
    void shouldThrowExceptionWhenEnrollmentAlreadyExists() {
        when(estudanteRepository.findById(1L)).thenReturn(Optional.of(estudante));
        when(courseUnitRepository.findById(1L)).thenReturn(Optional.of(courseUnit));
        when(enrollmentRepository.findByEstudanteIdAndCourseUnitIdAndAcademicYear(1L, 1L, "2025/2026"))
                .thenReturn(Optional.of(new Enrollment(estudante, courseUnit, "2025/2026", 14.0)));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                academicHistoryService.registerEnrollment(1L, validRequest)
        );

        assertTrue(exception.getMessage().contains("ja possui uma inscricao"));
        verify(enrollmentRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve retornar a lista de inscricoes de um estudante")
    void shouldReturnStudentHistory() {
        when(estudanteRepository.existsById(1L)).thenReturn(true);
        when(enrollmentRepository.findByEstudanteId(1L)).thenReturn(List.of(
                new Enrollment(estudante, courseUnit, "2025/2026", 17.0)
        ));

        List<Enrollment> history = academicHistoryService.getStudentHistory(1L);

        assertNotNull(history);
        assertEquals(1, history.size());
        assertEquals(17.0, history.get(0).getGrade());
    }
}