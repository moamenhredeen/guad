package app.guad.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Value("${cors.allowed-origins:http://localhost:5173}")
    private List<String> allowedOrigins;

    @Bean
    SecurityFilterChain adminFilterChain(
            HttpSecurity http,
            ClientRegistrationRepository clientRegistrationRepository,
            KeycloakGrantedAuthoritiesMapper authoritiesMapper
    ) {
        return http
                .securityMatcher("/admin/**", "/oauth2/**", "/login/oauth2/**")
                .authorizeHttpRequests(authZ -> authZ
                        .requestMatchers("/*.css", "/*.woff2", "/admin/auth/login*", "/oauth2/**", "/login/oauth2/**")
                        .permitAll()
                        .anyRequest()
                        .hasRole("ADMIN"))
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/admin/auth/login")
                        .defaultSuccessUrl("/admin/dashboard", true)
                        .userInfoEndpoint(userInfo -> userInfo
                                .userAuthoritiesMapper(authoritiesMapper)))
                .logout(logout -> logout
                        .logoutUrl("/admin/auth/logout")
                        .logoutSuccessHandler(oidcLogoutSuccessHandler(clientRegistrationRepository)))
                .build();
    }

    @Bean
    SecurityFilterChain apiFilterChain(
            HttpSecurity http,
            Oauth2AccessDeniedHandler oauth2AccessDeniedHandler,
            Oauth2AuthenticationEntryPoint oauth2AuthenticationEntryPoint
    ) {
        return http
                .securityMatcher("/api/**")
                .authorizeHttpRequests(authZ ->
                        authZ.anyRequest().authenticated())
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(Customizer.withDefaults())
                        .authenticationEntryPoint(oauth2AuthenticationEntryPoint)
                        .accessDeniedHandler(oauth2AccessDeniedHandler))
                .build();
    }

    private CorsConfigurationSource corsConfigurationSource() {
        var config = new CorsConfiguration();
        config.setAllowedOrigins(allowedOrigins);
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);
        return source;
    }

    private LogoutSuccessHandler oidcLogoutSuccessHandler(ClientRegistrationRepository clientRegistrationRepository) {
        OidcClientInitiatedLogoutSuccessHandler oidcLogoutSuccessHandler =
                new OidcClientInitiatedLogoutSuccessHandler(clientRegistrationRepository);
        oidcLogoutSuccessHandler.setPostLogoutRedirectUri("{baseUrl}/admin/auth/login");
        return oidcLogoutSuccessHandler;
    }
}
