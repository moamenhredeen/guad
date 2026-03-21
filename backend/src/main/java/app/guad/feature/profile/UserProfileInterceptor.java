package app.guad.feature.profile;

import app.guad.security.AuthenticatedUser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class UserProfileInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(UserProfileInterceptor.class);
    private final ProfileService profileService;

    public UserProfileInterceptor(ProfileService profileService) {
        this.profileService = profileService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof JwtAuthenticationToken jwtAuth)) {
            return true;
        }

        try {
            var user = AuthenticatedUser.from((Jwt) jwtAuth.getPrincipal());
            var profile = profileService.getOrCreateProfile(
                user.id(), user.email(), user.preferredUsername());
            request.setAttribute("userProfile", profile);
        } catch (Exception e) {
            log.warn("Failed to create/load user profile", e);
        }

        return true;
    }
}
