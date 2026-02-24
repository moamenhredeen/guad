package app.guad.web.viewmodel;

import java.util.List;

public record DocumentDetailsViewModel(
        Long id,
        String name,
        String content,
        List<AttachmentListItemViewModel> attachments
) {
}

