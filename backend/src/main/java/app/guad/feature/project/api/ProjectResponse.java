package app.guad.feature.project.api;

import app.guad.feature.project.Project;
import java.time.Instant;

public record ProjectResponse(
    Long id, String name, String description, String desiredOutcome,
    String status, String areaName, Long areaId, String color,
    int nextActionCount, Instant createdDate
) {
    public static ProjectResponse from(Project project, int nextActionCount) {
        return new ProjectResponse(
            project.getId(), project.getName(), project.getDescription(),
            project.getDesired_outcome(), project.getStatus().name(),
            project.getArea() != null ? project.getArea().getName() : null,
            project.getArea() != null ? project.getArea().getId() : null,
            project.getColor(), nextActionCount,
            project.getAudit().getCreatedAt()
        );
    }
}
