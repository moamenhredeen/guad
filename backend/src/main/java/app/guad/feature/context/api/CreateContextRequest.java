package app.guad.feature.context.api;

import jakarta.validation.constraints.NotBlank;

public record CreateContextRequest(@NotBlank String name, String description, String color, String iconKey) {}
