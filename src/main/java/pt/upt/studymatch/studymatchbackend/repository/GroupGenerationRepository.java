package pt.upt.studymatch.studymatchbackend.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import pt.upt.studymatch.studymatchbackend.model.GroupGeneration;

public interface GroupGenerationRepository
        extends JpaRepository<GroupGeneration, Long> {
    List<GroupGeneration>
        findByContextIdOrderByGeneratedAtDescIdDesc(Long contextId);
}