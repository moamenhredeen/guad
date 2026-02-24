package app.guad.web.viewmodel;

import app.guad.entity.InboxItemStatus;

import java.time.Instant;
import java.util.List;

public record InboxItemDetailsViewModel(
        Long id,
        String title,
        String description,
        InboxItemStatus status,
        Instant createdDate,
        Instant updatedDate,
        Instant processedDate,
        List<AttachmentListItemViewModel> attachments
) {
}

