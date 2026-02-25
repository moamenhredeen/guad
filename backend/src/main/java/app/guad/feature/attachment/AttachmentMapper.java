package app.guad.feature.attachment;

public final class AttachmentMapper {
    private AttachmentMapper(){}

    public static GetAttachmentViewModel toGetAttachmentViewModel(Attachment attachment) {
        return new GetAttachmentViewModel(
                attachment.getId(),
                attachment.getFilename(),
                attachment.getFileSize(),
                attachment.getMimeType()
        );
    }

    public static AttachmentDetailsViewModel toAttachmentDetailsViewModel(Attachment attachment) {
        return new AttachmentDetailsViewModel(
                attachment.getId(),
                attachment.getFilename(),
                attachment.getFileSize(),
                attachment.getMimeType(),
                attachment.getFileUrl(),
                attachment.getUploadedDate()
        );
    }

    public static DeleteAttachmentViewModel toDeleteAttachmentViewModel(Attachment attachment) {
        return new DeleteAttachmentViewModel(attachment.getId(), attachment.getFilename());
    }
}

