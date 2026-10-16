package pt.upt.studymatch.studymatchbackend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CompetenciaServiceTest {

    private CompetenciaService competenciaService;

    @BeforeEach
    void setUp() {
        competenciaService = new CompetenciaService();
    }

    @Test
    void deveCalcularMediaPonderadaCorretamente() {
        List<Double> notas = List.of(15.0, 18.0);
        List<Double> pesos = List.of(40.0, 60.0);

        double resultado = competenciaService.calcularMediaPonderada(notas, pesos);

        assertEquals(16.8, resultado, 0.001);
    }

    @Test
    void deveCalcularComApenasUmaNota() {
        double resultado = competenciaService.calcularMediaPonderada(
                List.of(14.0),
                List.of(100.0)
        );

        assertEquals(14.0, resultado, 0.001);
    }

    @Test
    void deveConverterNotaBaixaParaNivel1() {
        assertEquals(1, competenciaService.converterParaNivel(8.0));
    }

    @Test
    void deveConverterParaNivel2() {
        assertEquals(2, competenciaService.converterParaNivel(11.0));
    }

    @Test
    void deveConverterParaNivel3() {
        assertEquals(3, competenciaService.converterParaNivel(14.0));
    }

    @Test
    void deveConverterParaNivel4() {
        assertEquals(4, competenciaService.converterParaNivel(17.0));
    }

    @Test
    void deveConverterNotaAltaParaNivel5() {
        assertEquals(5, competenciaService.converterParaNivel(19.0));
    }

    @Test
    void deveCalcularNivelDaCompetencia() {
        int nivel = competenciaService.calcularNivelCompetencia(
                List.of(15.0, 18.0),
                List.of(40.0, 60.0)
        );

        assertEquals(4, nivel);
    }

    @Test
    void deveRejeitarPesosQueNaoSomam100() {
        assertThrows(
                IllegalArgumentException.class,
                () -> competenciaService.calcularMediaPonderada(
                        List.of(15.0, 18.0),
                        List.of(40.0, 40.0)
                )
        );
    }

    @Test
    void deveRejeitarQuantidadeDiferenteDeNotasEPesos() {
        assertThrows(
                IllegalArgumentException.class,
                () -> competenciaService.calcularMediaPonderada(
                        List.of(15.0, 18.0),
                        List.of(100.0)
                )
        );
    }

    @Test
    void deveRejeitarNotaForaDoIntervalo() {
        assertThrows(
                IllegalArgumentException.class,
                () -> competenciaService.calcularMediaPonderada(
                        List.of(21.0),
                        List.of(100.0)
                )
        );
    }

    @Test
    void deveRejeitarListaVazia() {
        assertThrows(
                IllegalArgumentException.class,
                () -> competenciaService.calcularMediaPonderada(
                        List.of(),
                        List.of()
                )
        );
    }
}