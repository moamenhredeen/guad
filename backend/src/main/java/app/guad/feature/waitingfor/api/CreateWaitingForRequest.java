package app.guad.feature.waitingfor.api;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

public record CreateWaitingForRequest(
    @NotBlank String title, String delegatedTo, LocalDateTime delegatedAt,
    LocalDateTime followUpDate, String notes, Long actionId, Long projectId
) {}
