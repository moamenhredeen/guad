package app.guad.feature.profile;

import app.guad.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserProfileInterceptorTest extends BaseIntegrationTest {

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Test
    void authenticatedApiRequest_createsProfileAutomatically() throws Exception {
        // Hit any existing API endpoint — interceptor should create profile
        mockMvc.perform(get("/api/areas").with(userJwt()))
            .andExpect(status().isOk());

        var profile = userProfileRepository.findByKeycloakId(testUserId);
        assertThat(profile).isPresent();
        assertThat(profile.get().getEmail()).isEqualTo("test@example.com");
        assertThat(profile.get().getDisplayName()).isEqualTo("testuser");
    }
}
