package app.guad.feature.document;

import app.guad.feature.attachment.AttachmentListItemViewModel;

import java.util.List;

public record DocumentDetailsViewModel(
        Long id,
        String name,
        String content,
        List<AttachmentListItemViewModel> attachments
) {
}

