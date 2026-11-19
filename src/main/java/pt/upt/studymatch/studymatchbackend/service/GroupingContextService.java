package pt.upt.studymatch.studymatchbackend.service;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import pt.upt.studymatch.studymatchbackend.dto.GroupingContextDtos.CreateRequest;
import pt.upt.studymatch.studymatchbackend.dto.GroupingContextDtos.ContextResponse;
import pt.upt.studymatch.studymatchbackend.model.Estudante;
import pt.upt.studymatch.studymatchbackend.model.GroupingContext;
import pt.upt.studymatch.studymatchbackend.repository.EstudanteRepository;
import pt.upt.studymatch.studymatchbackend.repository.GroupingContextRepository;

@Service
public class GroupingContextService {
    private final GroupingContextRepository contextRepository;
    private final EstudanteRepository studentRepository;

    public GroupingContextService(
            GroupingContextRepository contextRepository,
            EstudanteRepository studentRepository) {
        this.contextRepository = contextRepository;
        this.studentRepository = studentRepository;
    }

    @Transactional
    public ContextResponse create(CreateRequest request) {
        if (request == null
                || request.name() == null
                || request.name().isBlank()
                || request.studentIds() == null
                || request.studentIds().isEmpty()
                || request.studentIds().contains(null)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Nome e estudantes são obrigatórios"
            );
        }

        Set<Long> ids = new LinkedHashSet<>(request.studentIds());
        if (ids.size() != request.studentIds().size()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Estudantes repetidos"
            );
        }

        List<Estudante> found = studentRepository.findAllById(ids);
        if (found.size() != ids.size()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Estudante inexistente"
            );
        }

        GroupingContext saved = contextRepository.save(
                new GroupingContext(
                        request.name().trim(),
                        new LinkedHashSet<>(found)
                )
        );
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<ContextResponse> list() {
        return contextRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ContextResponse get(Long id) {
        GroupingContext context = contextRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Contexto não encontrado"
                ));
        return toResponse(context);
    }

    private ContextResponse toResponse(GroupingContext context) {
        return new ContextResponse(
                context.getId(),
                context.getName(),
                context.getStudents().stream()
                        .map(Estudante::getId)
                        .sorted()
                        .toList()
        );
    }
}