package app.guad.feature.profile.admin;

import app.guad.feature.profile.UserProfile;

public final class UserProfileMapper {
    private UserProfileMapper() {}

    public static GetUserProfileViewModel toGetUserProfileViewModel(UserProfile profile) {
        return new GetUserProfileViewModel(
                profile.getId(),
                profile.getEmail(),
                profile.getDisplayName(),
                profile.getTimezone(),
                profile.getAudit().getCreatedAt()
        );
    }

    public static UserProfileDetailsViewModel toUserProfileDetailsViewModel(UserProfile profile) {
        return new UserProfileDetailsViewModel(
                profile.getId(),
                profile.getKeycloakId(),
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

    public static DeleteUserProfileViewModel toDeleteUserProfileViewModel(UserProfile profile) {
        return new DeleteUserProfileViewModel(profile.getId(), profile.getEmail(), profile.getDisplayName());
    }
}
