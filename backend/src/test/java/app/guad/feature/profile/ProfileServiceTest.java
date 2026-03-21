package app.guad.feature.profile;

import app.guad.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.time.DayOfWeek;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ProfileServiceTest extends BaseIntegrationTest {

    @Autowired
    private ProfileService profileService;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Test
    void getOrCreateProfile_createsNewProfileOnFirstCall() {
        var keycloakId = UUID.randomUUID();
        var profile = profileService.getOrCreateProfile(keycloakId, "user@example.com", "testuser");

        assertThat(profile.getKeycloakId()).isEqualTo(keycloakId);
        assertThat(profile.getEmail()).isEqualTo("user@example.com");
        assertThat(profile.getDisplayName()).isEqualTo("testuser");
        assertThat(profile.getTimezone()).isEqualTo("Europe/Berlin");
        assertThat(profile.getDefaultReviewDay()).isEqualTo(DayOfWeek.SATURDAY);
        assertThat(profile.isEnergyTrackingEnabled()).isTrue();
        assertThat(profile.isEmailDigestsEnabled()).isFalse();
        assertThat(profile.isReminderNotificationsEnabled()).isTrue();
        assertThat(profile.getCreatedDate()).isNotNull();
    }

    @Test
    void getOrCreateProfile_returnsExistingProfileOnSubsequentCalls() {
        var keycloakId = UUID.randomUUID();
        long countBefore = userProfileRepository.count();
        var first = profileService.getOrCreateProfile(keycloakId, "user@example.com", "testuser");
        var second = profileService.getOrCreateProfile(keycloakId, "user@example.com", "testuser");

        assertThat(first.getId()).isEqualTo(second.getId());
        assertThat(userProfileRepository.count()).isEqualTo(countBefore + 1);
    }

    @Test
    void getOrCreateProfile_updatesCachedFieldsWhenChanged() {
        var keycloakId = UUID.randomUUID();
        profileService.getOrCreateProfile(keycloakId, "old@example.com", "olduser");
        var updated = profileService.getOrCreateProfile(keycloakId, "new@example.com", "newuser");

        assertThat(updated.getEmail()).isEqualTo("new@example.com");
        assertThat(updated.getDisplayName()).isEqualTo("newuser");
    }
}
