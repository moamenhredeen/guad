package app.guad.feature.inbox.api;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public record ProcessInboxItemRequest(
    @NotNull ProcessAction action,
    Long projectId,
    String delegatedTo,
    List<Long> contextIds
) {
    public enum ProcessAction {
        NEXT_ACTION, PROJECT, WAITING_FOR, SOMEDAY_MAYBE, REFERENCE, TRASH
    }
}
