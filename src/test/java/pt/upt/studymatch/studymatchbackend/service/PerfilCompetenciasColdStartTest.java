package pt.upt.studymatch.studymatchbackend.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import pt.upt.studymatch.studymatchbackend.model.*;
import pt.upt.studymatch.studymatchbackend.repository.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PerfilCompetenciasColdStartTest {
    @Mock NivelCompetenciaRepository nivelCompetenciaRepository;
    @Mock PerfilCompetenciasRepository perfilCompetenciasRepository;
    @Mock EnrollmentRepository enrollmentRepository;
    @Mock CompetenciaPesoRepository competenciaPesoRepository;
    @Mock EstudanteRepository estudanteRepository;
    @Mock CompetenciaService competenciaService;
    @Mock ColdStartProfileRepository coldStartProfileRepository;
    @Mock CompetenciaRepository competenciaRepository;
    @InjectMocks PerfilCompetenciasService service;

    @Test
    void autoavaliacaoPerdePesoComCadaNotaDaMesmaCompetencia() {
        Estudante estudante = new Estudante("Ana", "ana@upt.pt", "pass", "123", "Informática");
        Competencia competencia = new Competencia("Programação", "Código");
        ReflectionTestUtils.setField(competencia, "id", 3L);
        CourseUnit unidade = new CourseUnit("EI1", "Algoritmos", 1, 1, 6);
        ReflectionTestUtils.setField(unidade, "id", 5L);
        ColdStartProfile inicial = new ColdStartProfile(estudante, "IA", "Liderança", Map.of(3L, 5));
        when(estudanteRepository.findById(1L)).thenReturn(Optional.of(estudante));
        when(coldStartProfileRepository.findByEstudanteId(1L)).thenReturn(Optional.of(inicial));
        when(competenciaRepository.findAllById(any())).thenReturn(List.of(competencia));
        when(perfilCompetenciasRepository.findByEstudanteNumeroEstudante("123"))
                .thenReturn(Optional.empty());

        service.recalcularPerfil(1L);
        assertNivel(5, "AUTOAVALIACAO");

        when(enrollmentRepository.findByEstudanteId(1L)).thenReturn(List.of(
                new Enrollment(estudante, unidade, "2025/26", 10.0)));
        when(competenciaPesoRepository.findByCourseUnitId(5L))
                .thenReturn(List.of(new CompetenciaPeso(unidade, competencia, 100.0)));
        when(competenciaService.converterParaNivel(10.0)).thenReturn(2);
        service.recalcularPerfil(1L);
        assertNivel(4, "MISTO");

        when(enrollmentRepository.findByEstudanteId(1L)).thenReturn(List.of(
                new Enrollment(estudante, unidade, "2025/26", 10.0),
                new Enrollment(estudante, unidade, "2026/27", 10.0)));
        service.recalcularPerfil(1L);
        assertNivel(3, "MISTO");
    }

    private void assertNivel(int esperado, String origem) {
        ArgumentCaptor<PerfilCompetencias> captor = ArgumentCaptor.forClass(PerfilCompetencias.class);
        verify(perfilCompetenciasRepository, atLeastOnce()).save(captor.capture());
        NivelCompetencia nivel = captor.getValue().getNiveis().get(0);
        assertEquals(esperado, nivel.getNivel());
        assertEquals(origem, nivel.getOrigem());
    }
}
