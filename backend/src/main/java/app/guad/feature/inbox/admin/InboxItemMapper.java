package app.guad.feature.inbox.admin;

import app.guad.feature.inbox.InboxItem;

final class InboxItemMapper {
    private InboxItemMapper(){}

    public static GetInboxItemViewModel toGetInboxItemViewModel(InboxItem inboxItem) {
        return new GetInboxItemViewModel(
                inboxItem.getId(),
                inboxItem.getTitle(),
                inboxItem.getDescription(),
                inboxItem.getStatus()
        );
    }

    public static InboxItemDetailsViewModel toInboxItemDetailsViewModel(InboxItem inboxItem) {
        return new InboxItemDetailsViewModel(
                inboxItem.getId(),
                inboxItem.getTitle(),
                inboxItem.getDescription(),
                inboxItem.getStatus(),
                inboxItem.getAudit().getCreatedAt(),
                inboxItem.getAudit().getUpdatedAt(),
                inboxItem.getProcessedDate(),
                null
        );
    }

    public static DeleteInboxItemViewModel toDeleteInboxItemViewModel(InboxItem inboxItem) {
        return new DeleteInboxItemViewModel(inboxItem.getId(), inboxItem.getTitle());
    }
}

