package app.guad.feature.project.api;

import jakarta.validation.constraints.NotBlank;

public record CreateProjectRequest(
    @NotBlank String name, String description, String desiredOutcome,
    Long areaId, String color
) {}
