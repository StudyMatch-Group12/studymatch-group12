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

        List<Integer> sizes = groupSizes(candidates.size());

        throw new UnsupportedOperationException(
            "Distribuição ainda não implementada (" + sizes.size()
                + " grupos previstos)"
        );
    }

    private List<Integer> groupSizes(int count) {
        // Encontra 5b + 4a = count.
        for (int fives = count / 5; fives >= 0; fives--) {
            int remaining = count - 5 * fives;
            if (remaining % 4 == 0) {
                List<Integer> sizes = new ArrayList<>();
                for (int i = 0; i < fives; i++) {
                    sizes.add(5);
                }
                for (int i = 0; i < remaining / 4; i++) {
                    sizes.add(4);
                }
                return sizes;
            }
        }
        throw new IllegalArgumentException(
            "Não é possível formar apenas grupos de 4 ou 5"
        );
    }
}
