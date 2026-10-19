package pt.upt.studymatch.studymatchbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pt.upt.studymatch.studymatchbackend.model.ColdStartProfile;
import java.util.Optional;

public interface ColdStartProfileRepository extends JpaRepository<ColdStartProfile, Long> {
    Optional<ColdStartProfile> findByEstudanteId(Long estudanteId);
}
