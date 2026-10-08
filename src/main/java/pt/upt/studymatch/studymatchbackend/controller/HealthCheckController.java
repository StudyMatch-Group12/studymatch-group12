package pt.upt.studymatch.studymatchbackend.controller;

import pt.upt.studymatch.studymatchbackend.model.SystemStatus;
import pt.upt.studymatch.studymatchbackend.repository.SystemStatusRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/status")
public class HealthCheckController {

    private final SystemStatusRepository statusRepository;

    public HealthCheckController(SystemStatusRepository statusRepository) {
        this.statusRepository = statusRepository;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getStatus() {
        // Regista o teste no MySQL para simular escrita
        SystemStatus saved = statusRepository.save(
                new SystemStatus("StudyMatch Backend", "UP", LocalDateTime.now())
        );

        // Devolve o JSON com a confirmação da leitura/escrita
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("databaseConnected", true);
        response.put("lastRecordId", saved.getId());
        response.put("timestamp", saved.getCheckedAt().toString());

        return ResponseEntity.ok(response);
    }
}
