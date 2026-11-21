package pt.upt.studymatch.studymatchbackend.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;
import pt.upt.studymatch.studymatchbackend.model.Estudante;
import pt.upt.studymatch.studymatchbackend.model.NivelCompetencia;

/**
 * Estratégia por omissão: ordena os estudantes pelo nível médio de
 * competências e distribui-os em "serpentina" para equilibrar os grupos.
 * Nenhum grupo excede groupSize e os tamanhos diferem no máximo em 1.
 */
@Component
public class BalancedCompetencyStrategy implements GroupMatchingStrategy {

    @Override
    public List<List<Estudante>> formGroups(
            List<Estudante> students,
            Map<Long, List<NivelCompetencia>> competencyLevels,
            int groupSize) {
        if (students.isEmpty()) {
            return List.of();
        }

        List<Estudante> ordered = new ArrayList<>(students);
        ordered.sort(Comparator
                .comparingDouble((Estudante s) ->
                        averageLevel(competencyLevels.get(s.getId())))
                .reversed()
                .thenComparing(Estudante::getId));

        int groupCount = (ordered.size() + groupSize - 1) / groupSize;
        List<List<Estudante>> groups = new ArrayList<>();
        for (int i = 0; i < groupCount; i++) {
            groups.add(new ArrayList<>());
        }

        for (int i = 0; i < ordered.size(); i++) {
            int round = i / groupCount;
            int position = i % groupCount;
            int index = round % 2 == 0 ? position : groupCount - 1 - position;
            groups.get(index).add(ordered.get(i));
        }
        return groups;
    }

    private double averageLevel(List<NivelCompetencia> levels) {
        if (levels == null || levels.isEmpty()) {
            return 0;
        }
        return levels.stream()
                .mapToInt(NivelCompetencia::getNivel)
                .average()
                .orElse(0);
    }
}
