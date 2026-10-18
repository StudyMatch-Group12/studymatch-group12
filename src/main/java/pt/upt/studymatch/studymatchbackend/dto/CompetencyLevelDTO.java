package pt.upt.studymatch.studymatchbackend.dto;

// One axis of the student's competency radar: competency name, level (0-100) and its source
public record CompetencyLevelDTO(String competency, int level, String source) {
}
