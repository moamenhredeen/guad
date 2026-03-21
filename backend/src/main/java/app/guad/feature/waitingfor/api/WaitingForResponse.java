package app.guad.feature.waitingfor.api;

import app.guad.feature.waitingfor.WaitingForItem;
import java.time.Instant;

public record WaitingForResponse(
    Long id, String title, String delegatedTo, Instant createdDate,
    String notes, String status, String projectName, Long projectId
) {
    public static WaitingForResponse from(WaitingForItem item) {
        return new WaitingForResponse(
            item.getId(), item.getTitle(), item.getDelegatedTo(), item.getAudit().getCreatedAt(),
            item.getNotes(), item.getStatus().name(),
            item.getProject() != null ? item.getProject().getName() : null,
            item.getProject() != null ? item.getProject().getId() : null
        );
    }
}
