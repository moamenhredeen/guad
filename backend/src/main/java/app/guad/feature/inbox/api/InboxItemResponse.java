package app.guad.feature.inbox.api;

import app.guad.feature.inbox.InboxItem;

import java.time.Instant;

public record InboxItemResponse(Long id, String title, String description, String status, Instant createdAt) {
    public static InboxItemResponse from(InboxItem item) {
        return new InboxItemResponse(item.getId(), item.getTitle(), item.getDescription(),
            item.getStatus().name(), item.getAudit().getCreatedAt());
    }
}
