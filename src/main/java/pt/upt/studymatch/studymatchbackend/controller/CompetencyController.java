package pt.upt.studymatch.studymatchbackend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.upt.studymatch.studymatchbackend.dto.CompetencyLevelDTO;
import pt.upt.studymatch.studymatchbackend.dto.CompetencyLevelRequestDTO;
import pt.upt.studymatch.studymatchbackend.service.CompetencyService;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/estudantes/{studentId}/competencies")
public class CompetencyController {

    private final CompetencyService competencyService;

    public CompetencyController(CompetencyService competencyService) {
        this.competencyService = competencyService;
    }

    @GetMapping
    public ResponseEntity<List<CompetencyLevelDTO>> getCompetencyProfile(@PathVariable Long studentId) {
        try {
            return new ResponseEntity<>(competencyService.getCompetencyProfile(studentId), HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping
    public ResponseEntity<?> setCompetencyLevel(@PathVariable Long studentId,
                                                @RequestBody CompetencyLevelRequestDTO request) {
        try {
            CompetencyLevelDTO saved = competencyService.setCompetencyLevel(
                    studentId, request.competency(), request.level(), request.source());
            return new ResponseEntity<>(saved, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
