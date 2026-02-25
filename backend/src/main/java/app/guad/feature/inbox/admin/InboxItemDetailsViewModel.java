package app.guad.feature.inbox.admin;

import app.guad.feature.attachment.AttachmentListItemViewModel;
import app.guad.feature.inbox.InboxItemStatus;

import java.time.Instant;
import java.util.List;

record InboxItemDetailsViewModel(
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

