package pt.upt.studymatch.studymatchbackend.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.Sort;
import pt.upt.studymatch.studymatchbackend.model.Estudante;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

// Runs against an in-memory H2 database, so no MySQL instance is required
@DataJpaTest
public class StudentSearchRepositoryTest {

    private static final Sort SORT_BY_NAME = Sort.by(Sort.Direction.ASC, "nome");

    @Autowired
    private EstudanteRepository studentRepository;

    @BeforeEach
    public void setUp() {
        studentRepository.save(new Estudante("Ana Costa", "ana@upt.pt", "password1", "3000101", "Gestão"));
        studentRepository.save(new Estudante("Bruno Silva", "bruno@upt.pt", "password1", "3000202", "Engenharia Informática"));
        studentRepository.save(new Estudante("Carla Mendes", "carla@upt.pt", "password1", "3000303", "Engenharia Informática"));
    }

    @Test
    public void testSearchWithoutFiltersReturnsAllStudentsSorted() {
        List<Estudante> result = studentRepository.searchStudents(null, null, SORT_BY_NAME);

        assertEquals(List.of("Ana Costa", "Bruno Silva", "Carla Mendes"), names(result));
    }

    @Test
    public void testSearchByPartialNameIsCaseInsensitive() {
        List<Estudante> result = studentRepository.searchStudents("SILVA", null, SORT_BY_NAME);

        assertEquals(List.of("Bruno Silva"), names(result));
    }

    @Test
    public void testSearchByStudentNumber() {
        List<Estudante> result = studentRepository.searchStudents("3000303", null, SORT_BY_NAME);

        assertEquals(List.of("Carla Mendes"), names(result));
    }

    @Test
    public void testFilterByCourse() {
        List<Estudante> result = studentRepository.searchStudents(null, "Engenharia Informática", SORT_BY_NAME);

        assertEquals(List.of("Bruno Silva", "Carla Mendes"), names(result));
    }

    @Test
    public void testSearchAndCourseFilterAreCombined() {
        List<Estudante> result = studentRepository.searchStudents("ana", "Engenharia Informática", SORT_BY_NAME);

        assertTrue(result.isEmpty());
    }

    @Test
    public void testSortByNameDescending() {
        List<Estudante> result = studentRepository.searchStudents(null, null, Sort.by(Sort.Direction.DESC, "nome"));

        assertEquals(List.of("Carla Mendes", "Bruno Silva", "Ana Costa"), names(result));
    }

    @Test
    public void testFindDistinctCoursesIsSortedAndUnique() {
        List<String> courses = studentRepository.findDistinctCourses();

        assertEquals(List.of("Engenharia Informática", "Gestão"), courses);
    }

    private static List<String> names(List<Estudante> students) {
        return students.stream().map(Estudante::getNome).toList();
    }
}
