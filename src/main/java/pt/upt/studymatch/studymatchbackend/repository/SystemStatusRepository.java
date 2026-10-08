package pt.upt.studymatch.studymatchbackend.repository;

import pt.upt.studymatch.studymatchbackend.model.SystemStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SystemStatusRepository extends JpaRepository<SystemStatus, Long> {
}