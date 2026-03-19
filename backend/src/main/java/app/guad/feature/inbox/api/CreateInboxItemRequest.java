package app.guad.feature.inbox.api;

import jakarta.validation.constraints.NotBlank;

public record CreateInboxItemRequest(@NotBlank String title, String description) {}
