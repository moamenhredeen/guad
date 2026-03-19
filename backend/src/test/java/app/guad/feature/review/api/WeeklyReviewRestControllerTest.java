package app.guad.feature.review.api;

import app.guad.BaseIntegrationTest;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.test.annotation.DirtiesContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class WeeklyReviewRestControllerTest extends BaseIntegrationTest {

    @Test
    void startReview_returns201() throws Exception {
        mockMvc.perform(post("/api/reviews").with(userJwt()))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").isNumber())
            .andExpect(jsonPath("$.currentStep").value("CLEAR_INBOX"));
    }

    @Test
    void getCurrentReview_whenActive_returns200() throws Exception {
        mockMvc.perform(post("/api/reviews").with(userJwt()))
            .andExpect(status().isCreated());

        mockMvc.perform(get("/api/reviews/current").with(userJwt()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.currentStep").exists());
    }

    @Test
    void getCurrentReview_whenNone_returns204() throws Exception {
        mockMvc.perform(get("/api/reviews/current").with(userJwt()))
            .andExpect(status().isNoContent());
    }

    @Test
    void advanceStep_returns200() throws Exception {
        var result = mockMvc.perform(post("/api/reviews").with(userJwt()))
            .andExpect(status().isCreated())
            .andReturn();

        Number id = JsonPath.read(result.getResponse().getContentAsString(), "$.id");

        mockMvc.perform(patch("/api/reviews/" + id + "/step").with(userJwt()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.currentStep").value("REVIEW_NEXT_ACTIONS"));
    }

    @Test
    void completeReview_returns200() throws Exception {
        var result = mockMvc.perform(post("/api/reviews").with(userJwt()))
            .andExpect(status().isCreated())
            .andReturn();

        Number id = JsonPath.read(result.getResponse().getContentAsString(), "$.id");

        mockMvc.perform(post("/api/reviews/" + id + "/complete").with(userJwt()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.completedAt").isNotEmpty());
    }

    @Test
    void getLastReview_whenNone_returns204() throws Exception {
        mockMvc.perform(get("/api/reviews/last").with(userJwt()))
            .andExpect(status().isNoContent());
    }

    @Test
    void getLastReview_afterComplete_returns200() throws Exception {
        var result = mockMvc.perform(post("/api/reviews").with(userJwt()))
            .andExpect(status().isCreated())
            .andReturn();

        Number id = JsonPath.read(result.getResponse().getContentAsString(), "$.id");

        mockMvc.perform(post("/api/reviews/" + id + "/complete").with(userJwt()))
            .andExpect(status().isOk());

        mockMvc.perform(get("/api/reviews/last").with(userJwt()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.completedAt").isNotEmpty());
    }

    @Test
    void unauthenticated_returns401() throws Exception {
        mockMvc.perform(get("/api/reviews/current"))
            .andExpect(status().isUnauthorized());
    }
}
