package app.guad.feature.project.api;

import app.guad.BaseIntegrationTest;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ProjectRestControllerTest extends BaseIntegrationTest {

    @Test
    void createProject_returns201() throws Exception {
        mockMvc.perform(post("/api/projects")
                .with(userJwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"name": "Home renovation", "description": "Fix the kitchen"}
                    """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.data.name").value("Home renovation"))
            .andExpect(jsonPath("$.data.id").isNumber())
            .andExpect(jsonPath("$.data.status").value("ACTIVE"));
    }

    @Test
    void listProjects_returnsOnlyUserItems() throws Exception {
        mockMvc.perform(post("/api/projects")
                .with(userJwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"My project\"}"))
            .andExpect(status().isCreated());

        var otherUserId = UUID.randomUUID();
        mockMvc.perform(post("/api/projects")
                .with(userJwt(otherUserId))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"Other project\"}"))
            .andExpect(status().isCreated());

        mockMvc.perform(get("/api/projects").with(userJwt()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data").isArray())
            .andExpect(jsonPath("$.data[?(@.name == 'My project')]").exists())
            .andExpect(jsonPath("$.data[?(@.name == 'Other project')]").doesNotExist());
    }

    @Test
    void getProjectDetail_returns200() throws Exception {
        var result = mockMvc.perform(post("/api/projects")
                .with(userJwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"Detail project\"}"))
            .andExpect(status().isCreated())
            .andReturn();

        Number id = JsonPath.read(result.getResponse().getContentAsString(), "$.data.id");

        mockMvc.perform(get("/api/projects/" + id).with(userJwt()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.name").value("Detail project"))
            .andExpect(jsonPath("$.data.nextActions").isArray())
            .andExpect(jsonPath("$.data.completedActions").isArray());
    }

    @Test
    void getProject_otherUser_returns404() throws Exception {
        var result = mockMvc.perform(post("/api/projects")
                .with(userJwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"Private project\"}"))
            .andExpect(status().isCreated())
            .andReturn();

        Number id = JsonPath.read(result.getResponse().getContentAsString(), "$.data.id");

        mockMvc.perform(get("/api/projects/" + id).with(userJwt(UUID.randomUUID())))
            .andExpect(status().isNotFound());
    }

    @Test
    void deleteProject_returns204() throws Exception {
        var result = mockMvc.perform(post("/api/projects")
                .with(userJwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"To delete\"}"))
            .andExpect(status().isCreated())
            .andReturn();

        Number id = JsonPath.read(result.getResponse().getContentAsString(), "$.data.id");

        mockMvc.perform(delete("/api/projects/" + id).with(userJwt()))
            .andExpect(status().isNoContent());
    }

    @Test
    void invalidInput_returns400() throws Exception {
        mockMvc.perform(post("/api/projects")
                .with(userJwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"\"}"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void changeProjectStatus_returns200() throws Exception {
        var result = mockMvc.perform(post("/api/projects")
                .with(userJwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"Status project\"}"))
            .andExpect(status().isCreated())
            .andReturn();

        Number id = JsonPath.read(result.getResponse().getContentAsString(), "$.data.id");

        mockMvc.perform(patch("/api/projects/" + id + "/status")
                .with(userJwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\": \"COMPLETED\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.status").value("COMPLETED"));
    }

    @Test
    void addActionToProject_returns201() throws Exception {
        var result = mockMvc.perform(post("/api/projects")
                .with(userJwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"Action project\"}"))
            .andExpect(status().isCreated())
            .andReturn();

        Number id = JsonPath.read(result.getResponse().getContentAsString(), "$.data.id");

        mockMvc.perform(post("/api/projects/" + id + "/actions")
                .with(userJwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"description\": \"First step\"}"))
            .andExpect(status().isCreated());
    }

    @Test
    void unauthenticated_returns401() throws Exception {
        mockMvc.perform(get("/api/projects"))
            .andExpect(status().isUnauthorized());
    }
}
