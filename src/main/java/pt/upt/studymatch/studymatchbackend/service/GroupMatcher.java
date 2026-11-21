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

        Set<Long> competencyIds = new TreeSet<>();
        for (Candidate candidate : candidates) {
            competencyIds.addAll(candidate.levels().keySet());
        }

        List<Candidate> ordered = new ArrayList<>(candidates);
        ordered.sort(
            Comparator.comparingDouble(
                (Candidate candidate) ->
                    average(candidate, competencyIds)
            ).reversed().thenComparing(Candidate::studentId)
        );

        List<List<Candidate>> groups = new ArrayList<>();
        for (int ignored : sizes) {
            groups.add(new ArrayList<>());
        }

        for (Candidate candidate : ordered) {
            int chosen = -1;
            double best = Double.NEGATIVE_INFINITY;

            for (int i = 0; i < groups.size(); i++) {
                List<Candidate> group = groups.get(i);
                if (group.size() >= sizes.get(i)) {
                    continue;
                }

                double score = group.isEmpty()
                    ? 1_000_000 - i
                    : 10 * complementarity(
                        group, candidate, competencyIds
                    ) - groupStrength(group, competencyIds);

                if (score > best) {
                    best = score;
                    chosen = i;
                }
            }

            groups.get(chosen).add(candidate);
        }

        return groups.stream()
            .map(group -> group.stream()
                .map(Candidate::studentId)
                .toList())
            .toList();
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

    private double average(
            Candidate candidate,
            Set<Long> competencyIds) {
        if (competencyIds.isEmpty()) {
            return 3;
        }

        return competencyIds.stream()
            .mapToInt(id -> candidate.levels().getOrDefault(id, 3))
            .average()
            .orElse(3);
    }

    private double groupStrength(
            List<Candidate> group,
            Set<Long> competencyIds) {
        return group.stream()
            .mapToDouble(c -> average(c, competencyIds))
            .sum();
    }

    private double complementarity(
            List<Candidate> group,
            Candidate candidate,
            Set<Long> competencyIds) {
        double gain = 0;
        for (Long id : competencyIds) {
            int previousBest = group.stream()
                .mapToInt(c -> c.levels().getOrDefault(id, 3))
                .max()
                .orElse(3);
            int candidateLevel =
                candidate.levels().getOrDefault(id, 3);
            gain += Math.max(0, candidateLevel - previousBest);
        }
        return gain;
    }
}
