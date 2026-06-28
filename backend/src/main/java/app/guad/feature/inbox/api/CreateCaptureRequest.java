package app.guad.feature.inbox.api;

import jakarta.validation.constraints.NotBlank;

public record CreateCaptureRequest(@NotBlank String title, String description) {}
