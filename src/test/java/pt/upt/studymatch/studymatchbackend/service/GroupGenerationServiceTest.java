package pt.upt.studymatch.studymatchbackend.service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;
import pt.upt.studymatch.studymatchbackend.dto.GroupGenerationDtos.GenerateRequest;
import pt.upt.studymatch.studymatchbackend.dto.GroupGenerationDtos.GenerationResponse;
import pt.upt.studymatch.studymatchbackend.model.Estudante;
import pt.upt.studymatch.studymatchbackend.model.GroupAssignment;
import pt.upt.studymatch.studymatchbackend.model.GroupGeneration;
import pt.upt.studymatch.studymatchbackend.model.GroupingContext;
import pt.upt.studymatch.studymatchbackend.repository.GroupAssignmentRepository;
import pt.upt.studymatch.studymatchbackend.repository.GroupGenerationRepository;
import pt.upt.studymatch.studymatchbackend.repository.GroupingContextRepository;
import pt.upt.studymatch.studymatchbackend.repository.NivelCompetenciaRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class GroupGenerationServiceTest {
    private GroupingContextRepository contexts;
    private GroupGenerationRepository generations;
    private GroupAssignmentRepository assignments;
    private NivelCompetenciaRepository levels;
    private GroupMatchingStrategy strategy;
    private GroupGenerationService service;

    @BeforeEach
    void setUp() {
        contexts = mock(GroupingContextRepository.class);
        generations = mock(GroupGenerationRepository.class);
        assignments = mock(GroupAssignmentRepository.class);
        levels = mock(NivelCompetenciaRepository.class);
        strategy = mock(GroupMatchingStrategy.class);
        service = new GroupGenerationService(
                contexts, generations, assignments, levels, strategy);

        when(levels.findByPerfilCompetenciasEstudanteId(any()))
                .thenReturn(List.of());
        when(generations.save(any())).thenAnswer(invocation -> {
            GroupGeneration generation = invocation.getArgument(0);
            ReflectionTestUtils.setField(generation, "id", 100L);
            return generation;
        });
    }

    @Test
    void rejectsMissingRequestOrGroupSize() {
        assertStatus(HttpStatus.BAD_REQUEST,
                () -> service.generate(1L, null));
        assertStatus(HttpStatus.BAD_REQUEST,
                () -> service.generate(1L, new GenerateRequest(null)));
        verifyNoInteractions(contexts, generations, assignments, strategy);
    }

    @Test
    void rejectsGroupSizeBelowTwo() {
        assertStatus(HttpStatus.BAD_REQUEST,
                () -> service.generate(1L, new GenerateRequest(1)));
        assertStatus(HttpStatus.BAD_REQUEST,
                () -> service.generate(1L, new GenerateRequest(-3)));
        verifyNoInteractions(contexts, generations, assignments, strategy);
    }

    @Test
    void returnsNotFoundForUnknownContext() {
        when(contexts.findById(9L)).thenReturn(Optional.empty());

        assertStatus(HttpStatus.NOT_FOUND,
                () -> service.generate(9L, new GenerateRequest(2)));
        verifyNoInteractions(generations, assignments, strategy);
    }

    @Test
    void rejectsGroupSizeLargerThanContext() {
        givenContext(1L, student(1L), student(2L), student(3L));

        assertStatus(HttpStatus.BAD_REQUEST,
                () -> service.generate(1L, new GenerateRequest(4)));
        verify(generations, never()).save(any());
    }

    @Test
    void rejectsContextWithSingleStudent() {
        givenContext(1L, student(1L));

        assertStatus(HttpStatus.BAD_REQUEST,
                () -> service.generate(1L, new GenerateRequest(2)));
        verify(generations, never()).save(any());
    }

    @Test
    void persistsGenerationWithOneAssignmentPerStudent() {
        Estudante a = student(1L);
        Estudante b = student(2L);
        Estudante c = student(3L);
        Estudante d = student(4L);
        givenContext(1L, a, b, c, d);
        when(strategy.formGroups(anyList(), anyMap(), anyInt()))
                .thenReturn(List.of(List.of(a, d), List.of(b, c)));

        GenerationResponse response =
                service.generate(1L, new GenerateRequest(2));

        assertEquals(100L, response.id());
        assertEquals(1L, response.contextId());
        assertEquals("Contexto 1", response.contextName());
        assertNotNull(response.generatedAt());
        assertEquals(2, response.groups().size());
        assertEquals(1, response.groups().get(0).number());
        assertEquals(List.of(1L, 4L), memberIds(response, 0));
        assertEquals(List.of(2L, 3L), memberIds(response, 1));

        List<GroupAssignment> saved = capturedAssignments();
        assertEquals(4, saved.size());
        assertEquals(List.of(1, 1, 2, 2),
                saved.stream().map(GroupAssignment::getGroupNumber).toList());
        verify(levels).findByPerfilCompetenciasEstudanteId(3L);
    }

    @Test
    void refusesToPersistWhenStrategyRepeatsAStudent() {
        Estudante a = student(1L);
        Estudante b = student(2L);
        givenContext(1L, a, b);
        when(strategy.formGroups(anyList(), anyMap(), anyInt()))
                .thenReturn(List.of(List.of(a, b), List.of(a)));

        assertThrows(IllegalStateException.class,
                () -> service.generate(1L, new GenerateRequest(2)));
        verify(generations, never()).save(any());
        verify(assignments, never()).saveAll(any());
    }

    @Test
    void refusesToPersistWhenStrategyForgetsAStudent() {
        Estudante a = student(1L);
        Estudante b = student(2L);
        Estudante c = student(3L);
        givenContext(1L, a, b, c);
        when(strategy.formGroups(anyList(), anyMap(), anyInt()))
                .thenReturn(List.of(List.of(a, b)));

        assertThrows(IllegalStateException.class,
                () -> service.generate(1L, new GenerateRequest(2)));
        verify(generations, never()).save(any());
    }

    @Test
    void listsGenerationsOfContext() {
        GroupingContext context = givenContext(1L, student(1L), student(2L));
        GroupGeneration generation = generation(7L, context);
        when(generations.findByContextIdOrderByGeneratedAtDescIdDesc(1L))
                .thenReturn(List.of(generation));
        when(assignments.findByGenerationIdOrderByGroupNumberAscIdAsc(7L))
                .thenReturn(List.of(
                        new GroupAssignment(generation, student(1L), 1),
                        new GroupAssignment(generation, student(2L), 1)));

        List<GenerationResponse> response = service.list(1L);

        assertEquals(1, response.size());
        assertEquals(7L, response.get(0).id());
        assertEquals(List.of(1L, 2L), memberIds(response.get(0), 0));
    }

    @Test
    void listReturnsNotFoundForUnknownContext() {
        when(contexts.findById(5L)).thenReturn(Optional.empty());

        assertStatus(HttpStatus.NOT_FOUND, () -> service.list(5L));
    }

    @Test
    void getsSavedGeneration() {
        GroupingContext context = givenContext(1L, student(1L), student(2L));
        GroupGeneration generation = generation(7L, context);
        when(generations.findById(7L)).thenReturn(Optional.of(generation));
        when(assignments.findByGenerationIdOrderByGroupNumberAscIdAsc(7L))
                .thenReturn(List.of(
                        new GroupAssignment(generation, student(2L), 1),
                        new GroupAssignment(generation, student(1L), 2)));

        GenerationResponse response = service.get(1L, 7L);

        assertEquals(7L, response.id());
        assertEquals(List.of(2L), memberIds(response, 0));
        assertEquals(List.of(1L), memberIds(response, 1));
    }

    @Test
    void getReturnsNotFoundForGenerationOfAnotherContext() {
        givenContext(1L, student(1L), student(2L));
        GroupingContext other = context(2L, student(3L), student(4L));
        when(generations.findById(7L))
                .thenReturn(Optional.of(generation(7L, other)));

        assertStatus(HttpStatus.NOT_FOUND, () -> service.get(1L, 7L));
    }

    @Test
    void getReturnsNotFoundForUnknownGeneration() {
        givenContext(1L, student(1L), student(2L));
        when(generations.findById(8L)).thenReturn(Optional.empty());

        assertStatus(HttpStatus.NOT_FOUND, () -> service.get(1L, 8L));
    }

    @SuppressWarnings("unchecked")
    private List<GroupAssignment> capturedAssignments() {
        var captor = org.mockito.ArgumentCaptor.forClass(Iterable.class);
        verify(assignments).saveAll(captor.capture());
        List<GroupAssignment> result = new ArrayList<>();
        ((Iterable<GroupAssignment>) captor.getValue()).forEach(result::add);
        return result;
    }

    private List<Long> memberIds(GenerationResponse response, int group) {
        return response.groups().get(group).members().stream()
                .map(m -> m.id())
                .toList();
    }

    private GroupingContext givenContext(Long id, Estudante... students) {
        GroupingContext context = context(id, students);
        when(contexts.findById(id)).thenReturn(Optional.of(context));
        return context;
    }

    private GroupingContext context(Long id, Estudante... students) {
        GroupingContext context = new GroupingContext(
                "Contexto " + id, new LinkedHashSet<>(List.of(students)));
        ReflectionTestUtils.setField(context, "id", id);
        return context;
    }

    private GroupGeneration generation(Long id, GroupingContext context) {
        GroupGeneration generation = new GroupGeneration(context);
        ReflectionTestUtils.setField(generation, "id", id);
        return generation;
    }

    private Estudante student(Long id) {
        Estudante student = new Estudante(
                "Aluno " + id, "aluno" + id + "@upt.pt", "pass",
                "N" + id, "EI");
        ReflectionTestUtils.setField(student, "id", id);
        return student;
    }

    private void assertStatus(HttpStatus status, Runnable action) {
        ResponseStatusException error = assertThrows(
                ResponseStatusException.class, action::run);
        assertEquals(status, error.getStatusCode());
    }
}
