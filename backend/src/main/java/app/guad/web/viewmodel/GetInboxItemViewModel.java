package app.guad.web.viewmodel;

import app.guad.entity.InboxItemStatus;

import java.util.List;

public record GetInboxItemViewModel(
        Long id,
        String title,
        String description,
        InboxItemStatus status
) {
}


