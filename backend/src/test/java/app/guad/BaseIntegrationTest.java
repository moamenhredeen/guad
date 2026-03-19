package app.guad;

import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("test")
@Import(BaseIntegrationTest.TestSecurityConfig.class)
public abstract class BaseIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

    @DynamicPropertySource
    static void configure(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @TestConfiguration
    public static class TestSecurityConfig {

        @Bean
        JwtDecoder jwtDecoder() {
            // MockMvc jwt() post-processor sets the security context directly,
            // bypassing the JwtDecoder entirely
            return token -> {
                throw new UnsupportedOperationException(
                    "Real JWT decoding not used in tests - use SecurityMockMvcRequestPostProcessors.jwt()");
            };
        }

        @Bean
        ClientRegistrationRepository clientRegistrationRepository() {
            return Mockito.mock(ClientRegistrationRepository.class);
        }
    }

    @Autowired
    protected MockMvc mockMvc;

    protected UUID testUserId = UUID.randomUUID();

    protected org.springframework.test.web.servlet.request.RequestPostProcessor userJwt() {
        return jwt().jwt(builder -> builder.subject(testUserId.toString())
            .claim("preferred_username", "testuser")
            .claim("email", "test@example.com"));
    }

    protected org.springframework.test.web.servlet.request.RequestPostProcessor userJwt(UUID userId) {
        return jwt().jwt(builder -> builder.subject(userId.toString())
            .claim("preferred_username", "testuser")
            .claim("email", "test@example.com"));
    }
}
