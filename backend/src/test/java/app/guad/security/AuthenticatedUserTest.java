package app.guad.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AuthenticatedUserTest {

    @Test
    void from_returnsAuthenticatedUser_whenSubjectIsUuid() {
        var userId = UUID.randomUUID();
        var jwt = jwtWithSubject(userId.toString());

        var user = AuthenticatedUser.from(jwt);

        assertEquals(userId, user.id());
        assertEquals("testuser", user.preferredUsername());
        assertEquals("test@example.com", user.email());
    }

    @Test
    void from_throwsInvalidAuthenticatedUserException_whenSubjectIsMissing() {
        var jwt = jwtWithSubject(null);

        assertThrows(InvalidAuthenticatedUserException.class, () -> AuthenticatedUser.from(jwt));
    }

    @Test
    void from_throwsInvalidAuthenticatedUserException_whenSubjectIsNotUuid() {
        var jwt = jwtWithSubject("not-a-uuid");

        assertThrows(InvalidAuthenticatedUserException.class, () -> AuthenticatedUser.from(jwt));
    }

    private Jwt jwtWithSubject(String subject) {
        var claims = new java.util.HashMap<String, Object>();
        if (subject != null) {
            claims.put("sub", subject);
        }
        claims.put("preferred_username", "testuser");
        claims.put("email", "test@example.com");

        return new Jwt(
            "token",
            Instant.now(),
            Instant.now().plusSeconds(60),
            Map.of("alg", "none"),
            claims
        );
    }
}
