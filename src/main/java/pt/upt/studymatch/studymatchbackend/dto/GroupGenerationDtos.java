package pt.upt.studymatch.studymatchbackend.dto;

import java.time.LocalDateTime;
import java.util.List;

public final class GroupGenerationDtos {
    private GroupGenerationDtos() {}

    public record GenerateRequest(Integer groupSize) {}

    public record StudentResponse(
        Long id,
        String name,
        String studentNumber,
        String course
    ) {}

    public record GroupResponse(
        int number,
        List<StudentResponse> members
    ) {}

    public record GenerationResponse(
        Long id,
        Long contextId,
        String contextName,
        LocalDateTime generatedAt,
        List<GroupResponse> groups
    ) {}
}