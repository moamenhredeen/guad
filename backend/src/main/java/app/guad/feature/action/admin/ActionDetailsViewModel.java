package app.guad.feature.action.admin;

import app.guad.feature.action.Action;
import app.guad.feature.attachment.AttachmentListItemViewModel;
import app.guad.feature.context.Context;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public record ActionDetailsViewModel(
        Long id,
        String description,
        String notes,
        String status,
        Boolean isTimeSpecific,
        Integer estimatedDuration,
        Integer energyLevel,
        String location,
        Instant createdDate,
        Instant updatedDate,
        Instant completedDate,
        LocalDateTime scheduledDate,
        LocalDateTime dueDate,
        Long projectId,
        String projectName,
        Long areaId,
        String areaName,
        List<String> contextNames,
        List<AttachmentListItemViewModel> attachments
) {

    public static ActionDetailsViewModel toActionDetailsViewModel(Action action) {
        List<String> contextNames = action.getContexts() != null
                ? action.getContexts().stream()
                .map(Context::getName)
                .collect(Collectors.toList())
                : List.of();

        return new ActionDetailsViewModel(
                action.getId(),
                action.getDescription(),
                action.getNotes(),
                action.getStatus() != null ? action.getStatus().name() : null,
                action.isTimeSpecific(),
                action.getEstimatedDuration(),
                action.getEnergyLevel(),
                action.getLocation(),
                action.getAudit().getCreatedAt(),
                action.getAudit().getUpdatedAt(),
                action.getCompletedDate(),
                action.getScheduledDate(),
                action.getDueDate(),
                action.getProject() != null ? action.getProject().getId() : null,
                action.getProject() != null ? action.getProject().getName() : null,
                action.getArea() != null ? action.getArea().getId() : null,
                action.getArea() != null ? action.getArea().getName() : null,
                contextNames,
                List.of());
    }
}
