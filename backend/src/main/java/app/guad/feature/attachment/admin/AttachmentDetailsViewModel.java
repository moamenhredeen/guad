package app.guad.feature.attachment.admin;

import java.time.Instant;

record AttachmentDetailsViewModel(
        Long id,
        String filename,
        Long fileSize,
        String mimeType,
        String downloadUrl,
        Instant uploadedDate
) {
}
