package app.guad.feature.profile.api;

import app.guad.feature.profile.UserProfile;
import java.time.DayOfWeek;
import java.time.Instant;

public record ProfileResponse(
    String email,
    String displayName,
    String timezone,
    DayOfWeek defaultReviewDay,
    boolean energyTrackingEnabled,
    boolean emailDigestsEnabled,
    boolean reminderNotificationsEnabled,
    Instant createdAt,
    Instant updatedAt
) {
    public static ProfileResponse from(UserProfile profile) {
        return new ProfileResponse(
            profile.getEmail(),
            profile.getDisplayName(),
            profile.getTimezone(),
            profile.getDefaultReviewDay(),
            profile.isEnergyTrackingEnabled(),
            profile.isEmailDigestsEnabled(),
            profile.isReminderNotificationsEnabled(),
            profile.getAudit().getCreatedAt(),
            profile.getAudit().getUpdatedAt()
        );
    }
}
