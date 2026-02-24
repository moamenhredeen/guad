package app.guad.web.mapper;

import app.guad.entity.Attachment;
import app.guad.web.viewmodel.AttachmentDetailsViewModel;
import app.guad.web.viewmodel.DeleteAttachmentViewModel;
import app.guad.web.viewmodel.GetAttachmentViewModel;

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

