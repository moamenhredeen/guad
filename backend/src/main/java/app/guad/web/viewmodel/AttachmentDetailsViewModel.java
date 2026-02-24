package app.guad.web.viewmodel;

import java.time.Instant;

public record AttachmentDetailsViewModel(
        Long id,
        String filename,
        Long fileSize,
        String mimeType,
        String downloadUrl,
        Instant uploadedDate
) {
}

