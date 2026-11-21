package pt.upt.studymatch.studymatchbackend.service;

import java.util.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GroupMatcherTest {
    private final GroupMatcher matcher = new GroupMatcher();

    private List<GroupMatcher.Candidate> candidates(int count) {
        List<GroupMatcher.Candidate> result = new ArrayList<>();
        for (long id = 1; id <= count; id++) {
            result.add(new GroupMatcher.Candidate(
                id,
                Map.of(1L, (int) (id % 5) + 1)
            ));
        }
        return result;
    }

    @Test
    void distributesTwentyThreeAsFiveFiveFiveFourFour() {
        var groups = matcher.generate(candidates(23));
        assertEquals(List.of(5, 5, 5, 4, 4),
            groups.stream().map(List::size).toList());

        var allIds = groups.stream().flatMap(List::stream).toList();
        assertEquals(23, new HashSet<>(allIds).size());
    }

    @Test
    void acceptsFourFiveEightAndNineStudents() {
        for (int count : List.of(4, 5, 8, 9)) {
            var groups = matcher.generate(candidates(count));
            assertEquals(count,
                groups.stream().mapToInt(List::size).sum());
            assertTrue(groups.stream().allMatch(
                group -> group.size() == 4 || group.size() == 5
            ));
        }
    }

    @Test
    void rejectsImpossibleCounts() {
        assertThrows(IllegalArgumentException.class,
            () -> matcher.generate(candidates(6)));
        assertThrows(IllegalArgumentException.class,
            () -> matcher.generate(candidates(7)));
    }
}
