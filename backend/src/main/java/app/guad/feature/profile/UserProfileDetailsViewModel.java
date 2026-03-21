package app.guad.feature.profile;

import java.time.DayOfWeek;
import java.time.Instant;
import java.util.UUID;

public record UserProfileDetailsViewModel(
        Long id,
        UUID keycloakId,
        String email,
        String displayName,
        String timezone,
        DayOfWeek defaultReviewDay,
        boolean energyTrackingEnabled,
        boolean emailDigestsEnabled,
        boolean reminderNotificationsEnabled,
        Instant createdDate,
        Instant updatedDate
) {
}
