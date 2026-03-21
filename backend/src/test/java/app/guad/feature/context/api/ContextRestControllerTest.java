package app.guad.feature.context.api;

import app.guad.BaseIntegrationTest;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ContextRestControllerTest extends BaseIntegrationTest {

    @Test
    void createContext_returns201() throws Exception {
        mockMvc.perform(post("/api/contexts")
                .with(userJwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"name": "Phone", "description": "Phone calls", "color": "#ff0000"}
                    """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.data.name").value("Phone"))
            .andExpect(jsonPath("$.data.id").isNumber());
    }

    @Test
    void listContexts_returnsOnlyUserItems() throws Exception {
        mockMvc.perform(post("/api/contexts")
                .with(userJwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"Office\"}"))
            .andExpect(status().isCreated());

        var otherUserId = UUID.randomUUID();
        mockMvc.perform(post("/api/contexts")
                .with(userJwt(otherUserId))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"Other\"}"))
            .andExpect(status().isCreated());

        mockMvc.perform(get("/api/contexts").with(userJwt()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data").isArray())
            .andExpect(jsonPath("$.data[?(@.name == 'Office')]").exists())
            .andExpect(jsonPath("$.data[?(@.name == 'Other')]").doesNotExist());
    }

    @Test
    void deleteContext_returns204() throws Exception {
        var result = mockMvc.perform(post("/api/contexts")
                .with(userJwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"Temp\"}"))
            .andExpect(status().isCreated())
            .andReturn();

        Number id = JsonPath.read(result.getResponse().getContentAsString(), "$.data.id");

        mockMvc.perform(delete("/api/contexts/" + id).with(userJwt()))
            .andExpect(status().isNoContent());
    }

    @Test
    void deleteContext_otherUser_returns404() throws Exception {
        var result = mockMvc.perform(post("/api/contexts")
                .with(userJwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"Mine\"}"))
            .andExpect(status().isCreated())
            .andReturn();

        Number id = JsonPath.read(result.getResponse().getContentAsString(), "$.data.id");

        mockMvc.perform(delete("/api/contexts/" + id).with(userJwt(UUID.randomUUID())))
            .andExpect(status().isNotFound());
    }

    @Test
    void invalidInput_returns400() throws Exception {
        mockMvc.perform(post("/api/contexts")
                .with(userJwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"\"}"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void updateContext_returns200() throws Exception {
        var result = mockMvc.perform(post("/api/contexts")
                .with(userJwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"Original\"}"))
            .andExpect(status().isCreated())
            .andReturn();

        Number id = JsonPath.read(result.getResponse().getContentAsString(), "$.data.id");

        mockMvc.perform(put("/api/contexts/" + id)
                .with(userJwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"Updated\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.name").value("Updated"));
    }

    @Test
    void unauthenticated_returns401() throws Exception {
        mockMvc.perform(get("/api/contexts"))
            .andExpect(status().isUnauthorized());
    }
}
