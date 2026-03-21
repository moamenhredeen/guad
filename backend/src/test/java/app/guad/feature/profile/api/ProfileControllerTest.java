package app.guad.feature.profile.api;

import app.guad.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ProfileControllerTest extends BaseIntegrationTest {

    @Test
    void getProfile_returnsProfileWithDefaults() throws Exception {
        mockMvc.perform(get("/api/profile").with(userJwt()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.email").value("test@example.com"))
            .andExpect(jsonPath("$.displayName").value("testuser"))
            .andExpect(jsonPath("$.timezone").value("Europe/Berlin"))
            .andExpect(jsonPath("$.defaultReviewDay").value("SATURDAY"))
            .andExpect(jsonPath("$.energyTrackingEnabled").value(true))
            .andExpect(jsonPath("$.emailDigestsEnabled").value(false))
            .andExpect(jsonPath("$.reminderNotificationsEnabled").value(true))
            .andExpect(jsonPath("$.id").doesNotExist());
    }

    @Test
    void updateSettings_updatesPreferences() throws Exception {
        mockMvc.perform(get("/api/profile").with(userJwt()))
            .andExpect(status().isOk());

        mockMvc.perform(put("/api/profile/settings")
                .with(userJwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "timezone": "America/New_York",
                        "defaultReviewDay": "SUNDAY",
                        "energyTrackingEnabled": false,
                        "emailDigestsEnabled": true,
                        "reminderNotificationsEnabled": false
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.timezone").value("America/New_York"))
            .andExpect(jsonPath("$.defaultReviewDay").value("SUNDAY"))
            .andExpect(jsonPath("$.energyTrackingEnabled").value(false))
            .andExpect(jsonPath("$.emailDigestsEnabled").value(true))
            .andExpect(jsonPath("$.reminderNotificationsEnabled").value(false));
    }

    @Test
    void updateSettings_invalidTimezone_returns400() throws Exception {
        mockMvc.perform(get("/api/profile").with(userJwt()))
            .andExpect(status().isOk());

        mockMvc.perform(put("/api/profile/settings")
                .with(userJwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "timezone": "Not/A/Timezone",
                        "defaultReviewDay": "SATURDAY",
                        "energyTrackingEnabled": true,
                        "emailDigestsEnabled": false,
                        "reminderNotificationsEnabled": true
                    }
                    """))
            .andExpect(status().isBadRequest());
    }

    @Test
    void unauthenticated_returns401() throws Exception {
        mockMvc.perform(get("/api/profile"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void settingsAreIsolatedPerUser() throws Exception {
        // User A updates settings
        mockMvc.perform(get("/api/profile").with(userJwt()))
            .andExpect(status().isOk());
        mockMvc.perform(put("/api/profile/settings")
                .with(userJwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "timezone": "Asia/Tokyo",
                        "defaultReviewDay": "MONDAY",
                        "energyTrackingEnabled": false,
                        "emailDigestsEnabled": true,
                        "reminderNotificationsEnabled": false
                    }
                    """))
            .andExpect(status().isOk());

        // User B should still have defaults
        var otherUserId = UUID.randomUUID();
        mockMvc.perform(get("/api/profile").with(userJwt(otherUserId)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.timezone").value("Europe/Berlin"))
            .andExpect(jsonPath("$.defaultReviewDay").value("SATURDAY"))
            .andExpect(jsonPath("$.energyTrackingEnabled").value(true));
    }
}
