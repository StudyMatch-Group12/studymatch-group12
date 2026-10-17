package pt.upt.studymatch.studymatchbackend.dto;

// Request body for PUT /api/estudantes/{studentId}/competencies
public record CompetencyLevelRequestDTO(String competency, Integer level, String source) {
}
