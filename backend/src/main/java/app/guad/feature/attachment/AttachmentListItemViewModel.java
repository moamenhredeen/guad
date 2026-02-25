package app.guad.feature.attachment;

import java.time.Instant;

public record AttachmentListItemViewModel(
        Long id,
        String filename,
        Long fileSize,
        String mimeType,
        String downloadUrl,
        Instant uploadedDate
) {
}


