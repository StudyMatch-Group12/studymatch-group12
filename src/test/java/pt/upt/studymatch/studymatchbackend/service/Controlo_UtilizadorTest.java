package pt.upt.studymatch.studymatchbackend.service;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pt.upt.studymatch.studymatchbackend.model.Estudante;
import pt.upt.studymatch.studymatchbackend.repository.EstudanteRepository;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
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
}