package pt.upt.studymatch.studymatchbackend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.upt.studymatch.studymatchbackend.dto.CompetencyLevelDTO;
import pt.upt.studymatch.studymatchbackend.model.Competency;
import pt.upt.studymatch.studymatchbackend.model.CompetencyLevel;
import pt.upt.studymatch.studymatchbackend.model.Estudante;
import pt.upt.studymatch.studymatchbackend.repository.CompetencyLevelRepository;
import pt.upt.studymatch.studymatchbackend.repository.CompetencyRepository;
import pt.upt.studymatch.studymatchbackend.repository.EstudanteRepository;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class CompetencyService {

    private static final String DEFAULT_SOURCE = "SELF_ASSESSMENT";

    private final EstudanteRepository studentRepository;
    private final CompetencyRepository competencyRepository;
    private final CompetencyLevelRepository competencyLevelRepository;

    public CompetencyService(EstudanteRepository studentRepository,
                             CompetencyRepository competencyRepository,
                             CompetencyLevelRepository competencyLevelRepository) {
        this.studentRepository = studentRepository;
        this.competencyRepository = competencyRepository;
        this.competencyLevelRepository = competencyLevelRepository;
    }

    @Transactional(readOnly = true)
    public List<CompetencyLevelDTO> getCompetencyProfile(Long studentId) {
        if (!studentRepository.existsById(studentId)) {
            throw new NoSuchElementException("Student not found: " + studentId);
        }
        return competencyLevelRepository.findByStudent_IdOrderByCompetency_NameAsc(studentId).stream()
                .map(level -> new CompetencyLevelDTO(level.getCompetency().getName(), level.getLevel(), level.getSource()))
                .toList();
    }

    // Creates the competency if it does not exist yet, then creates or updates the student's level
    @Transactional
    public CompetencyLevelDTO setCompetencyLevel(Long studentId, String competencyName, Integer level, String source) {
        if (competencyName == null || competencyName.trim().isEmpty()) {
            throw new IllegalArgumentException("Competency name is required.");
        }
        if (level == null || level < CompetencyLevel.MIN_LEVEL || level > CompetencyLevel.MAX_LEVEL) {
            throw new IllegalArgumentException("Competency level must be between "
                    + CompetencyLevel.MIN_LEVEL + " and " + CompetencyLevel.MAX_LEVEL + ".");
        }

        Estudante student = studentRepository.findById(studentId)
                .orElseThrow(() -> new NoSuchElementException("Student not found: " + studentId));

        String name = competencyName.trim();
        Competency competency = competencyRepository.findByNameIgnoreCase(name)
                .orElseGet(() -> competencyRepository.save(new Competency(name, null)));

        String levelSource = (source == null || source.trim().isEmpty()) ? DEFAULT_SOURCE : source.trim();

        CompetencyLevel competencyLevel = competencyLevelRepository
                .findByStudent_IdAndCompetency_Id(studentId, competency.getId())
                .orElseGet(() -> new CompetencyLevel(student, competency, level, levelSource));
        competencyLevel.setLevel(level);
        competencyLevel.setSource(levelSource);

        CompetencyLevel saved = competencyLevelRepository.save(competencyLevel);
        return new CompetencyLevelDTO(saved.getCompetency().getName(), saved.getLevel(), saved.getSource());
    }
}
