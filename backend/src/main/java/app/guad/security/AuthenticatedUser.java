package app.guad.security;

import org.springframework.security.oauth2.jwt.Jwt;
import java.util.UUID;

public record AuthenticatedUser(UUID id, String preferredUsername, String email) {
    public static AuthenticatedUser from(Jwt jwt) {
        var subject = jwt.getSubject();
        if (subject == null || subject.isBlank()) {
            throw new InvalidAuthenticatedUserException("Authenticated token is missing subject.");
        }

        final UUID userId;
        try {
            userId = UUID.fromString(subject);
        } catch (IllegalArgumentException ex) {
            throw new InvalidAuthenticatedUserException("Authenticated token subject is not a UUID.");
        }

        return new AuthenticatedUser(
            userId,
            jwt.getClaimAsString("preferred_username"),
            jwt.getClaimAsString("email")
        );
    }
}
