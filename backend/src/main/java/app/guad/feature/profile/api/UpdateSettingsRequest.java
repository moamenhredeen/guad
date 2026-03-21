package app.guad.feature.profile.api;

import jakarta.validation.constraints.NotNull;
import java.time.DayOfWeek;

public record UpdateSettingsRequest(
    @NotNull String timezone,
    @NotNull DayOfWeek defaultReviewDay,
    @NotNull Boolean energyTrackingEnabled,
    @NotNull Boolean emailDigestsEnabled,
    @NotNull Boolean reminderNotificationsEnabled
) {}
