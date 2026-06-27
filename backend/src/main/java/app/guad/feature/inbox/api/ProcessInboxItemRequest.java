package app.guad.feature.inbox.api;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

public record ProcessInboxItemRequest(
    @NotNull ProcessAction action,
    String description,
    String notes,
    Long projectId,
    Long areaId,
    String delegatedTo,
    List<Long> contextIds,
    Integer energyLevel,
    Integer estimatedDuration,
    LocalDateTime dueDate,
    LocalDateTime scheduledDate
) {
    public enum ProcessAction {
        NEXT_ACTION, PROJECT, WAITING_FOR, SOMEDAY_MAYBE, REFERENCE, TRASH
    }
}
