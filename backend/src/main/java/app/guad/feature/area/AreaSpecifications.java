package app.guad.feature.area;

import org.springframework.data.jpa.domain.Specification;

class AreaSpecifications {
    public static Specification<Area> byName(String name) {
        if (name == null) return Specification.unrestricted();
        return (root, _, cb) ->  cb.like(root.get("name"), name + "%");
    }

    public static Specification<Area> byUser(Long userId) {
        if (userId == null) return Specification.unrestricted();
        return (root, _, cb) ->  cb.equal(root.get("user_id"), userId);
    }
}
