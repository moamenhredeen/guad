package app.guad.feature.attachment.admin;

record GetAttachmentViewModel(
        Long id,
        String filename,
        Long fileSize,
        String mimeType
) {
}
