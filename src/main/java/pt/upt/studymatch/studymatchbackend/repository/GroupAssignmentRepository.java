package pt.upt.studymatch.studymatchbackend.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import pt.upt.studymatch.studymatchbackend.model.GroupAssignment;

public interface GroupAssignmentRepository
        extends JpaRepository<GroupAssignment, Long> {
    List<GroupAssignment>
        findByGenerationIdOrderByGroupNumberAscIdAsc(Long generationId);
}