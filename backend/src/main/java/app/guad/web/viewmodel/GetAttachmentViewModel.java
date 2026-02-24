package app.guad.web.viewmodel;

public record GetAttachmentViewModel(
        Long id,
        String filename,
        Long fileSize,
        String mimeType
) {
}

