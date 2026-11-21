package pt.upt.studymatch.studymatchbackend.service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.LongStream;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import pt.upt.studymatch.studymatchbackend.model.Estudante;
import pt.upt.studymatch.studymatchbackend.model.NivelCompetencia;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BalancedCompetencyStrategyTest {
    private final BalancedCompetencyStrategy strategy =
            new BalancedCompetencyStrategy();

    @Test
    void placesEveryStudentExactlyOnce() {
        List<Estudante> students = students(7);

        List<List<Estudante>> groups =
                strategy.formGroups(students, Map.of(), 3);

        Set<Long> ids = new HashSet<>();
        groups.forEach(g -> g.forEach(s -> assertTrue(ids.add(s.getId()))));
        assertEquals(7, ids.size());
    }

    @Test
    void neverExceedsGroupSizeAndKeepsSizesBalanced() {
        List<List<Estudante>> groups =
                strategy.formGroups(students(7), Map.of(), 3);

        assertEquals(3, groups.size());
        groups.forEach(g -> assertTrue(g.size() <= 3));
        int min = groups.stream().mapToInt(List::size).min().orElseThrow();
        int max = groups.stream().mapToInt(List::size).max().orElseThrow();
        assertTrue(max - min <= 1);
    }

    @Test
    void spreadsStrongestStudentsAcrossGroups() {
        List<Estudante> students = students(4);
        Map<Long, List<NivelCompetencia>> levels = new HashMap<>();
        levels.put(1L, List.of(level(5)));
        levels.put(2L, List.of(level(4)));
        levels.put(3L, List.of(level(2)));
        levels.put(4L, List.of(level(1)));

        List<List<Estudante>> groups = strategy.formGroups(students, levels, 2);

        assertEquals(List.of(1L, 4L), ids(groups.get(0)));
        assertEquals(List.of(2L, 3L), ids(groups.get(1)));
    }

    @Test
    void returnsNoGroupsForNoStudents() {
        assertTrue(strategy.formGroups(List.of(), Map.of(), 3).isEmpty());
    }

    private List<Long> ids(List<Estudante> group) {
        return group.stream().map(Estudante::getId).toList();
    }

    private NivelCompetencia level(int value) {
        return new NivelCompetencia(value, "teste", null, null);
    }

    private List<Estudante> students(int count) {
        return LongStream.rangeClosed(1, count)
                .mapToObj(id -> {
                    Estudante student = new Estudante(
                            "E" + id, "e" + id + "@upt.pt", "pass",
                            "N" + id, "EI");
                    ReflectionTestUtils.setField(student, "id", id);
                    return student;
                })
                .toList();
    }
}
