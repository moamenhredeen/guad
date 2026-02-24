package app.guad.web.mapper;

import app.guad.entity.Project;
import app.guad.web.viewmodel.GetProjectViewModel;
import app.guad.web.viewmodel.ProjectDetailsViewModel;

import java.util.List;

public final class ProjectMapper {

    private ProjectMapper() {
    }

    public static GetProjectViewModel toGetProjectViewModel(Project project) {
        return new GetProjectViewModel(
                project.getId(),
                project.getName(),
                project.getDescription(),
                project.getStatus().name(),
                project.getArea().getName()
        );
    }

    public static ProjectDetailsViewModel toProjectDetailsViewModel(Project project) {
        return new ProjectDetailsViewModel(
                project.getId(),
                project.getName(),
                project.getDescription(),
                project.getDesired_outcome(),
                project.getStatus() != null ? project.getStatus().name() : null,
                project.getCreatedDate(),
                project.getUpdatedDate(),
                project.getCompletedDate(),
                project.getColor(),
                project.getArea() != null ? project.getArea().getId() : null,
                project.getArea() != null ? project.getArea().getName() : null,
                List.of());
    }

}
