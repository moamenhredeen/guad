package app.guad.feature.profile;

public record DeleteUserProfileViewModel(
        Long id,
        String email,
        String displayName
) {
}
