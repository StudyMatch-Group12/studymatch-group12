package pt.upt.studymatch.studymatchbackend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import pt.upt.studymatch.studymatchbackend.dto.CalculoCompetenciaRequest;
import pt.upt.studymatch.studymatchbackend.dto.CalculoCompetenciaResponse;
import pt.upt.studymatch.studymatchbackend.service.CompetenciaService;
import java.util.List;
import pt.upt.studymatch.studymatchbackend.dto.CompetenciaPerfilResponse;
import pt.upt.studymatch.studymatchbackend.service.PerfilCompetenciasService;

@RestController
@RequestMapping("/api/competencias")
public class CompetenciaController {

    private final PerfilCompetenciasService perfilCompetenciasService;
    private final CompetenciaService competenciaService;

    public CompetenciaController(
            CompetenciaService competenciaService,
            PerfilCompetenciasService perfilCompetenciasService) {

        this.competenciaService = competenciaService;
        this.perfilCompetenciasService = perfilCompetenciasService;
    }

    @GetMapping("/estudantes/{numeroEstudante}")
    public ResponseEntity<List<CompetenciaPerfilResponse>> consultarPerfil(
            @PathVariable String numeroEstudante) {

        return ResponseEntity.ok(
                perfilCompetenciasService.consultarPerfil(numeroEstudante)
        );
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