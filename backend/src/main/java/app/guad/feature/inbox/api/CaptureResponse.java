package app.guad.feature.inbox.api;

import app.guad.feature.inbox.Capture;

import java.time.Instant;

public record CaptureResponse(Long id, String title, String description, String status, Instant createdAt) {
    public static CaptureResponse from(Capture item) {
        return new CaptureResponse(item.getId(), item.getTitle(), item.getDescription(),
            item.getStatus().name(), item.getAudit().getCreatedAt());
    }
}
