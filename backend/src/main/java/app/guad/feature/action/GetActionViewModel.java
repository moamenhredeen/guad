package app.guad.feature.action;

public record GetActionViewModel(
        Long id,
        String description,
        String status,
        String areaName,
        String projectName
) {
}

