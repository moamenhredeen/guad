package app.guad.feature.profile.admin;

import java.time.Instant;

public record GetUserProfileViewModel(
        Long id,
        String email,
        String displayName,
        String timezone,
        Instant createdAt
) {
}
