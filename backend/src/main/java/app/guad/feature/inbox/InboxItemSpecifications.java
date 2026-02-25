package app.guad.feature.inbox;

import org.springframework.data.jpa.domain.Specification;

public class InboxItemSpecifications {
    public static Specification<InboxItem> byTitle(String title) {
        if (title == null) return Specification.unrestricted();
        return (root, _, cb) ->  cb.like(root.get("title"), title + "%");
    }

    public static Specification<InboxItem> byStatus(InboxItemStatus status) {
        if (status == null) return Specification.unrestricted();
        return (root, _, cb) -> cb.equal(root.get("status"), status);
    }
}
