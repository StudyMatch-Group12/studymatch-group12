package pt.upt.studymatch.studymatchbackend.service;

import java.util.*;
import org.springframework.stereotype.Service;

@Service
public class GroupMatcher {
    public record Candidate(Long studentId, Map<Long, Integer> levels) {
        public Candidate {
            Objects.requireNonNull(studentId);
            Objects.requireNonNull(levels);
            levels = Map.copyOf(levels);
        }
    }

    public List<List<Long>> generate(List<Candidate> candidates) {
        if (candidates == null || candidates.size() < 4) {
            throw new IllegalArgumentException(
                "São necessários pelo menos 4 estudantes"
            );
        }

        long uniqueStudents = candidates.stream()
            .map(Candidate::studentId)
            .distinct()
            .count();

        if (uniqueStudents != candidates.size()) {
            throw new IllegalArgumentException("Estudantes repetidos");
        }

        throw new UnsupportedOperationException(
            "Distribuição ainda não implementada"
        );
    }
}
