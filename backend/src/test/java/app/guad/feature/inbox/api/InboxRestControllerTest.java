package app.guad.feature.inbox.api;

import app.guad.BaseIntegrationTest;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class InboxRestControllerTest extends BaseIntegrationTest {

    @Test
    void createInboxItem_returns201() throws Exception {
        mockMvc.perform(post("/api/inbox")
                .with(userJwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"title": "Buy groceries", "description": "Milk, eggs, bread"}
                    """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.title").value("Buy groceries"))
            .andExpect(jsonPath("$.id").isNumber())
            .andExpect(jsonPath("$.status").value("UNPROCESSED"));
    }

    @Test
    void listInboxItems_returnsOnlyUserItems() throws Exception {
        mockMvc.perform(post("/api/inbox")
                .with(userJwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\": \"My item\"}"))
            .andExpect(status().isCreated());

        var otherUserId = UUID.randomUUID();
        mockMvc.perform(post("/api/inbox")
                .with(userJwt(otherUserId))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\": \"Other item\"}"))
            .andExpect(status().isCreated());

        mockMvc.perform(get("/api/inbox").with(userJwt()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$[?(@.title == 'My item')]").exists())
            .andExpect(jsonPath("$[?(@.title == 'Other item')]").doesNotExist());
    }

    @Test
    void getInboxItem_byId_returns200() throws Exception {
        var result = mockMvc.perform(post("/api/inbox")
                .with(userJwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\": \"Pick item\"}"))
            .andExpect(status().isCreated())
            .andReturn();

        Number id = JsonPath.read(result.getResponse().getContentAsString(), "$.id");

        mockMvc.perform(get("/api/inbox/" + id).with(userJwt()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value("Pick item"));
    }

    @Test
    void getInboxItem_otherUser_returns404() throws Exception {
        var result = mockMvc.perform(post("/api/inbox")
                .with(userJwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\": \"Private item\"}"))
            .andExpect(status().isCreated())
            .andReturn();

        Number id = JsonPath.read(result.getResponse().getContentAsString(), "$.id");

        mockMvc.perform(get("/api/inbox/" + id).with(userJwt(UUID.randomUUID())))
            .andExpect(status().isNotFound());
    }

    @Test
    void deleteInboxItem_returns204() throws Exception {
        var result = mockMvc.perform(post("/api/inbox")
                .with(userJwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\": \"To delete\"}"))
            .andExpect(status().isCreated())
            .andReturn();

        Number id = JsonPath.read(result.getResponse().getContentAsString(), "$.id");

        mockMvc.perform(delete("/api/inbox/" + id).with(userJwt()))
            .andExpect(status().isNoContent());
    }

    @Test
    void invalidInput_returns400() throws Exception {
        mockMvc.perform(post("/api/inbox")
                .with(userJwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\": \"\"}"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void processInboxItem_asTrash_returns200() throws Exception {
        var result = mockMvc.perform(post("/api/inbox")
                .with(userJwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\": \"Trash me\"}"))
            .andExpect(status().isCreated())
            .andReturn();

        Number id = JsonPath.read(result.getResponse().getContentAsString(), "$.id");

        mockMvc.perform(post("/api/inbox/" + id + "/process")
                .with(userJwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"action\": \"TRASH\"}"))
            .andExpect(status().isOk());
    }

    @Test
    void processInboxItem_asNextAction_returns200() throws Exception {
        var result = mockMvc.perform(post("/api/inbox")
                .with(userJwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\": \"Do something\"}"))
            .andExpect(status().isCreated())
            .andReturn();

        Number id = JsonPath.read(result.getResponse().getContentAsString(), "$.id");

        mockMvc.perform(post("/api/inbox/" + id + "/process")
                .with(userJwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"action\": \"NEXT_ACTION\"}"))
            .andExpect(status().isOk());
    }

    @Test
    void unauthenticated_returns401() throws Exception {
        mockMvc.perform(get("/api/inbox"))
            .andExpect(status().isUnauthorized());
    }
}
