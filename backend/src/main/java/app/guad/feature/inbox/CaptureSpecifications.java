package app.guad.feature.inbox;

import org.springframework.data.jpa.domain.Specification;

class CaptureSpecifications {
    public static Specification<Capture> byTitle(String title) {
        if (title == null) return Specification.unrestricted();
        return (root, _, cb) ->  cb.like(root.get("title"), title + "%");
    }

    public static Specification<Capture> byStatus(CaptureStatus status) {
        if (status == null) return Specification.unrestricted();
        return (root, _, cb) -> cb.equal(root.get("status"), status);
    }
}
