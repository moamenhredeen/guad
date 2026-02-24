package app.guad.specification;

import app.guad.entity.Context;
import org.springframework.data.jpa.domain.Specification;

public class ContextSpecifications {
    public static Specification<Context> byName(String name) {
        if (name == null) return Specification.unrestricted();
        return (root, _, cb) ->  cb.like(root.get("name"), name + "%");
    }
}
