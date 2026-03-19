package app.guad.feature.action.api;

import java.time.LocalDateTime;
import java.util.List;

public record UpdateActionRequest(
    String description, String notes, Long projectId, Long areaId,
    List<Long> contextIds, Integer energyLevel, Integer estimatedDuration,
    LocalDateTime dueDate, LocalDateTime scheduledDate
) {}
