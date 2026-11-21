package pt.upt.studymatch.studymatchbackend.repository;

import java.util.LinkedHashSet;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import pt.upt.studymatch.studymatchbackend.model.Estudante;
import pt.upt.studymatch.studymatchbackend.model.GroupAssignment;
import pt.upt.studymatch.studymatchbackend.model.GroupGeneration;
import pt.upt.studymatch.studymatchbackend.model.GroupingContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

// Corre contra H2 em memória, não precisa de MySQL
@DataJpaTest
class GroupGenerationPersistenceTest {

    @Autowired
    private EstudanteRepository studentRepository;

    @Autowired
    private GroupingContextRepository contextRepository;

    @Autowired
    private GroupGenerationRepository generationRepository;

    @Autowired
    private GroupAssignmentRepository assignmentRepository;

    private Estudante ana;
    private Estudante bruno;
    private Estudante carla;
    private GroupingContext context;

    @BeforeEach
    void setUp() {
        ana = studentRepository.save(
                new Estudante("Ana", "ana@upt.pt", "pass", "1001", "EI"));
        bruno = studentRepository.save(
                new Estudante("Bruno", "bruno@upt.pt", "pass", "1002", "EI"));
        carla = studentRepository.save(
                new Estudante("Carla", "carla@upt.pt", "pass", "1003", "EI"));
        context = contextRepository.save(new GroupingContext(
                "Projeto QS", new LinkedHashSet<>(List.of(ana, bruno, carla))));
    }

    @Test
    void savedGenerationCanBeRecoveredWithItsDateAndGroups() {
        GroupGeneration generation =
                generationRepository.save(new GroupGeneration(context));
        assignmentRepository.saveAll(List.of(
                new GroupAssignment(generation, carla, 2),
                new GroupAssignment(generation, ana, 1),
                new GroupAssignment(generation, bruno, 1)));
        assignmentRepository.flush();

        GroupGeneration found =
                generationRepository.findById(generation.getId()).orElseThrow();
        assertNotNull(found.getGeneratedAt());
        assertEquals(context.getId(), found.getContext().getId());

        List<GroupAssignment> assignments = assignmentRepository
                .findByGenerationIdOrderByGroupNumberAscIdAsc(generation.getId());
        assertEquals(List.of(1, 1, 2),
                assignments.stream().map(GroupAssignment::getGroupNumber).toList());
        assertEquals(List.of("Bruno", "Carla"),
                List.of(assignments.get(1).getStudent().getNome(),
                        assignments.get(2).getStudent().getNome()));
    }

    @Test
    void studentCannotBeInTwoGroupsOfTheSameGeneration() {
        GroupGeneration generation =
                generationRepository.save(new GroupGeneration(context));
        assignmentRepository.saveAndFlush(
                new GroupAssignment(generation, ana, 1));

        assertThrows(DataIntegrityViolationException.class,
                () -> assignmentRepository.saveAndFlush(
                        new GroupAssignment(generation, ana, 2)));
    }

    @Test
    void sameStudentCanAppearInDifferentGenerations() {
        GroupGeneration first =
                generationRepository.save(new GroupGeneration(context));
        GroupGeneration second =
                generationRepository.save(new GroupGeneration(context));

        assignmentRepository.saveAndFlush(new GroupAssignment(first, ana, 1));
        assignmentRepository.saveAndFlush(new GroupAssignment(second, ana, 2));

        assertEquals(1, assignmentRepository
                .findByGenerationIdOrderByGroupNumberAscIdAsc(second.getId())
                .size());
    }

    @Test
    void listsGenerationsOfContextNewestFirst() {
        GroupGeneration first =
                generationRepository.save(new GroupGeneration(context));
        GroupGeneration second =
                generationRepository.save(new GroupGeneration(context));

        List<GroupGeneration> result = generationRepository
                .findByContextIdOrderByGeneratedAtDescIdDesc(context.getId());

        assertEquals(List.of(second.getId(), first.getId()),
                result.stream().map(GroupGeneration::getId).toList());
    }
}
