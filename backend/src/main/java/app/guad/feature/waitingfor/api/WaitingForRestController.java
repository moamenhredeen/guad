package app.guad.feature.waitingfor.api;

import app.guad.core.ApiResponse;
import app.guad.core.ResourceNotFoundException;
import app.guad.feature.action.ActionService;
import app.guad.feature.project.ProjectService;
import app.guad.feature.waitingfor.WaitingForItem;
import app.guad.feature.waitingfor.WaitingForItemStatus;
import app.guad.feature.waitingfor.WaitingForService;
import app.guad.security.AuthenticatedUser;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/waiting-for")
class WaitingForRestController {

    private final WaitingForService waitingForService;
    private final ActionService actionService;
    private final ProjectService projectService;

    WaitingForRestController(WaitingForService waitingForService,
                              ActionService actionService, ProjectService projectService) {
        this.waitingForService = waitingForService;
        this.actionService = actionService;
        this.projectService = projectService;
    }

    @GetMapping
    ApiResponse<List<WaitingForResponse>> list(@AuthenticationPrincipal Jwt jwt) {
        var userId = AuthenticatedUser.from(jwt).id();
        return ApiResponse.of(waitingForService.findAllByUserIdAndStatus(userId, WaitingForItemStatus.WAITING).stream()
            .map(WaitingForResponse::from).toList());
    }

    @PostMapping
    ResponseEntity<ApiResponse<WaitingForResponse>> create(@Valid @RequestBody CreateWaitingForRequest request,
                                               @AuthenticationPrincipal Jwt jwt) {
        var userId = AuthenticatedUser.from(jwt).id();
        var item = new WaitingForItem();
        item.setTitle(request.title());
        item.setDelegatedTo(request.delegatedTo());
        item.setDelegatedAt(request.delegatedAt());
        item.setFollowUpDate(request.followUpDate());
        item.setNotes(request.notes());
        item.setStatus(WaitingForItemStatus.WAITING);
        item.setUserId(userId);
        if (request.actionId() != null) {
            actionService.getActionById(request.actionId()).ifPresent(item::setAction);
        }
        if (request.projectId() != null) {
            projectService.findById(request.projectId()).ifPresent(item::setProject);
        }
        var saved = waitingForService.save(item);
        return ResponseEntity.created(URI.create("/api/waiting-for/" + saved.getId()))
            .body(ApiResponse.of(WaitingForResponse.from(saved)));
    }

    @GetMapping("/{id}")
    ApiResponse<WaitingForResponse> get(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        var userId = AuthenticatedUser.from(jwt).id();
        return ApiResponse.of(waitingForService.findByIdAndUserId(id, userId)
            .map(WaitingForResponse::from)
            .orElseThrow(() -> new ResourceNotFoundException("WaitingForItem", id)));
    }

    @PutMapping("/{id}")
    ApiResponse<WaitingForResponse> update(@PathVariable Long id, @Valid @RequestBody CreateWaitingForRequest request,
                               @AuthenticationPrincipal Jwt jwt) {
        var userId = AuthenticatedUser.from(jwt).id();
        var item = waitingForService.findByIdAndUserId(id, userId)
            .orElseThrow(() -> new ResourceNotFoundException("WaitingForItem", id));
        item.setTitle(request.title());
        item.setDelegatedTo(request.delegatedTo());
        item.setDelegatedAt(request.delegatedAt());
        item.setFollowUpDate(request.followUpDate());
        item.setNotes(request.notes());
        if (request.actionId() != null) {
            actionService.getActionById(request.actionId()).ifPresent(item::setAction);
        }
        if (request.projectId() != null) {
            projectService.findById(request.projectId()).ifPresent(item::setProject);
        }
        return ApiResponse.of(WaitingForResponse.from(waitingForService.save(item)));
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> delete(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        var userId = AuthenticatedUser.from(jwt).id();
        waitingForService.findByIdAndUserId(id, userId)
            .orElseThrow(() -> new ResourceNotFoundException("WaitingForItem", id));
        waitingForService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/resolve")
    ApiResponse<WaitingForResponse> resolve(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        var userId = AuthenticatedUser.from(jwt).id();
        var item = waitingForService.findByIdAndUserId(id, userId)
            .orElseThrow(() -> new ResourceNotFoundException("WaitingForItem", id));
        item.setStatus(WaitingForItemStatus.RESOLVED);
        item.setCompletedDate(Instant.now());
        return ApiResponse.of(WaitingForResponse.from(waitingForService.save(item)));
    }
}
