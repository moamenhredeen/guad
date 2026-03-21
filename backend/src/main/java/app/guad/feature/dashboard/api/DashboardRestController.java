package app.guad.feature.dashboard.api;

import app.guad.core.ApiResponse;
import app.guad.feature.dashboard.DashboardService;
import app.guad.security.AuthenticatedUser;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
class DashboardRestController {

    private final DashboardService dashboardService;

    DashboardRestController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping
    ApiResponse<DashboardResponse> get(@AuthenticationPrincipal Jwt jwt) {
        var userId = AuthenticatedUser.from(jwt).id();
        return ApiResponse.of(dashboardService.getDashboard(userId));
    }
}
