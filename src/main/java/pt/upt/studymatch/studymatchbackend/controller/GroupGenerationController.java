package pt.upt.studymatch.studymatchbackend.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pt.upt.studymatch.studymatchbackend.dto.GroupGenerationDtos.GenerateRequest;
import pt.upt.studymatch.studymatchbackend.dto.GroupGenerationDtos.GenerationResponse;
import pt.upt.studymatch.studymatchbackend.service.GroupGenerationService;

@RestController
@RequestMapping("/api/grouping-contexts/{contextId}/generations")
public class GroupGenerationController {
    private final GroupGenerationService service;

    public GroupGenerationController(GroupGenerationService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GenerationResponse generate(
            @PathVariable Long contextId,
            @RequestBody(required = false) GenerateRequest request) {
        return service.generate(contextId, request);
    }

    @GetMapping
    public List<GenerationResponse> list(@PathVariable Long contextId) {
        return service.list(contextId);
    }

    @GetMapping("/{generationId}")
    public GenerationResponse get(
            @PathVariable Long contextId,
            @PathVariable Long generationId) {
        return service.get(contextId, generationId);
    }
}
