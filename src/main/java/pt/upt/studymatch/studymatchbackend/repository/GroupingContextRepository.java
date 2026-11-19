package pt.upt.studymatch.studymatchbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pt.upt.studymatch.studymatchbackend.model.GroupingContext;

public interface GroupingContextRepository
        extends JpaRepository<GroupingContext, Long> {
}