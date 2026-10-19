package pt.upt.studymatch.studymatchbackend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pt.upt.studymatch.studymatchbackend.dto.ColdStartRequest;
import pt.upt.studymatch.studymatchbackend.model.ColdStartProfile;
import pt.upt.studymatch.studymatchbackend.model.Estudante;
import pt.upt.studymatch.studymatchbackend.repository.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ColdStartServiceTest {
    @Mock EstudanteRepository estudanteRepository;
    @Mock EnrollmentRepository enrollmentRepository;
    @Mock CompetenciaRepository competenciaRepository;
    @Mock ColdStartProfileRepository coldStartProfileRepository;
    @Mock PerfilCompetenciasService perfilCompetenciasService;
    @InjectMocks ColdStartService service;

    private ColdStartRequest request;

    @BeforeEach
    void setUp() {
        request = new ColdStartRequest();
        request.setInteresses("Inteligência artificial");
        request.setPreferenciaPapel("Organização");
        request.setNiveis(Map.of(3L, 4));
    }

    @Test
    void criaPerfilProvisorioSemNotas() {
        when(estudanteRepository.findById(1L)).thenReturn(Optional.of(
                new Estudante("Ana", "ana@upt.pt", "pass", "123", "Informática")));
        when(enrollmentRepository.findByEstudanteId(1L)).thenReturn(List.of());
        when(competenciaRepository.existsById(3L)).thenReturn(true);

        service.criarPerfil(1L, request);

        ArgumentCaptor<ColdStartProfile> captor = ArgumentCaptor.forClass(ColdStartProfile.class);
        verify(coldStartProfileRepository).save(captor.capture());
        assertEquals(4, captor.getValue().getNiveis().get(3L));
        assertEquals("Inteligência artificial", captor.getValue().getInteresses());
        verify(perfilCompetenciasService).recalcularPerfil(1L);
    }

    @Test
    void rejeitaNivelForaDaEscala() {
        when(estudanteRepository.findById(1L)).thenReturn(Optional.of(
                new Estudante("Ana", "ana@upt.pt", "pass", "123", "Informática")));
        when(enrollmentRepository.findByEstudanteId(1L)).thenReturn(List.of());
        request.setNiveis(Map.of(3L, 6));

        assertThrows(IllegalArgumentException.class, () -> service.criarPerfil(1L, request));
        verify(coldStartProfileRepository, never()).save(any());
    }
}
