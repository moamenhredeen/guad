package app.guad.feature.action.api;

import app.guad.feature.action.Action;
import app.guad.feature.context.api.ContextResponse;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

public record ActionResponse(
    Long id, String description, String notes, String status,
    Integer energyLevel, Integer estimatedDuration,
    LocalDateTime dueDate, LocalDateTime scheduledDate,
    String projectName, Long projectId, String areaName, Long areaId,
    List<ContextResponse> contexts,
    Instant createdDate, Instant completedDate
) {
    public static ActionResponse from(Action action) {
        return new ActionResponse(
            action.getId(), action.getDescription(), action.getNotes(),
            action.getStatus().name(),
            action.getEnergyLevel(), action.getEstimatedDuration(),
            action.getDueDate(), action.getScheduledDate(),
            action.getProject() != null ? action.getProject().getName() : null,
            action.getProject() != null ? action.getProject().getId() : null,
            action.getArea() != null ? action.getArea().getName() : null,
            action.getArea() != null ? action.getArea().getId() : null,
            action.getContexts() != null
                ? action.getContexts().stream().map(ContextResponse::from).toList()
                : List.of(),
            action.getAudit().getCreatedAt(), action.getCompletedDate()
        );
    }
}
