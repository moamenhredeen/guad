package app.guad.feature.action.api;

import app.guad.core.ResourceNotFoundException;
import app.guad.feature.action.Action;
import app.guad.feature.action.ActionService;
import app.guad.feature.action.ActionStatus;
import app.guad.feature.area.AreaService;
import app.guad.feature.context.Context;
import app.guad.feature.context.ContextService;
import app.guad.feature.project.ProjectService;
import app.guad.security.AuthenticatedUser;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/actions")
class ActionRestController {

    private final ActionService actionService;
    private final ProjectService projectService;
    private final AreaService areaService;
    private final ContextService contextService;

    ActionRestController(ActionService actionService,
                          ProjectService projectService, AreaService areaService,
                          ContextService contextService) {
        this.actionService = actionService;
        this.projectService = projectService;
        this.areaService = areaService;
        this.contextService = contextService;
    }

    @GetMapping
    List<ActionResponse> list(@RequestParam(required = false) ActionStatus status,
                               @RequestParam(required = false) Long contextId,
                               @AuthenticationPrincipal Jwt jwt) {
        var userId = AuthenticatedUser.from(jwt).id();
        List<Action> actions;
        if (status != null) {
            actions = actionService.findAllByUserIdAndStatus(userId, status);
        } else {
            actions = actionService.findAllByUserId(userId);
        }
        if (contextId != null) {
            actions = actions.stream()
                .filter(a -> a.getContexts() != null && a.getContexts().stream()
                    .anyMatch(c -> c.getId().equals(contextId)))
                .toList();
        }
        return actions.stream().map(ActionResponse::from).toList();
    }

    @PostMapping
    ResponseEntity<ActionResponse> create(@Valid @RequestBody CreateActionRequest request,
                                           @AuthenticationPrincipal Jwt jwt) {
        var userId = AuthenticatedUser.from(jwt).id();
        var action = new Action();
        action.setDescription(request.description());
        action.setNotes(request.notes());
        action.setStatus(ActionStatus.NEXT);
        action.setEnergyLevel(request.energyLevel());
        action.setEstimatedDuration(request.estimatedDuration());
        action.setDueDate(request.dueDate());
        action.setScheduledDate(request.scheduledDate());
        action.setUserId(userId);
        if (request.projectId() != null) {
            projectService.findById(request.projectId()).ifPresent(action::setProject);
        }
        if (request.areaId() != null) {
            areaService.getAreaById(request.areaId()).ifPresent(action::setArea);
        }
        if (request.contextIds() != null && !request.contextIds().isEmpty()) {
            var contexts = new HashSet<Context>();
            for (var ctxId : request.contextIds()) {
                contextService.findById(ctxId).ifPresent(contexts::add);
            }
            action.setContexts(contexts);
        }
        var saved = actionService.save(action);
        return ResponseEntity.created(URI.create("/api/actions/" + saved.getId()))
            .body(ActionResponse.from(saved));
    }

    @GetMapping("/{id}")
    ActionResponse get(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        var userId = AuthenticatedUser.from(jwt).id();
        return actionService.findByIdAndUserId(id, userId)
            .map(ActionResponse::from)
            .orElseThrow(() -> new ResourceNotFoundException("Action", id));
    }

    @PutMapping("/{id}")
    ActionResponse update(@PathVariable Long id, @Valid @RequestBody UpdateActionRequest request,
                           @AuthenticationPrincipal Jwt jwt) {
        var userId = AuthenticatedUser.from(jwt).id();
        var action = actionService.findByIdAndUserId(id, userId)
            .orElseThrow(() -> new ResourceNotFoundException("Action", id));
        if (request.description() != null) action.setDescription(request.description());
        if (request.notes() != null) action.setNotes(request.notes());
        action.setEnergyLevel(request.energyLevel());
        action.setEstimatedDuration(request.estimatedDuration());
        action.setDueDate(request.dueDate());
        action.setScheduledDate(request.scheduledDate());
        if (request.projectId() != null) {
            projectService.findById(request.projectId()).ifPresent(action::setProject);
        }
        if (request.areaId() != null) {
            areaService.getAreaById(request.areaId()).ifPresent(action::setArea);
        }
        if (request.contextIds() != null) {
            var contexts = new HashSet<Context>();
            for (var ctxId : request.contextIds()) {
                contextService.findById(ctxId).ifPresent(contexts::add);
            }
            action.setContexts(contexts);
        }
        return ActionResponse.from(actionService.save(action));
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> delete(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        var userId = AuthenticatedUser.from(jwt).id();
        actionService.findByIdAndUserId(id, userId)
            .orElseThrow(() -> new ResourceNotFoundException("Action", id));
        actionService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/complete")
    ActionResponse complete(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        var userId = AuthenticatedUser.from(jwt).id();
        return ActionResponse.from(actionService.completeAction(id, userId));
    }

    @PatchMapping("/{id}/status")
    ActionResponse changeStatus(@PathVariable Long id, @RequestBody Map<String, String> body,
                                 @AuthenticationPrincipal Jwt jwt) {
        var userId = AuthenticatedUser.from(jwt).id();
        var status = ActionStatus.valueOf(body.get("status"));
        return ActionResponse.from(actionService.updateStatus(id, userId, status));
    }
}
