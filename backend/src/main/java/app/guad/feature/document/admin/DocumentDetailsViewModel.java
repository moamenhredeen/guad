package app.guad.feature.document.admin;

import app.guad.feature.attachment.AttachmentListItemViewModel;

import java.util.List;

record DocumentDetailsViewModel(
        Long id,
        String name,
        String content,
        List<AttachmentListItemViewModel> attachments
) {
}
