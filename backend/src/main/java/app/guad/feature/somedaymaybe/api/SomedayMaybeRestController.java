package app.guad.feature.somedaymaybe.api;

import app.guad.feature.action.ActionService;
import app.guad.feature.action.ActionStatus;
import app.guad.feature.action.api.ActionResponse;
import app.guad.feature.project.ProjectRepository;
import app.guad.feature.project.ProjectStatus;
import app.guad.feature.project.api.ProjectResponse;
import app.guad.security.AuthenticatedUser;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/someday-maybe")
class SomedayMaybeRestController {

    private final ActionService actionService;
    private final ProjectRepository projectRepository;

    SomedayMaybeRestController(ActionService actionService, ProjectRepository projectRepository) {
        this.actionService = actionService;
        this.projectRepository = projectRepository;
    }

    @GetMapping
    SomedayMaybeResponse list(@AuthenticationPrincipal Jwt jwt) {
        var userId = AuthenticatedUser.from(jwt).id();
        var actions = actionService.findAllByUserIdAndStatus(userId, ActionStatus.SOMEDAY_MAYBE)
            .stream().map(ActionResponse::from).toList();
        var projects = projectRepository.findAllByUserIdAndStatus(userId, ProjectStatus.SOMEDAY_MAYBE)
            .stream().map(p -> ProjectResponse.from(p, 0)).toList();
        return new SomedayMaybeResponse(actions, projects);
    }
}
