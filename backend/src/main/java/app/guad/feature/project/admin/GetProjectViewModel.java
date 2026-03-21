package app.guad.feature.project.admin;

public record GetProjectViewModel(
        Long id,
        String name,
        String description,
        String status,
        String areaName
) {
}
