package app.guad.feature.profile;

public final class UserProfileMapper {
    private UserProfileMapper() {}

    public static GetUserProfileViewModel toGetUserProfileViewModel(UserProfile profile) {
        return new GetUserProfileViewModel(
                profile.getId(),
                profile.getEmail(),
                profile.getDisplayName(),
                profile.getTimezone(),
                profile.getCreatedDate()
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
                profile.getCreatedDate(),
                profile.getUpdatedDate()
        );
    }

    public static DeleteUserProfileViewModel toDeleteUserProfileViewModel(UserProfile profile) {
        return new DeleteUserProfileViewModel(profile.getId(), profile.getEmail(), profile.getDisplayName());
    }
}
