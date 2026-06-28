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
    void createCapture_returns201() throws Exception {
        mockMvc.perform(post("/api/inbox")
                .with(userJwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"title": "Buy groceries", "description": "Milk, eggs, bread"}
                    """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.data.title").value("Buy groceries"))
            .andExpect(jsonPath("$.data.id").isNumber())
            .andExpect(jsonPath("$.data.status").value("UNPROCESSED"));
    }

    @Test
    void listCaptures_returnsOnlyUserItems() throws Exception {
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
            .andExpect(jsonPath("$.data").isArray())
            .andExpect(jsonPath("$.data[?(@.title == 'My item')]").exists())
            .andExpect(jsonPath("$.data[?(@.title == 'Other item')]").doesNotExist());
    }

    @Test
    void getCapture_byId_returns200() throws Exception {
        var result = mockMvc.perform(post("/api/inbox")
                .with(userJwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\": \"Pick item\"}"))
            .andExpect(status().isCreated())
            .andReturn();

        Number id = JsonPath.read(result.getResponse().getContentAsString(), "$.data.id");

        mockMvc.perform(get("/api/inbox/" + id).with(userJwt()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.title").value("Pick item"));
    }

    @Test
    void getCapture_otherUser_returns404() throws Exception {
        var result = mockMvc.perform(post("/api/inbox")
                .with(userJwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\": \"Private item\"}"))
            .andExpect(status().isCreated())
            .andReturn();

        Number id = JsonPath.read(result.getResponse().getContentAsString(), "$.data.id");

        mockMvc.perform(get("/api/inbox/" + id).with(userJwt(UUID.randomUUID())))
            .andExpect(status().isNotFound());
    }

    @Test
    void deleteCapture_returns204() throws Exception {
        var result = mockMvc.perform(post("/api/inbox")
                .with(userJwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\": \"To delete\"}"))
            .andExpect(status().isCreated())
            .andReturn();

        Number id = JsonPath.read(result.getResponse().getContentAsString(), "$.data.id");

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
    void processCapture_asTrash_returns200() throws Exception {
        var result = mockMvc.perform(post("/api/inbox")
                .with(userJwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\": \"Trash me\"}"))
            .andExpect(status().isCreated())
            .andReturn();

        Number id = JsonPath.read(result.getResponse().getContentAsString(), "$.data.id");

        mockMvc.perform(post("/api/inbox/" + id + "/process")
                .with(userJwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"action\": \"TRASH\"}"))
            .andExpect(status().isOk());
    }

    @Test
    void processCapture_asNextAction_returns200() throws Exception {
        var result = mockMvc.perform(post("/api/inbox")
                .with(userJwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\": \"Do something\"}"))
            .andExpect(status().isCreated())
            .andReturn();

        Number id = JsonPath.read(result.getResponse().getContentAsString(), "$.data.id");

        mockMvc.perform(post("/api/inbox/" + id + "/process")
                .with(userJwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"action\": \"NEXT_ACTION\"}"))
            .andExpect(status().isOk());
    }

    @Test
    void processCapture_asNextAction_usesClarifiedActionFields() throws Exception {
        var areaResult = mockMvc.perform(post("/api/areas")
                .with(userJwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"Work\"}"))
            .andExpect(status().isCreated())
            .andReturn();
        Number areaId = JsonPath.read(areaResult.getResponse().getContentAsString(), "$.data.id");

        var projectResult = mockMvc.perform(post("/api/projects")
                .with(userJwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"Launch\", \"areaId\": %s}".formatted(areaId)))
            .andExpect(status().isCreated())
            .andReturn();
        Number projectId = JsonPath.read(projectResult.getResponse().getContentAsString(), "$.data.id");

        var contextResult = mockMvc.perform(post("/api/contexts")
                .with(userJwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"Laptop\"}"))
            .andExpect(status().isCreated())
            .andReturn();
        Number contextId = JsonPath.read(contextResult.getResponse().getContentAsString(), "$.data.id");

        var inboxResult = mockMvc.perform(post("/api/inbox")
                .with(userJwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\": \"Launch thing\", \"description\": \"messy capture\"}"))
            .andExpect(status().isCreated())
            .andReturn();
        Number inboxId = JsonPath.read(inboxResult.getResponse().getContentAsString(), "$.data.id");

        mockMvc.perform(post("/api/inbox/" + inboxId + "/process")
                .with(userJwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "action": "NEXT_ACTION",
                      "description": "Email launch checklist to Sam",
                      "notes": "Use the latest checklist",
                      "projectId": %s,
                      "areaId": %s,
                      "contextIds": [%s],
                      "energyLevel": 2,
                      "estimatedDuration": 25
                    }
                    """.formatted(projectId, areaId, contextId)))
            .andExpect(status().isOk());

        mockMvc.perform(get("/api/actions?status=NEXT").with(userJwt()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data[0].description").value("Email launch checklist to Sam"))
            .andExpect(jsonPath("$.data[0].notes").value("Use the latest checklist"))
            .andExpect(jsonPath("$.data[0].projectId").value(projectId))
            .andExpect(jsonPath("$.data[0].areaId").value(areaId))
            .andExpect(jsonPath("$.data[0].energyLevel").value(2))
            .andExpect(jsonPath("$.data[0].estimatedDuration").value(25))
            .andExpect(jsonPath("$.data[0].contexts[0].id").value(contextId));
    }

    @Test
    void unauthenticated_returns401() throws Exception {
        mockMvc.perform(get("/api/inbox"))
            .andExpect(status().isUnauthorized());
    }
}
