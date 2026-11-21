package pt.upt.studymatch.studymatchbackend.dto;

import java.util.List;

public final class GroupingContextDtos {
    private GroupingContextDtos() {}

    public record CreateRequest(String name, List<Long> studentIds) {}

    public record ContextResponse(
            Long id,
            String name,
            List<Long> studentIds
    ) {}
}