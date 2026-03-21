package app.guad.feature.project.api;

import app.guad.core.ResourceNotFoundException;
import app.guad.feature.action.Action;
import app.guad.feature.action.ActionService;
import app.guad.feature.action.ActionStatus;
import app.guad.feature.area.AreaService;
import app.guad.feature.project.Project;
import app.guad.feature.project.ProjectService;
import app.guad.feature.project.ProjectStatus;
import app.guad.feature.waitingfor.WaitingForItemStatus;
import app.guad.feature.waitingfor.WaitingForService;
import app.guad.security.AuthenticatedUser;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/projects")
class ProjectRestController {

    private final ProjectService projectService;
    private final ActionService actionService;
    private final AreaService areaService;
    private final WaitingForService waitingForService;

    ProjectRestController(ProjectService projectService,
                           ActionService actionService,
                           AreaService areaService, WaitingForService waitingForService) {
        this.projectService = projectService;
        this.actionService = actionService;
        this.areaService = areaService;
        this.waitingForService = waitingForService;
    }

    @GetMapping
    List<ProjectResponse> list(@AuthenticationPrincipal Jwt jwt) {
        var userId = AuthenticatedUser.from(jwt).id();
        return projectService.findAllByUserIdAndStatus(userId, ProjectStatus.ACTIVE).stream()
            .map(p -> {
                int nextCount = (int) actionService.findAllByUserIdAndProjectId(userId, p.getId())
                    .stream().filter(a -> a.getStatus() == ActionStatus.NEXT).count();
                return ProjectResponse.from(p, nextCount);
            }).toList();
    }

    @PostMapping
    ResponseEntity<ProjectResponse> create(@Valid @RequestBody CreateProjectRequest request,
                                            @AuthenticationPrincipal Jwt jwt) {
        var userId = AuthenticatedUser.from(jwt).id();
        var project = new Project();
        project.setName(request.name());
        project.setDescription(request.description());
        project.setDesired_outcome(request.desiredOutcome());
        project.setStatus(ProjectStatus.ACTIVE);
        project.setColor(request.color());
        project.setUserId(userId);
        if (request.areaId() != null) {
            areaService.getAreaById(request.areaId()).ifPresent(project::setArea);
        }
        var saved = projectService.save(project);
        return ResponseEntity.created(URI.create("/api/projects/" + saved.getId()))
            .body(ProjectResponse.from(saved, 0));
    }

    @GetMapping("/{id}")
    ProjectDetailResponse get(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        var userId = AuthenticatedUser.from(jwt).id();
        var project = projectService.findByIdAndUserId(id, userId)
            .orElseThrow(() -> new ResourceNotFoundException("Project", id));
        var allActions = actionService.findAllByUserIdAndProjectId(userId, id);
        var nextActions = allActions.stream()
            .filter(a -> a.getStatus() == ActionStatus.NEXT || a.getStatus() == ActionStatus.IN_PROGRESS)
            .toList();
        var completedActions = allActions.stream()
            .filter(a -> a.getStatus() == ActionStatus.COMPLETED)
            .toList();
        var waitingFor = waitingForService.findAllByUserIdAndStatus(userId, WaitingForItemStatus.WAITING)
            .stream().filter(w -> w.getProject() != null && w.getProject().getId().equals(id))
            .toList();
        return ProjectDetailResponse.from(project, nextActions, waitingFor, completedActions);
    }

    @PutMapping("/{id}")
    ProjectResponse update(@PathVariable Long id, @Valid @RequestBody CreateProjectRequest request,
                            @AuthenticationPrincipal Jwt jwt) {
        var userId = AuthenticatedUser.from(jwt).id();
        var project = projectService.findByIdAndUserId(id, userId)
            .orElseThrow(() -> new ResourceNotFoundException("Project", id));
        project.setName(request.name());
        project.setDescription(request.description());
        project.setDesired_outcome(request.desiredOutcome());
        project.setColor(request.color());
        if (request.areaId() != null) {
            areaService.getAreaById(request.areaId()).ifPresent(project::setArea);
        }
        var saved = projectService.save(project);
        int nextCount = (int) actionService.findAllByUserIdAndProjectId(userId, id)
            .stream().filter(a -> a.getStatus() == ActionStatus.NEXT).count();
        return ProjectResponse.from(saved, nextCount);
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> delete(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        var userId = AuthenticatedUser.from(jwt).id();
        projectService.findByIdAndUserId(id, userId)
            .orElseThrow(() -> new ResourceNotFoundException("Project", id));
        projectService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/actions")
    ResponseEntity<Void> addAction(@PathVariable Long id,
                                    @Valid @RequestBody Map<String, String> body,
                                    @AuthenticationPrincipal Jwt jwt) {
        var userId = AuthenticatedUser.from(jwt).id();
        var project = projectService.findByIdAndUserId(id, userId)
            .orElseThrow(() -> new ResourceNotFoundException("Project", id));
        var action = new Action();
        action.setDescription(body.get("description"));
        action.setStatus(ActionStatus.NEXT);
        action.setProject(project);
        action.setUserId(userId);
        actionService.save(action);
        return ResponseEntity.status(201).build();
    }

    @PatchMapping("/{id}/status")
    ProjectResponse changeStatus(@PathVariable Long id, @RequestBody Map<String, String> body,
                                  @AuthenticationPrincipal Jwt jwt) {
        var userId = AuthenticatedUser.from(jwt).id();
        var project = projectService.findByIdAndUserId(id, userId)
            .orElseThrow(() -> new ResourceNotFoundException("Project", id));
        project.setStatus(ProjectStatus.valueOf(body.get("status")));
        var saved = projectService.save(project);
        int nextCount = (int) actionService.findAllByUserIdAndProjectId(userId, id)
            .stream().filter(a -> a.getStatus() == ActionStatus.NEXT).count();
        return ProjectResponse.from(saved, nextCount);
    }
}
