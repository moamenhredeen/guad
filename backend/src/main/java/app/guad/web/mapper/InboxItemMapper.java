package app.guad.web.mapper;

import app.guad.entity.InboxItem;
import app.guad.web.viewmodel.*;

public final class InboxItemMapper {
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
                inboxItem.getCreatedDate(),
                inboxItem.getUpdatedDate(),
                inboxItem.getProcessedDate(),
                null
        );
    }

    public static DeleteInboxItemViewModel toDeleteInboxItemViewModel(InboxItem inboxItem) {
        return new DeleteInboxItemViewModel(inboxItem.getId(), inboxItem.getTitle());
    }
}

