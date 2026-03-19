package app.guad.security;

import org.springframework.security.oauth2.jwt.Jwt;
import java.util.UUID;

public record AuthenticatedUser(UUID id, String preferredUsername, String email) {
    public static AuthenticatedUser from(Jwt jwt) {
        return new AuthenticatedUser(
            UUID.fromString(jwt.getSubject()),
            jwt.getClaimAsString("preferred_username"),
            jwt.getClaimAsString("email")
        );
    }
}
