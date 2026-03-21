package app.guad.feature.action.admin;

public record GetActionViewModel(
        Long id,
        String description,
        String status,
        String areaName,
        String projectName
) {
}
