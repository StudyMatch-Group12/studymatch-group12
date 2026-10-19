package pt.upt.studymatch.studymatchbackend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.upt.studymatch.studymatchbackend.dto.ColdStartRequest;
import pt.upt.studymatch.studymatchbackend.model.Competencia;
import pt.upt.studymatch.studymatchbackend.repository.CompetenciaRepository;
import pt.upt.studymatch.studymatchbackend.service.ColdStartService;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ColdStartController {
    private final ColdStartService coldStartService;
    private final CompetenciaRepository competenciaRepository;

    public ColdStartController(ColdStartService coldStartService, CompetenciaRepository competenciaRepository) {
        this.coldStartService = coldStartService;
        this.competenciaRepository = competenciaRepository;
    }

    @GetMapping("/competencias")
    public List<Competencia> listarCompetencias() {
        return competenciaRepository.findAll();
    }

    @PostMapping("/estudantes/{estudanteId}/cold-start")
    public ResponseEntity<?> criarPerfil(@PathVariable Long estudanteId, @RequestBody ColdStartRequest request) {
        try {
            coldStartService.criarPerfil(estudanteId, request);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }
}
