package pt.upt.studymatch.studymatchbackend.service;

import java.util.List;
import java.util.Map;
import pt.upt.studymatch.studymatchbackend.model.Estudante;
import pt.upt.studymatch.studymatchbackend.model.NivelCompetencia;

/**
 * Ponto de ligação entre a geração de grupos e o algoritmo de matching.
 * Cada estudante recebido tem de aparecer exatamente uma vez no resultado.
 */
public interface GroupMatchingStrategy {

    List<List<Estudante>> formGroups(
            List<Estudante> students,
            Map<Long, List<NivelCompetencia>> competencyLevels,
            int groupSize);
}
