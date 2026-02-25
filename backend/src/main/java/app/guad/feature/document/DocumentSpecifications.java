package app.guad.feature.document;

import org.springframework.data.jpa.domain.Specification;

public class DocumentSpecifications {
    public static Specification<Document> byName(String name) {
        if (name == null) return Specification.unrestricted();
        return (root, _, cb) ->  cb.like(root.get("name"), name + "%");
    }
}
