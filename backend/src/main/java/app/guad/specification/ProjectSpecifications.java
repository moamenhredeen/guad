package app.guad.specification;

import app.guad.entity.Action;
import app.guad.entity.Project;
import app.guad.entity.ProjectStatus;
import org.springframework.data.jpa.domain.Specification;

public class ProjectSpecifications {

    public static Specification<Project> byName(String name) {
        if (name == null) return Specification.unrestricted();
        return (root, _, cb) ->  cb.like(root.get("name"), name + "%");
    }

    public static Specification<Project> byArea(Long areaId) {
        if (areaId == null) return Specification.unrestricted();
        return (root, _, cb) ->  cb.equal(root.get("area_id"),  areaId);
    }

    public static Specification<Project> byStatus(ProjectStatus status) {
        if (status == null) return Specification.unrestricted();
        return (root, _, cb) ->  cb.equal(root.get("status"), status);
    }
}
