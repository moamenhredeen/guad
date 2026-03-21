package app.guad.feature.profile;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ProfileWebConfig implements WebMvcConfigurer {

    private final UserProfileInterceptor userProfileInterceptor;

    public ProfileWebConfig(UserProfileInterceptor userProfileInterceptor) {
        this.userProfileInterceptor = userProfileInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(userProfileInterceptor)
            .addPathPatterns("/api/**");
    }
}
