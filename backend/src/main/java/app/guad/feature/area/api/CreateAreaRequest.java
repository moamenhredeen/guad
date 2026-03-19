package app.guad.feature.area.api;

import jakarta.validation.constraints.NotBlank;

public record CreateAreaRequest(@NotBlank String name, String description) {}
