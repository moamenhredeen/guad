package app.guad.feature.dashboard.admin;

import app.guad.feature.dashboard.DashboardService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

@Controller
@RequestMapping("/admin")
public class DashboardAdminController {

    private final DashboardService dashboardService;

    public DashboardAdminController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("dashboard")
    public String dashboard(OAuth2AuthenticationToken authentication, Model model) {
        var oidcUser = (OidcUser) authentication.getPrincipal();
        var userId = UUID.fromString(oidcUser.getSubject());
        var dashboard = dashboardService.getDashboard(userId);

        model.addAttribute("inboxCount", dashboard.inboxCount());
        model.addAttribute("nextActionsCount", dashboard.nextActionsCount());
        model.addAttribute("activeProjectsCount", dashboard.activeProjectsCount());
        model.addAttribute("waitingForCount", dashboard.waitingForCount());
        model.addAttribute("somedayMaybeCount", dashboard.somedayMaybeActionsCount());
        model.addAttribute("weeklyReviewDue", dashboard.weeklyReviewDue());

        return "admin/dashboard";
    }
}
