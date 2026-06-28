package app.guad.feature.inbox.admin;

import app.guad.feature.attachment.AttachmentListItemViewModel;
import app.guad.feature.inbox.CaptureStatus;

import java.time.Instant;
import java.util.List;

record CaptureDetailsViewModel(
        Long id,
        String title,
        String description,
        CaptureStatus status,
        Instant createdDate,
        Instant updatedDate,
        Instant processedDate,
        List<AttachmentListItemViewModel> attachments
) {
}

