package app.guad.feature.waitingfor.api;

import app.guad.core.ResourceNotFoundException;
import app.guad.feature.action.ActionRepository;
import app.guad.feature.project.ProjectRepository;
import app.guad.feature.waitingfor.WaitingForItem;
import app.guad.feature.waitingfor.WaitingForItemStatus;
import app.guad.feature.waitingfor.WaitingForRepository;
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
    private final WaitingForRepository waitingForRepository;
    private final ActionRepository actionRepository;
    private final ProjectRepository projectRepository;

    WaitingForRestController(WaitingForService waitingForService, WaitingForRepository waitingForRepository,
                              ActionRepository actionRepository, ProjectRepository projectRepository) {
        this.waitingForService = waitingForService;
        this.waitingForRepository = waitingForRepository;
        this.actionRepository = actionRepository;
        this.projectRepository = projectRepository;
    }

    @GetMapping
    List<WaitingForResponse> list(@AuthenticationPrincipal Jwt jwt) {
        var userId = AuthenticatedUser.from(jwt).id();
        return waitingForRepository.findAllByUserIdAndStatus(userId, WaitingForItemStatus.WAITING).stream()
            .map(WaitingForResponse::from).toList();
    }

    @PostMapping
    ResponseEntity<WaitingForResponse> create(@Valid @RequestBody CreateWaitingForRequest request,
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
            actionRepository.findById(request.actionId()).ifPresent(item::setAction);
        }
        if (request.projectId() != null) {
            projectRepository.findById(request.projectId()).ifPresent(item::setProject);
        }
        var saved = waitingForService.save(item);
        return ResponseEntity.created(URI.create("/api/waiting-for/" + saved.getId()))
            .body(WaitingForResponse.from(saved));
    }

    @GetMapping("/{id}")
    WaitingForResponse get(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        var userId = AuthenticatedUser.from(jwt).id();
        return waitingForRepository.findByIdAndUserId(id, userId)
            .map(WaitingForResponse::from)
            .orElseThrow(() -> new ResourceNotFoundException("WaitingForItem", id));
    }

    @PutMapping("/{id}")
    WaitingForResponse update(@PathVariable Long id, @Valid @RequestBody CreateWaitingForRequest request,
                               @AuthenticationPrincipal Jwt jwt) {
        var userId = AuthenticatedUser.from(jwt).id();
        var item = waitingForRepository.findByIdAndUserId(id, userId)
            .orElseThrow(() -> new ResourceNotFoundException("WaitingForItem", id));
        item.setTitle(request.title());
        item.setDelegatedTo(request.delegatedTo());
        item.setDelegatedAt(request.delegatedAt());
        item.setFollowUpDate(request.followUpDate());
        item.setNotes(request.notes());
        if (request.actionId() != null) {
            actionRepository.findById(request.actionId()).ifPresent(item::setAction);
        }
        if (request.projectId() != null) {
            projectRepository.findById(request.projectId()).ifPresent(item::setProject);
        }
        return WaitingForResponse.from(waitingForService.save(item));
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> delete(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        var userId = AuthenticatedUser.from(jwt).id();
        waitingForRepository.findByIdAndUserId(id, userId)
            .orElseThrow(() -> new ResourceNotFoundException("WaitingForItem", id));
        waitingForService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/resolve")
    WaitingForResponse resolve(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        var userId = AuthenticatedUser.from(jwt).id();
        var item = waitingForRepository.findByIdAndUserId(id, userId)
            .orElseThrow(() -> new ResourceNotFoundException("WaitingForItem", id));
        item.setStatus(WaitingForItemStatus.RESOLVED);
        item.setCompletedDate(Instant.now());
        return WaitingForResponse.from(waitingForService.save(item));
    }
}
