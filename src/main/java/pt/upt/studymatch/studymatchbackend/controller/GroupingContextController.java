package pt.upt.studymatch.studymatchbackend.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pt.upt.studymatch.studymatchbackend.dto.GroupingContextDtos.CreateRequest;
import pt.upt.studymatch.studymatchbackend.dto.GroupingContextDtos.ContextResponse;
import pt.upt.studymatch.studymatchbackend.service.GroupingContextService;

@RestController
@RequestMapping("/api/grouping-contexts")
public class GroupingContextController {
    private final GroupingContextService service;

    public GroupingContextController(GroupingContextService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ContextResponse create(@RequestBody CreateRequest request) {
        return service.create(request);
    }

    @GetMapping
    public List<ContextResponse> list() {
        return service.list();
    }

    @GetMapping("/{id}")
    public ContextResponse get(@PathVariable Long id) {
        return service.get(id);
    }
}