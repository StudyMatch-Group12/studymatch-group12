package pt.upt.studymatch.studymatchbackend.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pt.upt.studymatch.studymatchbackend.model.Estudante;

import java.util.List;
import java.util.Optional;

@Repository
public interface EstudanteRepository extends JpaRepository<Estudante, Long> {
    Optional<Estudante> findByNumeroEstudante(String numeroEstudante);
    Optional<Estudante> findByEmail(String email);

    // Optional filters: a null parameter means "do not filter by this field".
    // The search term matches the student's name or student number (case-insensitive).
    @Query("SELECT e FROM Estudante e "
            + "WHERE (:searchTerm IS NULL "
            + "    OR LOWER(e.nome) LIKE LOWER(CONCAT('%', :searchTerm, '%')) "
            + "    OR e.numeroEstudante LIKE CONCAT('%', :searchTerm, '%')) "
            + "AND (:course IS NULL OR e.curso = :course)")
    List<Estudante> searchStudents(@Param("searchTerm") String searchTerm,
                                   @Param("course") String course,
                                   Sort sort);

    @Query("SELECT DISTINCT e.curso FROM Estudante e ORDER BY e.curso")
    List<String> findDistinctCourses();
}
