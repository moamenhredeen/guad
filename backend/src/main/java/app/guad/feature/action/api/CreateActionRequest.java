package app.guad.feature.action.api;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

public record CreateActionRequest(
    @NotBlank String description, String notes, Long projectId, Long areaId,
    List<Long> contextIds, Integer energyLevel, Integer estimatedDuration,
    LocalDateTime dueDate, LocalDateTime scheduledDate
) {}
