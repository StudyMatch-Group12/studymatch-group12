package pt.upt.studymatch.studymatchbackend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.upt.studymatch.studymatchbackend.model.Estudante;
import pt.upt.studymatch.studymatchbackend.service.Controlo_Utilizador;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api/estudantes")
@CrossOrigin(origins = "http://localhost:3000")
public class EstudanteController {

    private final Controlo_Utilizador controloUtilizador;

    @Autowired
    public EstudanteController(Controlo_Utilizador controloUtilizador) {
        this.controloUtilizador = controloUtilizador;
    }
    @PostMapping
    public ResponseEntity<?> registarEstudante(@RequestBody Estudante estudante) {
        try {
            Estudante novoEstudante = controloUtilizador.registarEstudante(estudante);
            return new ResponseEntity<>(novoEstudante, HttpStatus.CREATED); // Retorna 201 Created
        } catch (IllegalArgumentException e) {
            // Se o serviço lançar erro, retorna 400 Bad Request
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            // Retorna um erro genérico 500 para outras falhas
            return new ResponseEntity<>("Erro interno no servidor: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    // GET /api/estudantes?search=ana&course=Gestão&sortBy=name&direction=asc
    // All parameters are optional; without them every student is returned, sorted by name.
    @GetMapping
    public ResponseEntity<?> searchStudents(@RequestParam(required = false) String search,
                                            @RequestParam(required = false) String course,
                                            @RequestParam(required = false) String sortBy,
                                            @RequestParam(required = false) String direction) {
        try {
            List<Estudante> students = controloUtilizador.searchStudents(search, course, sortBy, direction);
            return new ResponseEntity<>(students, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // Distinct course names, used to populate the course filter in the frontend
    @GetMapping("/courses")
    public ResponseEntity<List<String>> getAvailableCourses() {
        return new ResponseEntity<>(controloUtilizador.getAvailableCourses(), HttpStatus.OK);
    }
    @GetMapping("/{id}")
    public ResponseEntity<Estudante> obterEstudantePorId(@PathVariable Long id) {
        Optional<Estudante> estudante = controloUtilizador.obterEstudantePorId(id);
        // Se encontrar devolve 200 OK com o estudante, senão devolve 404 Not Found
        return estudante.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}