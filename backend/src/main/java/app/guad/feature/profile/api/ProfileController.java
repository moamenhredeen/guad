package app.guad.feature.profile.api;

import app.guad.feature.profile.ProfileService;
import app.guad.security.AuthenticatedUser;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.time.ZoneId;

@RestController
@RequestMapping("/api/profile")
class ProfileController {

    private final ProfileService profileService;

    ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping
    ResponseEntity<ProfileResponse> getProfile(@AuthenticationPrincipal Jwt jwt) {
        var user = AuthenticatedUser.from(jwt);
        var profile = profileService.getProfileByKeycloakId(user.id());
        if (profile == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ProfileResponse.from(profile));
    }

    @PutMapping("/settings")
    ResponseEntity<ProfileResponse> updateSettings(
            @Valid @RequestBody UpdateSettingsRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        // Validate timezone
        try {
            ZoneId.of(request.timezone());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }

        var user = AuthenticatedUser.from(jwt);
        var profile = profileService.updateSettings(
            user.id(),
            request.timezone(),
            request.defaultReviewDay(),
            request.energyTrackingEnabled(),
            request.emailDigestsEnabled(),
            request.reminderNotificationsEnabled()
        );
        return ResponseEntity.ok(ProfileResponse.from(profile));
    }
}
