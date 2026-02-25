package app.guad.feature.auth;

import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Controller
@RequestMapping("/admin/auth")
public class AuthAdminController {

    private final ClientRegistrationRepository clientRegistrationRepository;

    public AuthAdminController(ClientRegistrationRepository clientRegistrationRepository) {
        this.clientRegistrationRepository = clientRegistrationRepository;
    }

    @GetMapping("/login")
    public String login(Model model){
        List<Map<String, String>> providers = new ArrayList<>();

        if (clientRegistrationRepository instanceof InMemoryClientRegistrationRepository registrations) {
            registrations.forEach(registration -> {
                Map<String, String> provider = new HashMap<>();
                provider.put("name", registration.getClientName());
                provider.put("url", "/oauth2/authorization/" + registration.getRegistrationId());
                providers.add(provider);
            });
        }

        model.addAttribute("oauth2Providers", providers);
        return "admin/login";
    }


    @GetMapping("/logout")
    public String logout(){
        return "admin/logout";
    }
}
