package app.guad.feature.action.api;

import app.guad.BaseIntegrationTest;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ActionRestControllerTest extends BaseIntegrationTest {

    @Test
    void createAction_returns201() throws Exception {
        mockMvc.perform(post("/api/actions")
                .with(userJwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"description\": \"Call dentist\"}"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.description").value("Call dentist"))
            .andExpect(jsonPath("$.id").isNumber())
            .andExpect(jsonPath("$.status").value("NEXT"));
    }

    @Test
    void listActions_returnsOnlyUserItems() throws Exception {
        mockMvc.perform(post("/api/actions")
                .with(userJwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"description\": \"My action\"}"))
            .andExpect(status().isCreated());

        var otherUserId = UUID.randomUUID();
        mockMvc.perform(post("/api/actions")
                .with(userJwt(otherUserId))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"description\": \"Other action\"}"))
            .andExpect(status().isCreated());

        mockMvc.perform(get("/api/actions").with(userJwt()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$[?(@.description == 'My action')]").exists())
            .andExpect(jsonPath("$[?(@.description == 'Other action')]").doesNotExist());
    }

    @Test
    void getAction_byId_returns200() throws Exception {
        var result = mockMvc.perform(post("/api/actions")
                .with(userJwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"description\": \"Fetch action\"}"))
            .andExpect(status().isCreated())
            .andReturn();

        Number id = JsonPath.read(result.getResponse().getContentAsString(), "$.id");

        mockMvc.perform(get("/api/actions/" + id).with(userJwt()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.description").value("Fetch action"));
    }

    @Test
    void getAction_otherUser_returns404() throws Exception {
        var result = mockMvc.perform(post("/api/actions")
                .with(userJwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"description\": \"Private action\"}"))
            .andExpect(status().isCreated())
            .andReturn();

        Number id = JsonPath.read(result.getResponse().getContentAsString(), "$.id");

        mockMvc.perform(get("/api/actions/" + id).with(userJwt(UUID.randomUUID())))
            .andExpect(status().isNotFound());
    }

    @Test
    void deleteAction_returns204() throws Exception {
        var result = mockMvc.perform(post("/api/actions")
                .with(userJwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"description\": \"Delete me\"}"))
            .andExpect(status().isCreated())
            .andReturn();

        Number id = JsonPath.read(result.getResponse().getContentAsString(), "$.id");

        mockMvc.perform(delete("/api/actions/" + id).with(userJwt()))
            .andExpect(status().isNoContent());
    }

    @Test
    void invalidInput_returns400() throws Exception {
        mockMvc.perform(post("/api/actions")
                .with(userJwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"description\": \"\"}"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void completeAction_returns200() throws Exception {
        var result = mockMvc.perform(post("/api/actions")
                .with(userJwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"description\": \"Complete me\"}"))
            .andExpect(status().isCreated())
            .andReturn();

        Number id = JsonPath.read(result.getResponse().getContentAsString(), "$.id");

        mockMvc.perform(patch("/api/actions/" + id + "/complete").with(userJwt()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    void changeActionStatus_returns200() throws Exception {
        var result = mockMvc.perform(post("/api/actions")
                .with(userJwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"description\": \"Status change action\"}"))
            .andExpect(status().isCreated())
            .andReturn();

        Number id = JsonPath.read(result.getResponse().getContentAsString(), "$.id");

        mockMvc.perform(patch("/api/actions/" + id + "/status")
                .with(userJwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\": \"IN_PROGRESS\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
    }

    @Test
    void updateAction_returns200() throws Exception {
        var result = mockMvc.perform(post("/api/actions")
                .with(userJwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"description\": \"Original description\"}"))
            .andExpect(status().isCreated())
            .andReturn();

        Number id = JsonPath.read(result.getResponse().getContentAsString(), "$.id");

        mockMvc.perform(put("/api/actions/" + id)
                .with(userJwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"description\": \"Updated description\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.description").value("Updated description"));
    }

    @Test
    void unauthenticated_returns401() throws Exception {
        mockMvc.perform(get("/api/actions"))
            .andExpect(status().isUnauthorized());
    }
}
