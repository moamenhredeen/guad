package app.guad.feature.project.api;

import app.guad.core.ResourceNotFoundException;
import app.guad.feature.action.Action;
import app.guad.feature.action.ActionRepository;
import app.guad.feature.action.ActionService;
import app.guad.feature.action.ActionStatus;
import app.guad.feature.area.AreaRepository;
import app.guad.feature.project.Project;
import app.guad.feature.project.ProjectRepository;
import app.guad.feature.project.ProjectService;
import app.guad.feature.project.ProjectStatus;
import app.guad.feature.waitingfor.WaitingForItemStatus;
import app.guad.feature.waitingfor.WaitingForRepository;
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
    private final ProjectRepository projectRepository;
    private final ActionRepository actionRepository;
    private final ActionService actionService;
    private final AreaRepository areaRepository;
    private final WaitingForRepository waitingForRepository;

    ProjectRestController(ProjectService projectService, ProjectRepository projectRepository,
                           ActionRepository actionRepository, ActionService actionService,
                           AreaRepository areaRepository, WaitingForRepository waitingForRepository) {
        this.projectService = projectService;
        this.projectRepository = projectRepository;
        this.actionRepository = actionRepository;
        this.actionService = actionService;
        this.areaRepository = areaRepository;
        this.waitingForRepository = waitingForRepository;
    }

    @GetMapping
    List<ProjectResponse> list(@AuthenticationPrincipal Jwt jwt) {
        var userId = AuthenticatedUser.from(jwt).id();
        return projectRepository.findAllByUserIdAndStatus(userId, ProjectStatus.ACTIVE).stream()
            .map(p -> {
                int nextCount = (int) actionRepository.findAllByUserIdAndProjectId(userId, p.getId())
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
            areaRepository.findById(request.areaId()).ifPresent(project::setArea);
        }
        var saved = projectService.save(project);
        return ResponseEntity.created(URI.create("/api/projects/" + saved.getId()))
            .body(ProjectResponse.from(saved, 0));
    }

    @GetMapping("/{id}")
    ProjectDetailResponse get(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        var userId = AuthenticatedUser.from(jwt).id();
        var project = projectRepository.findByIdAndUserId(id, userId)
            .orElseThrow(() -> new ResourceNotFoundException("Project", id));
        var allActions = actionRepository.findAllByUserIdAndProjectId(userId, id);
        var nextActions = allActions.stream()
            .filter(a -> a.getStatus() == ActionStatus.NEXT || a.getStatus() == ActionStatus.IN_PROGRESS)
            .toList();
        var completedActions = allActions.stream()
            .filter(a -> a.getStatus() == ActionStatus.COMPLETED)
            .toList();
        var waitingFor = waitingForRepository.findAllByUserIdAndStatus(userId, WaitingForItemStatus.WAITING)
            .stream().filter(w -> w.getProject() != null && w.getProject().getId().equals(id))
            .toList();
        return ProjectDetailResponse.from(project, nextActions, waitingFor, completedActions);
    }

    @PutMapping("/{id}")
    ProjectResponse update(@PathVariable Long id, @Valid @RequestBody CreateProjectRequest request,
                            @AuthenticationPrincipal Jwt jwt) {
        var userId = AuthenticatedUser.from(jwt).id();
        var project = projectRepository.findByIdAndUserId(id, userId)
            .orElseThrow(() -> new ResourceNotFoundException("Project", id));
        project.setName(request.name());
        project.setDescription(request.description());
        project.setDesired_outcome(request.desiredOutcome());
        project.setColor(request.color());
        if (request.areaId() != null) {
            areaRepository.findById(request.areaId()).ifPresent(project::setArea);
        }
        var saved = projectService.save(project);
        int nextCount = (int) actionRepository.findAllByUserIdAndProjectId(userId, id)
            .stream().filter(a -> a.getStatus() == ActionStatus.NEXT).count();
        return ProjectResponse.from(saved, nextCount);
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> delete(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        var userId = AuthenticatedUser.from(jwt).id();
        projectRepository.findByIdAndUserId(id, userId)
            .orElseThrow(() -> new ResourceNotFoundException("Project", id));
        projectService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/actions")
    ResponseEntity<Void> addAction(@PathVariable Long id,
                                    @Valid @RequestBody Map<String, String> body,
                                    @AuthenticationPrincipal Jwt jwt) {
        var userId = AuthenticatedUser.from(jwt).id();
        var project = projectRepository.findByIdAndUserId(id, userId)
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
        var project = projectRepository.findByIdAndUserId(id, userId)
            .orElseThrow(() -> new ResourceNotFoundException("Project", id));
        project.setStatus(ProjectStatus.valueOf(body.get("status")));
        var saved = projectService.save(project);
        int nextCount = (int) actionRepository.findAllByUserIdAndProjectId(userId, id)
            .stream().filter(a -> a.getStatus() == ActionStatus.NEXT).count();
        return ProjectResponse.from(saved, nextCount);
    }
}
