package app.guad.feature.profile;

import app.guad.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;

import java.time.DayOfWeek;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ProfileServiceTest extends BaseIntegrationTest {

    @Autowired
    private ProfileService profileService;

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
        assertThat(profile.getAudit().getCreatedAt()).isNotNull();
    }

    @Test
    void getOrCreateProfile_returnsExistingProfileOnSubsequentCalls() {
        var keycloakId = UUID.randomUUID();
        var first = profileService.getOrCreateProfile(keycloakId, "user@example.com", "testuser");
        var second = profileService.getOrCreateProfile(keycloakId, "user@example.com", "testuser");

        assertThat(first.getId()).isEqualTo(second.getId());
    }

    @Test
    void getOrCreateProfile_updatesCachedFieldsWhenChanged() {
        var keycloakId = UUID.randomUUID();
        profileService.getOrCreateProfile(keycloakId, "old@example.com", "olduser");
        var updated = profileService.getOrCreateProfile(keycloakId, "new@example.com", "newuser");

        assertThat(updated.getEmail()).isEqualTo("new@example.com");
        assertThat(updated.getDisplayName()).isEqualTo("newuser");
    }

    @Test
    void search_returnsMatchingProfiles() {
        profileService.getOrCreateProfile(UUID.randomUUID(), "alice@example.com", "Alice");
        profileService.getOrCreateProfile(UUID.randomUUID(), "bob@example.com", "Bob");

        var result = profileService.search("alice", PageRequest.of(0, 10));
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().getEmail()).isEqualTo("alice@example.com");
    }

    @Test
    void search_withNullSearch_returnsAll() {
        profileService.getOrCreateProfile(UUID.randomUUID(), "alice@example.com", "Alice");
        profileService.getOrCreateProfile(UUID.randomUUID(), "bob@example.com", "Bob");

        var result = profileService.search(null, PageRequest.of(0, 10));
        assertThat(result.getContent()).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    void findById_returnsProfileWhenExists() {
        var profile = profileService.getOrCreateProfile(UUID.randomUUID(), "find@example.com", "FindMe");

        var found = profileService.findById(profile.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("find@example.com");
    }

    @Test
    void findById_returnsEmptyWhenNotExists() {
        var found = profileService.findById(999999L);
        assertThat(found).isEmpty();
    }

    @Test
    void deleteById_removesProfile() {
        var profile = profileService.getOrCreateProfile(UUID.randomUUID(), "delete@example.com", "DeleteMe");
        var id = profile.getId();

        profileService.deleteById(id);

        assertThat(profileService.findById(id)).isEmpty();
    }
}
