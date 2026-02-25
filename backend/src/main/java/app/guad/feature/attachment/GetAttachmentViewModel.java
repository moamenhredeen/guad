package app.guad.feature.attachment;

public record GetAttachmentViewModel(
        Long id,
        String filename,
        Long fileSize,
        String mimeType
) {
}

