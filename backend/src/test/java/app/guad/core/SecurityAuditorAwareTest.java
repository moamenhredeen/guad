package app.guad.core;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.*;

class SecurityAuditorAwareTest {

    private final SecurityAuditorAware auditorAware = new SecurityAuditorAware();

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void returnsUsername_whenAuthenticated() {
        var auth = new TestingAuthenticationToken("user-123", null);
        auth.setAuthenticated(true);
        SecurityContextHolder.getContext().setAuthentication(auth);

        var auditor = auditorAware.getCurrentAuditor();
        assertTrue(auditor.isPresent());
        assertEquals("user-123", auditor.get());
    }

    @Test
    void returnsEmpty_whenNoAuthentication() {
        var auditor = auditorAware.getCurrentAuditor();
        assertTrue(auditor.isEmpty());
    }

    @Test
    void returnsEmpty_whenAnonymous() {
        var auth = new TestingAuthenticationToken("anonymousUser", null);
        auth.setAuthenticated(false);
        SecurityContextHolder.getContext().setAuthentication(auth);

        var auditor = auditorAware.getCurrentAuditor();
        assertTrue(auditor.isEmpty());
    }
}
