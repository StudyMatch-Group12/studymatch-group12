package pt.upt.studymatch.studymatchbackend.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import pt.upt.studymatch.studymatchbackend.model.Competency;
import pt.upt.studymatch.studymatchbackend.model.CompetencyLevel;
import pt.upt.studymatch.studymatchbackend.model.Estudante;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

// Runs against an in-memory H2 database, so no MySQL instance is required
@DataJpaTest
public class CompetencyLevelRepositoryTest {

    @Autowired
    private EstudanteRepository studentRepository;
    @Autowired
    private CompetencyRepository competencyRepository;
    @Autowired
    private CompetencyLevelRepository competencyLevelRepository;

    @Test
    public void testFindByStudentReturnsOnlyThatStudentLevelsSortedByCompetency() {
        Estudante ana = studentRepository.save(new Estudante("Ana Costa", "ana@upt.pt", "password1", "3000101", "Gestão"));
        Estudante bruno = studentRepository.save(new Estudante("Bruno Silva", "bruno@upt.pt", "password1", "3000202", "Gestão"));
        Competency teamwork = competencyRepository.save(new Competency("Teamwork", null));
        Competency databases = competencyRepository.save(new Competency("Databases", null));

        competencyLevelRepository.save(new CompetencyLevel(ana, teamwork, 70, "SELF_ASSESSMENT"));
        competencyLevelRepository.save(new CompetencyLevel(ana, databases, 40, "SELF_ASSESSMENT"));
        competencyLevelRepository.save(new CompetencyLevel(bruno, teamwork, 90, "SELF_ASSESSMENT"));

        List<CompetencyLevel> levels = competencyLevelRepository.findByStudent_IdOrderByCompetency_NameAsc(ana.getId());

        assertEquals(List.of("Databases", "Teamwork"), levels.stream().map(level -> level.getCompetency().getName()).toList());
        assertEquals(List.of(40, 70), levels.stream().map(CompetencyLevel::getLevel).toList());
    }

    @Test
    public void testFindCompetencyByNameIgnoresCase() {
        competencyRepository.save(new Competency("Programming", null));

        assertEquals("Programming", competencyRepository.findByNameIgnoreCase("programming").orElseThrow().getName());
    }
}
