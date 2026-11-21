package pt.upt.studymatch.studymatchbackend.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pt.upt.studymatch.studymatchbackend.dto.GroupGenerationDtos.GenerationResponse;
import pt.upt.studymatch.studymatchbackend.service.GroupGenerationService;

@RestController
public class GroupGenerationController {
    private final GroupGenerationService service;

    public GroupGenerationController(
            GroupGenerationService service) {
        this.service = service;
    }

    @PostMapping(
        "/api/grouping-contexts/{contextId}/generations"
    )
    @ResponseStatus(HttpStatus.CREATED)
    public GenerationResponse generate(
            @PathVariable Long contextId) {
        return service.generate(contextId);
    }

    @GetMapping(
        "/api/grouping-contexts/{contextId}/generations"
    )
    public List<GenerationResponse> list(
            @PathVariable Long contextId) {
        return service.list(contextId);
    }

    @GetMapping("/api/group-generations/{generationId}")
    public GenerationResponse get(
            @PathVariable Long generationId) {
        return service.get(generationId);
    }
}
