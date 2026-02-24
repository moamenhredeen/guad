package app.guad.web.viewmodel;

public record GetActionViewModel(
        Long id,
        String description,
        String status,
        String areaName,
        String projectName
) {
}

