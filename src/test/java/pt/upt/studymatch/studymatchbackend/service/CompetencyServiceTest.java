package pt.upt.studymatch.studymatchbackend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pt.upt.studymatch.studymatchbackend.dto.CompetencyLevelDTO;
import pt.upt.studymatch.studymatchbackend.model.Competency;
import pt.upt.studymatch.studymatchbackend.model.CompetencyLevel;
import pt.upt.studymatch.studymatchbackend.model.Estudante;
import pt.upt.studymatch.studymatchbackend.repository.CompetencyLevelRepository;
import pt.upt.studymatch.studymatchbackend.repository.CompetencyRepository;
import pt.upt.studymatch.studymatchbackend.repository.EstudanteRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CompetencyServiceTest {

    @Mock
    private EstudanteRepository studentRepository;
    @Mock
    private CompetencyRepository competencyRepository;
    @Mock
    private CompetencyLevelRepository competencyLevelRepository;

    private CompetencyService competencyService;
    private Estudante student;

    @BeforeEach
    public void setUp() {
        competencyService = new CompetencyService(studentRepository, competencyRepository, competencyLevelRepository);
        student = new Estudante("Ana Costa", "ana@upt.pt", "password", "3000101", "Gestão");
    }

    @Test
    public void testGetCompetencyProfileMapsLevelsToDto() {
        Competency programming = new Competency("Programming", null);
        when(studentRepository.existsById(1L)).thenReturn(true);
        when(competencyLevelRepository.findByStudent_IdOrderByCompetency_NameAsc(1L))
                .thenReturn(List.of(new CompetencyLevel(student, programming, 80, "ACADEMIC_HISTORY")));

        List<CompetencyLevelDTO> profile = competencyService.getCompetencyProfile(1L);

        assertEquals(List.of(new CompetencyLevelDTO("Programming", 80, "ACADEMIC_HISTORY")), profile);
    }

    @Test
    public void testGetCompetencyProfileOfUnknownStudentThrows() {
        when(studentRepository.existsById(99L)).thenReturn(false);

        assertThrows(NoSuchElementException.class, () -> competencyService.getCompetencyProfile(99L));
    }

    @Test
    public void testSetCompetencyLevelCreatesCompetencyAndUsesDefaultSource() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(competencyRepository.findByNameIgnoreCase("Teamwork")).thenReturn(Optional.empty());
        when(competencyRepository.save(any(Competency.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(competencyLevelRepository.save(any(CompetencyLevel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CompetencyLevelDTO saved = competencyService.setCompetencyLevel(1L, "  Teamwork ", 65, null);

        assertEquals(new CompetencyLevelDTO("Teamwork", 65, "SELF_ASSESSMENT"), saved);
        verify(competencyRepository).save(any(Competency.class));
    }

    @Test
    public void testSetCompetencyLevelRejectsOutOfRangeLevel() {
        assertThrows(IllegalArgumentException.class, () -> competencyService.setCompetencyLevel(1L, "Teamwork", 101, null));
        assertThrows(IllegalArgumentException.class, () -> competencyService.setCompetencyLevel(1L, "Teamwork", -1, null));
        verifyNoInteractions(studentRepository, competencyRepository, competencyLevelRepository);
    }

    @Test
    public void testSetCompetencyLevelRejectsBlankName() {
        assertThrows(IllegalArgumentException.class, () -> competencyService.setCompetencyLevel(1L, " ", 50, null));
    }
}
