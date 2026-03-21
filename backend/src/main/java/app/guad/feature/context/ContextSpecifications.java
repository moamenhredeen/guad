package app.guad.feature.context;

import org.springframework.data.jpa.domain.Specification;

class ContextSpecifications {
    public static Specification<Context> byName(String name) {
        if (name == null) return Specification.unrestricted();
        return (root, _, cb) ->  cb.like(root.get("name"), name + "%");
    }
}
