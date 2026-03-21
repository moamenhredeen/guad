package app.guad.feature.project;

import org.springframework.data.jpa.domain.Specification;

class ProjectSpecifications {

    public static Specification<Project> byName(String name) {
        if (name == null) return Specification.unrestricted();
        return (root, _, cb) ->  cb.like(root.get("name"), name + "%");
    }

    public static Specification<Project> byStatus(ProjectStatus status) {
        if (status == null) return Specification.unrestricted();
        return (root, _, cb) ->  cb.equal(root.get("status"), status);
    }
}
