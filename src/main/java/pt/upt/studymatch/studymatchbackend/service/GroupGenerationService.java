package pt.upt.studymatch.studymatchbackend.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import pt.upt.studymatch.studymatchbackend.dto.GroupGenerationDtos.GenerateRequest;
import pt.upt.studymatch.studymatchbackend.dto.GroupGenerationDtos.GenerationResponse;
import pt.upt.studymatch.studymatchbackend.dto.GroupGenerationDtos.GroupResponse;
import pt.upt.studymatch.studymatchbackend.dto.GroupGenerationDtos.StudentResponse;
import pt.upt.studymatch.studymatchbackend.model.Estudante;
import pt.upt.studymatch.studymatchbackend.model.GroupAssignment;
import pt.upt.studymatch.studymatchbackend.model.GroupGeneration;
import pt.upt.studymatch.studymatchbackend.model.GroupingContext;
import pt.upt.studymatch.studymatchbackend.model.NivelCompetencia;
import pt.upt.studymatch.studymatchbackend.repository.GroupAssignmentRepository;
import pt.upt.studymatch.studymatchbackend.repository.GroupGenerationRepository;
import pt.upt.studymatch.studymatchbackend.repository.GroupingContextRepository;
import pt.upt.studymatch.studymatchbackend.repository.NivelCompetenciaRepository;

@Service
public class GroupGenerationService {
    private static final int MIN_GROUP_SIZE = 2;

    private final GroupingContextRepository contextRepository;
    private final GroupGenerationRepository generationRepository;
    private final GroupAssignmentRepository assignmentRepository;
    private final NivelCompetenciaRepository levelRepository;
    private final GroupMatchingStrategy strategy;

    public GroupGenerationService(
            GroupingContextRepository contextRepository,
            GroupGenerationRepository generationRepository,
            GroupAssignmentRepository assignmentRepository,
            NivelCompetenciaRepository levelRepository,
            GroupMatchingStrategy strategy) {
        this.contextRepository = contextRepository;
        this.generationRepository = generationRepository;
        this.assignmentRepository = assignmentRepository;
        this.levelRepository = levelRepository;
        this.strategy = strategy;
    }

    @Transactional
    public GenerationResponse generate(Long contextId, GenerateRequest request) {
        if (request == null || request.groupSize() == null) {
            throw badRequest("Tamanho do grupo é obrigatório");
        }
        int groupSize = request.groupSize();
        if (groupSize < MIN_GROUP_SIZE) {
            throw badRequest("Tamanho do grupo tem de ser pelo menos "
                    + MIN_GROUP_SIZE);
        }

        GroupingContext context = findContext(contextId);
        List<Estudante> students = context.getStudents().stream()
                .sorted(Comparator.comparing(Estudante::getId))
                .toList();
        if (students.size() < MIN_GROUP_SIZE) {
            throw badRequest("O contexto precisa de pelo menos "
                    + MIN_GROUP_SIZE + " estudantes");
        }
        if (groupSize > students.size()) {
            throw badRequest("Tamanho do grupo maior que o número de estudantes");
        }

        Map<Long, List<NivelCompetencia>> levels = new LinkedHashMap<>();
        for (Estudante student : students) {
            levels.put(student.getId(),
                    levelRepository.findByPerfilCompetenciasEstudanteId(
                            student.getId()));
        }

        List<List<Estudante>> groups =
                strategy.formGroups(students, levels, groupSize);
        ensureEachStudentOnce(students, groups);

        GroupGeneration generation =
                generationRepository.save(new GroupGeneration(context));
        List<GroupAssignment> assignments = new ArrayList<>();
        for (int i = 0; i < groups.size(); i++) {
            for (Estudante student : groups.get(i)) {
                assignments.add(
                        new GroupAssignment(generation, student, i + 1));
            }
        }
        assignmentRepository.saveAll(assignments);

        return toResponse(generation, assignments);
    }

    @Transactional(readOnly = true)
    public List<GenerationResponse> list(Long contextId) {
        findContext(contextId);
        return generationRepository
                .findByContextIdOrderByGeneratedAtDescIdDesc(contextId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public GenerationResponse get(Long contextId, Long generationId) {
        findContext(contextId);
        GroupGeneration generation = generationRepository.findById(generationId)
                .filter(g -> g.getContext().getId().equals(contextId))
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Geração não encontrada"
                ));
        return toResponse(generation);
    }

    private GroupingContext findContext(Long contextId) {
        return contextRepository.findById(contextId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Contexto não encontrado"
                ));
    }

    private void ensureEachStudentOnce(
            List<Estudante> students,
            List<List<Estudante>> groups) {
        Set<Long> expected = new HashSet<>();
        students.forEach(s -> expected.add(s.getId()));

        Set<Long> seen = new HashSet<>();
        for (List<Estudante> group : groups) {
            if (group.isEmpty()) {
                throw new IllegalStateException("Algoritmo devolveu grupo vazio");
            }
            for (Estudante student : group) {
                if (!expected.contains(student.getId())
                        || !seen.add(student.getId())) {
                    throw new IllegalStateException(
                            "Algoritmo atribuiu um estudante inválido ou repetido");
                }
            }
        }
        if (!seen.equals(expected)) {
            throw new IllegalStateException(
                    "Algoritmo não atribuiu todos os estudantes");
        }
    }

    private GenerationResponse toResponse(GroupGeneration generation) {
        return toResponse(generation,
                assignmentRepository.findByGenerationIdOrderByGroupNumberAscIdAsc(
                        generation.getId()));
    }

    private GenerationResponse toResponse(
            GroupGeneration generation,
            List<GroupAssignment> assignments) {
        Map<Integer, List<StudentResponse>> byGroup = new LinkedHashMap<>();
        assignments.stream()
                .sorted(Comparator.comparingInt(GroupAssignment::getGroupNumber))
                .forEach(a -> byGroup
                        .computeIfAbsent(a.getGroupNumber(), n -> new ArrayList<>())
                        .add(toStudent(a.getStudent())));

        List<GroupResponse> groups = byGroup.entrySet().stream()
                .map(e -> new GroupResponse(e.getKey(), e.getValue()))
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

    private StudentResponse toStudent(Estudante student) {
        return new StudentResponse(
                student.getId(),
                student.getNome(),
                student.getNumeroEstudante(),
                student.getCurso()
        );
    }

    private ResponseStatusException badRequest(String message) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }
}
