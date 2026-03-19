package app.guad.feature.waitingfor.api;

import app.guad.BaseIntegrationTest;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class WaitingForRestControllerTest extends BaseIntegrationTest {

    @Test
    void createWaitingForItem_returns201() throws Exception {
        mockMvc.perform(post("/api/waiting-for")
                .with(userJwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"title": "Waiting for plumber", "delegatedTo": "Bob"}
                    """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.title").value("Waiting for plumber"))
            .andExpect(jsonPath("$.id").isNumber())
            .andExpect(jsonPath("$.status").value("WAITING"));
    }

    @Test
    void listWaitingForItems_returnsOnlyUserItems() throws Exception {
        mockMvc.perform(post("/api/waiting-for")
                .with(userJwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\": \"My item\"}"))
            .andExpect(status().isCreated());

        var otherUserId = UUID.randomUUID();
        mockMvc.perform(post("/api/waiting-for")
                .with(userJwt(otherUserId))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\": \"Other item\"}"))
            .andExpect(status().isCreated());

        mockMvc.perform(get("/api/waiting-for").with(userJwt()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$[?(@.title == 'My item')]").exists())
            .andExpect(jsonPath("$[?(@.title == 'Other item')]").doesNotExist());
    }

    @Test
    void getWaitingForItem_byId_returns200() throws Exception {
        var result = mockMvc.perform(post("/api/waiting-for")
                .with(userJwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\": \"Fetch me\"}"))
            .andExpect(status().isCreated())
            .andReturn();

        Number id = JsonPath.read(result.getResponse().getContentAsString(), "$.id");

        mockMvc.perform(get("/api/waiting-for/" + id).with(userJwt()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value("Fetch me"));
    }

    @Test
    void getWaitingForItem_otherUser_returns404() throws Exception {
        var result = mockMvc.perform(post("/api/waiting-for")
                .with(userJwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\": \"Private item\"}"))
            .andExpect(status().isCreated())
            .andReturn();

        Number id = JsonPath.read(result.getResponse().getContentAsString(), "$.id");

        mockMvc.perform(get("/api/waiting-for/" + id).with(userJwt(UUID.randomUUID())))
            .andExpect(status().isNotFound());
    }

    @Test
    void deleteWaitingForItem_returns204() throws Exception {
        var result = mockMvc.perform(post("/api/waiting-for")
                .with(userJwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\": \"Delete me\"}"))
            .andExpect(status().isCreated())
            .andReturn();

        Number id = JsonPath.read(result.getResponse().getContentAsString(), "$.id");

        mockMvc.perform(delete("/api/waiting-for/" + id).with(userJwt()))
            .andExpect(status().isNoContent());
    }

    @Test
    void invalidInput_returns400() throws Exception {
        mockMvc.perform(post("/api/waiting-for")
                .with(userJwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\": \"\"}"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void resolveWaitingForItem_returns200() throws Exception {
        var result = mockMvc.perform(post("/api/waiting-for")
                .with(userJwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\": \"Resolve me\"}"))
            .andExpect(status().isCreated())
            .andReturn();

        Number id = JsonPath.read(result.getResponse().getContentAsString(), "$.id");

        mockMvc.perform(patch("/api/waiting-for/" + id + "/resolve").with(userJwt()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("RESOLVED"));
    }

    @Test
    void resolvedItemsNotListedAsWaiting() throws Exception {
        var result = mockMvc.perform(post("/api/waiting-for")
                .with(userJwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\": \"Will be resolved\"}"))
            .andExpect(status().isCreated())
            .andReturn();

        Number id = JsonPath.read(result.getResponse().getContentAsString(), "$.id");

        mockMvc.perform(patch("/api/waiting-for/" + id + "/resolve").with(userJwt()))
            .andExpect(status().isOk());

        mockMvc.perform(get("/api/waiting-for").with(userJwt()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[?(@.title == 'Will be resolved')]").doesNotExist());
    }

    @Test
    void unauthenticated_returns401() throws Exception {
        mockMvc.perform(get("/api/waiting-for"))
            .andExpect(status().isUnauthorized());
    }
}
