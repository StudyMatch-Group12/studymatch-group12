package pt.upt.studymatch.studymatchbackend.service;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import pt.upt.studymatch.studymatchbackend.model.Estudante;
import pt.upt.studymatch.studymatchbackend.repository.EstudanteRepository;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class Controlo_UtilizadorTest {

    @Mock
    private EstudanteRepository estudanteRepository;

    private Controlo_Utilizador controloUtilizador;


    @BeforeEach
    public void setUp() {
        controloUtilizador = new Controlo_Utilizador(estudanteRepository);
    }

    @Test
    public void testRegistarEstudanteComDadosValidos() {
        Estudante novoEstudante = new Estudante("João Silva", "joao@upt.pt", "senha123", "3000123", "Engenharia Informática");

        when(estudanteRepository.findByNumeroEstudante("3000123")).thenReturn(Optional.empty());
        when(estudanteRepository.findByEmail("joao@upt.pt")).thenReturn(Optional.empty());
        when(estudanteRepository.save(any(Estudante.class))).thenReturn(novoEstudante);

        Estudante guardado = controloUtilizador.registarEstudante(novoEstudante);

        assertNotNull(guardado);

        assertEquals("João Silva", guardado.getNome());
    }

    @Test
    public void testRegistarEstudanteComEmailInvalido() {
        Estudante estudanteInvalido = new Estudante("Ana Costa", "anacosta_upt.pt", "senha123", "3000124", "Gestão");
        try {
            controloUtilizador.registarEstudante(estudanteInvalido);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("O e-mail introduzido é inválido.", e.getMessage());
        }
    }

    @Test
    public void testRegistarEstudanteComNumeroDuplicado() {
        Estudante estudanteExistente = new Estudante("Carlos", "carlos@upt.pt", "senha", "3000125", "Direito");
        Estudante novoEstudante = new Estudante("Novo Carlos", "novo@upt.pt", "senha", "3000125", "Direito");
        when(estudanteRepository.findByNumeroEstudante("3000125")).thenReturn(Optional.of(estudanteExistente));
        try {
            controloUtilizador.registarEstudante(novoEstudante);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("Já existe um estudante registado com o número"));
        }
    }

    @Test
    public void testSearchStudentsUsesDefaultSortByNameAscending() {
        Estudante student = new Estudante("Ana Costa", "ana@upt.pt", "password", "3000126", "Gestão");
        when(estudanteRepository.searchStudents(isNull(), isNull(), any(Sort.class))).thenReturn(List.of(student));

        List<Estudante> result = controloUtilizador.searchStudents(null, null, null, null);

        assertEquals(1, result.size());
        verify(estudanteRepository).searchStudents(null, null, Sort.by(Sort.Direction.ASC, "nome"));
    }

    @Test
    public void testSearchStudentsTrimsFiltersAndTreatsBlankAsNoFilter() {
        controloUtilizador.searchStudents("  ana  ", "   ", "course", "DESC");

        verify(estudanteRepository).searchStudents("ana", null, Sort.by(Sort.Direction.DESC, "curso"));
    }

    @Test
    public void testSearchStudentsMapsNumberSortToEntityField() {
        controloUtilizador.searchStudents(null, "Gestão", "number", "asc");

        verify(estudanteRepository).searchStudents(null, "Gestão", Sort.by(Sort.Direction.ASC, "numeroEstudante"));
    }

    @Test
    public void testSearchStudentsRejectsInvalidSortField() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> controloUtilizador.searchStudents(null, null, "password", null));

        assertTrue(e.getMessage().contains("Invalid sort field"));
        verifyNoInteractions(estudanteRepository);
    }

    @Test
    public void testSearchStudentsRejectsInvalidSortDirection() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> controloUtilizador.searchStudents(null, null, "name", "sideways"));

        assertTrue(e.getMessage().contains("Invalid sort direction"));
        verifyNoInteractions(estudanteRepository);
    }
}
