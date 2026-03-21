package app.guad.feature.action;

import org.springframework.data.jpa.domain.Specification;

class ActionSpecifications {
    public static Specification<Action> byDescription(String description) {
        if (description == null) return Specification.unrestricted();
        return (root, _, cb) ->  cb.like(root.get("description"), description + "%");
    }

    public static Specification<Action> byStatus(ActionStatus status) {
        if (status == null) return Specification.unrestricted();
        return (root, _, cb) -> cb.equal(root.get("status"), status);
    }
}
