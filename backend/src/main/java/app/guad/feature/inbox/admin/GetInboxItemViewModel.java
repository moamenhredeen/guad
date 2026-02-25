package app.guad.feature.inbox.admin;

import app.guad.feature.inbox.InboxItemStatus;

record GetInboxItemViewModel(
        Long id,
        String title,
        String description,
        InboxItemStatus status
) {
}


