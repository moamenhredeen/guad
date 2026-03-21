package app.guad.feature.attachment.admin;

import app.guad.feature.attachment.Attachment;

final class AttachmentMapper {
    private AttachmentMapper() {}

    static GetAttachmentViewModel toGetAttachmentViewModel(Attachment attachment) {
        return new GetAttachmentViewModel(
                attachment.getId(),
                attachment.getFilename(),
                attachment.getFileSize(),
                attachment.getMimeType()
        );
    }

    static AttachmentDetailsViewModel toAttachmentDetailsViewModel(Attachment attachment) {
        return new AttachmentDetailsViewModel(
                attachment.getId(),
                attachment.getFilename(),
                attachment.getFileSize(),
                attachment.getMimeType(),
                attachment.getFileUrl(),
                attachment.getAudit().getCreatedAt()
        );
    }

    static DeleteAttachmentViewModel toDeleteAttachmentViewModel(Attachment attachment) {
        return new DeleteAttachmentViewModel(attachment.getId(), attachment.getFilename());
    }
}
