package pt.upt.studymatch.studymatchbackend.service;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;
import pt.upt.studymatch.studymatchbackend.dto.GroupingContextDtos.CreateRequest;
import pt.upt.studymatch.studymatchbackend.model.Estudante;
import pt.upt.studymatch.studymatchbackend.model.GroupingContext;
import pt.upt.studymatch.studymatchbackend.repository.EstudanteRepository;
import pt.upt.studymatch.studymatchbackend.repository.GroupingContextRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class GroupingContextServiceTest {
    private GroupingContextRepository contexts;
    private EstudanteRepository students;
    private GroupingContextService service;

    @BeforeEach
    void setUp() {
        contexts = mock(GroupingContextRepository.class);
        students = mock(EstudanteRepository.class);
        service = new GroupingContextService(contexts, students);
    }

    @Test
    void rejectsNullRequest() {
        assertBadRequest(() -> service.create(null));
        verifyNoInteractions(students, contexts);
    }

    @Test
    void rejectsMissingOrBlankName() {
        assertBadRequest(() -> service.create(new CreateRequest(null, List.of(1L))));
        assertBadRequest(() -> service.create(new CreateRequest("  ", List.of(1L))));
        verifyNoInteractions(students, contexts);
    }

    @Test
    void rejectsMissingOrEmptyStudentList() {
        assertBadRequest(() -> service.create(new CreateRequest("Projeto", null)));
        assertBadRequest(() -> service.create(new CreateRequest("Projeto", List.of())));
        verifyNoInteractions(students, contexts);
    }

    @Test
    void rejectsNullStudentId() {
        assertBadRequest(() -> service.create(
                new CreateRequest("Projeto", Arrays.asList(1L, null))));
        verifyNoInteractions(students, contexts);
    }

    @Test
    void rejectsRepeatedStudentIdsBeforeReadingDatabase() {
        assertBadRequest(() -> service.create(
                new CreateRequest("Projeto", List.of(1L, 2L, 1L))));
        verifyNoInteractions(students, contexts);
    }

    @Test
    void rejectsWhenOneOfTheStudentsDoesNotExist() {
        when(students.findAllById(any())).thenReturn(List.of(student(1L, "Ana")));

        assertBadRequest(() -> service.create(
                new CreateRequest("Projeto", List.of(1L, 2L))));

        verify(contexts, never()).save(any());
    }

    @Test
    void createsContextWithTrimmedNameAndAllSelectedStudents() {
        Estudante ana = student(8L, "Ana");
        Estudante bruno = student(3L, "Bruno");
        when(students.findAllById(any())).thenReturn(List.of(ana, bruno));
        when(contexts.save(any())).thenAnswer(invocation -> {
            GroupingContext context = invocation.getArgument(0);
            ReflectionTestUtils.setField(context, "id", 42L);
            return context;
        });

        var response = service.create(
                new CreateRequest("  Projeto QS  ", List.of(8L, 3L)));

        assertEquals(42L, response.id());
        assertEquals("Projeto QS", response.name());
        assertEquals(List.of(3L, 8L), response.studentIds());

        ArgumentCaptor<GroupingContext> captor =
                ArgumentCaptor.forClass(GroupingContext.class);
        verify(contexts).save(captor.capture());
        assertEquals("Projeto QS", captor.getValue().getName());
        assertEquals(2, captor.getValue().getStudents().size());
        assertTrue(captor.getValue().getStudents().containsAll(List.of(ana, bruno)));
    }

    @Test
    void listsContextsWithSortedStudentIds() {
        GroupingContext first = context(10L, "Projeto A",
                student(9L, "Ana"), student(2L, "Bruno"));
        GroupingContext second = context(11L, "Projeto B",
                student(4L, "Carla"));
        when(contexts.findAll()).thenReturn(List.of(first, second));

        var response = service.list();

        assertEquals(2, response.size());
        assertEquals(10L, response.get(0).id());
        assertEquals(List.of(2L, 9L), response.get(0).studentIds());
        assertEquals("Projeto B", response.get(1).name());
        assertEquals(List.of(4L), response.get(1).studentIds());
    }

    @Test
    void returnsEmptyListWhenNoContextsExist() {
        when(contexts.findAll()).thenReturn(List.of());

        assertTrue(service.list().isEmpty());
    }

    @Test
    void getsExistingContext() {
        GroupingContext context = context(15L, "Laboratório",
                student(7L, "Ana"), student(1L, "Bruno"));
        when(contexts.findById(15L)).thenReturn(Optional.of(context));

        var response = service.get(15L);

        assertEquals(15L, response.id());
        assertEquals("Laboratório", response.name());
        assertEquals(List.of(1L, 7L), response.studentIds());
    }

    @Test
    void returnsNotFoundForUnknownContext() {
        when(contexts.findById(99L)).thenReturn(Optional.empty());

        ResponseStatusException error = assertThrows(
                ResponseStatusException.class, () -> service.get(99L));

        assertEquals(HttpStatus.NOT_FOUND, error.getStatusCode());
    }

    private void assertBadRequest(ThrowingAction action) {
        ResponseStatusException error = assertThrows(
                ResponseStatusException.class, action::run);
        assertEquals(HttpStatus.BAD_REQUEST, error.getStatusCode());
    }

    private Estudante student(Long id, String name) {
        Estudante student = new Estudante(
                name, name.toLowerCase() + id + "@upt.pt", "pass",
                "E" + id, "EI");
        ReflectionTestUtils.setField(student, "id", id);
        return student;
    }

    private GroupingContext context(Long id, String name, Estudante... students) {
        GroupingContext context = new GroupingContext(
                name, new LinkedHashSet<>(List.of(students)));
        ReflectionTestUtils.setField(context, "id", id);
        return context;
    }

    @FunctionalInterface
    private interface ThrowingAction {
        void run();
    }
}
