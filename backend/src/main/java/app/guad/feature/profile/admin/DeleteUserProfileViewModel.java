package app.guad.feature.profile.admin;

public record DeleteUserProfileViewModel(
        Long id,
        String email,
        String displayName
) {
}
