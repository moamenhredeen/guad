package app.guad.security;

import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
@NullMarked
public class KeycloakGrantedAuthoritiesMapper implements GrantedAuthoritiesMapper {

    @Override
    public Collection<? extends GrantedAuthority> mapAuthorities(Collection<? extends GrantedAuthority> authorities) {
        Set<GrantedAuthority> mapped = new HashSet<>(authorities);

        for (GrantedAuthority authority : authorities) {
            if (authority instanceof OidcUserAuthority oidc) {
                mapped.addAll(extractRealmRoles(oidc.getIdToken().getClaims()));
            }
        }

        return mapped;
    }

    private Collection<GrantedAuthority> extractRealmRoles(Map<String, Object> claims) {
        List<GrantedAuthority> roles = List.of();

        var realmAccess = claims.get("realm_access");
        if (realmAccess instanceof Map<?, ?> realmAccessMap) {
            var realmRoles = realmAccessMap.get("roles");
            if (realmRoles instanceof List<?> roleList) {
                roles = roleList.stream()
                        .filter(String.class::isInstance)
                        .map(role -> (GrantedAuthority) new SimpleGrantedAuthority("ROLE_" + ((String) role).toUpperCase()))
                        .toList();
            }
        }

        return roles;
    }
}
