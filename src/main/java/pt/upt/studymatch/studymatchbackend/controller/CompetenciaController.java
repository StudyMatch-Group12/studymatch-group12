package pt.upt.studymatch.studymatchbackend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import pt.upt.studymatch.studymatchbackend.dto.CalculoCompetenciaRequest;
import pt.upt.studymatch.studymatchbackend.dto.CalculoCompetenciaResponse;
import pt.upt.studymatch.studymatchbackend.service.CompetenciaService;

@RestController
@RequestMapping("/api/competencias")
public class CompetenciaController {

    private final CompetenciaService competenciaService;

    public CompetenciaController(CompetenciaService competenciaService) {
        this.competenciaService = competenciaService;
    }

    @PostMapping("/calcular")
    public ResponseEntity<CalculoCompetenciaResponse> calcular(
            @RequestBody CalculoCompetenciaRequest request) {

        double media = competenciaService.calcularMediaPonderada(
                request.getNotas(),
                request.getPesos()
        );

        int nivel = competenciaService.converterParaNivel(media);

        return ResponseEntity.ok(
                new CalculoCompetenciaResponse(media, nivel)
        );
    }
}