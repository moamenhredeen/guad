package app.guad.feature.inbox.api;

import app.guad.core.ResourceNotFoundException;
import app.guad.feature.inbox.InboxItem;
import app.guad.feature.inbox.InboxItemStatus;
import app.guad.feature.inbox.InboxRepository;
import app.guad.feature.inbox.InboxService;
import app.guad.security.AuthenticatedUser;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/inbox")
class InboxRestController {

    private final InboxService inboxService;
    private final InboxRepository inboxRepository;

    InboxRestController(InboxService inboxService, InboxRepository inboxRepository) {
        this.inboxService = inboxService;
        this.inboxRepository = inboxRepository;
    }

    @PostMapping
    ResponseEntity<InboxItemResponse> create(@Valid @RequestBody CreateInboxItemRequest request,
                                             @AuthenticationPrincipal Jwt jwt) {
        var userId = AuthenticatedUser.from(jwt).id();
        var item = new InboxItem();
        item.setTitle(request.title());
        item.setDescription(request.description());
        item.setStatus(InboxItemStatus.UNPROCESSED);
        item.setUserId(userId);
        var saved = inboxService.save(item);
        return ResponseEntity.created(URI.create("/api/inbox/" + saved.getId()))
            .body(InboxItemResponse.from(saved));
    }

    @GetMapping
    List<InboxItemResponse> list(@AuthenticationPrincipal Jwt jwt) {
        var userId = AuthenticatedUser.from(jwt).id();
        return inboxRepository.findAllByUserIdAndStatus(userId, InboxItemStatus.UNPROCESSED).stream()
            .map(InboxItemResponse::from).toList();
    }

    @GetMapping("/{id}")
    InboxItemResponse get(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        var userId = AuthenticatedUser.from(jwt).id();
        return inboxRepository.findByIdAndUserId(id, userId)
            .map(InboxItemResponse::from)
            .orElseThrow(() -> new ResourceNotFoundException("InboxItem", id));
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> delete(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        var userId = AuthenticatedUser.from(jwt).id();
        inboxRepository.findByIdAndUserId(id, userId)
            .orElseThrow(() -> new ResourceNotFoundException("InboxItem", id));
        inboxService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
