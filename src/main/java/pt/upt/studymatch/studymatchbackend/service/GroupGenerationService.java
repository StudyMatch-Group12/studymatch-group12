package pt.upt.studymatch.studymatchbackend.service;

import java.util.*;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import pt.upt.studymatch.studymatchbackend.dto.GroupGenerationDtos.*;
import pt.upt.studymatch.studymatchbackend.model.*;
import pt.upt.studymatch.studymatchbackend.repository.*;

@Service
public class GroupGenerationService {
    private final GroupingContextRepository contexts;
    private final GroupGenerationRepository generations;
    private final GroupAssignmentRepository assignments;
    private final NivelCompetenciaRepository levels;
    private final GroupMatcher matcher;

    public GroupGenerationService(
            GroupingContextRepository contexts,
            GroupGenerationRepository generations,
            GroupAssignmentRepository assignments,
            NivelCompetenciaRepository levels,
            GroupMatcher matcher) {
        this.contexts = contexts;
        this.generations = generations;
        this.assignments = assignments;
        this.levels = levels;
        this.matcher = matcher;
    }

    @Transactional
    public GenerationResponse generate(Long contextId) {
        GroupingContext context = findContext(contextId);

        List<GroupMatcher.Candidate> candidates =
            context.getStudents().stream()
                .map(student -> new GroupMatcher.Candidate(
                    student.getId(),
                    competencyLevels(student.getId())
                ))
                .toList();

        List<List<Long>> groups;
        try {
            groups = matcher.generate(candidates);
        } catch (IllegalArgumentException error) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                error.getMessage()
            );
        }

        Map<Long, Estudante> studentsById =
            context.getStudents().stream()
                .collect(Collectors.toMap(
                    Estudante::getId,
                    student -> student
                ));

        GroupGeneration generation =
            generations.save(new GroupGeneration(context));

        List<GroupAssignment> newAssignments = new ArrayList<>();
        for (int i = 0; i < groups.size(); i++) {
            for (Long studentId : groups.get(i)) {
                newAssignments.add(new GroupAssignment(
                    generation,
                    studentsById.get(studentId),
                    i + 1
                ));
            }
        }

        assignments.saveAllAndFlush(newAssignments);
        return toResponse(generation);
    }

    @Transactional(readOnly = true)
    public List<GenerationResponse> list(Long contextId) {
        findContext(contextId);
        return generations
            .findByContextIdOrderByGeneratedAtDescIdDesc(contextId)
            .stream()
            .map(this::toResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public GenerationResponse get(Long generationId) {
        GroupGeneration generation =
            generations.findById(generationId)
                .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Geração não encontrada"
                ));
        return toResponse(generation);
    }

    private GroupingContext findContext(Long id) {
        return contexts.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Contexto não encontrado"
            ));
    }

    private Map<Long, Integer> competencyLevels(Long studentId) {
        return levels
            .findByPerfilCompetenciasEstudanteId(studentId)
            .stream()
            .collect(Collectors.toMap(
                level -> level.getCompetencia().getId(),
                NivelCompetencia::getNivel,
                (first, duplicate) -> first
            ));
    }

    private GenerationResponse toResponse(
            GroupGeneration generation) {
        Map<Integer, List<StudentResponse>> grouped =
            new TreeMap<>();

        for (GroupAssignment assignment : assignments
                .findByGenerationIdOrderByGroupNumberAscIdAsc(
                    generation.getId()
                )) {
            Estudante student = assignment.getStudent();
            grouped.computeIfAbsent(
                assignment.getGroupNumber(),
                ignored -> new ArrayList<>()
            ).add(new StudentResponse(
                student.getId(),
                student.getNome(),
                student.getNumeroEstudante(),
                student.getCurso()
            ));
        }

        List<GroupResponse> groups = grouped.entrySet().stream()
            .map(entry -> new GroupResponse(
                entry.getKey(),
                List.copyOf(entry.getValue())
            ))
            .toList();

        GroupingContext context = generation.getContext();
        return new GenerationResponse(
            generation.getId(),
            context.getId(),
            context.getName(),
            generation.getGeneratedAt(),
            groups
        );
    }
}
