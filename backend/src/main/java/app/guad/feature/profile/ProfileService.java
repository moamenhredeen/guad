package app.guad.feature.profile;

import app.guad.core.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.util.UUID;

@Service
public class ProfileService {

    private final UserProfileRepository userProfileRepository;

    public ProfileService(UserProfileRepository userProfileRepository) {
        this.userProfileRepository = userProfileRepository;
    }

    @Transactional
    public UserProfile getOrCreateProfile(UUID keycloakId, String email, String displayName) {
        var existing = userProfileRepository.findByKeycloakId(keycloakId);
        if (existing.isPresent()) {
            var profile = existing.get();
            boolean changed = false;
            if (!profile.getEmail().equals(email)) {
                profile.setEmail(email);
                changed = true;
            }
            if (!profile.getDisplayName().equals(displayName)) {
                profile.setDisplayName(displayName);
                changed = true;
            }
            return changed ? userProfileRepository.save(profile) : profile;
        }

        var profile = new UserProfile();
        profile.setKeycloakId(keycloakId);
        profile.setEmail(email);
        profile.setDisplayName(displayName);
        return userProfileRepository.save(profile);
    }

    public UserProfile getProfileByKeycloakId(UUID keycloakId) {
        return userProfileRepository.findByKeycloakId(keycloakId).orElse(null);
    }

    @Transactional
    public UserProfile updateSettings(UUID keycloakId, String timezone, DayOfWeek defaultReviewDay,
                                       boolean energyTrackingEnabled, boolean emailDigestsEnabled,
                                       boolean reminderNotificationsEnabled) {
        var profile = userProfileRepository.findByKeycloakId(keycloakId)
            .orElseThrow(() -> new ResourceNotFoundException("Profile"));
        profile.setTimezone(timezone);
        profile.setDefaultReviewDay(defaultReviewDay);
        profile.setEnergyTrackingEnabled(energyTrackingEnabled);
        profile.setEmailDigestsEnabled(emailDigestsEnabled);
        profile.setReminderNotificationsEnabled(reminderNotificationsEnabled);
        return userProfileRepository.save(profile);
    }
}
