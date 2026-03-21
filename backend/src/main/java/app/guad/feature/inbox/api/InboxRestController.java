package app.guad.feature.inbox.api;

import app.guad.core.ApiResponse;
import app.guad.core.ResourceNotFoundException;
import app.guad.feature.inbox.InboxItem;
import app.guad.feature.inbox.InboxItemStatus;
import app.guad.feature.inbox.InboxProcessingService;
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
    private final InboxProcessingService inboxProcessingService;

    InboxRestController(InboxService inboxService, InboxProcessingService inboxProcessingService) {
        this.inboxService = inboxService;
        this.inboxProcessingService = inboxProcessingService;
    }

    @PostMapping
    ResponseEntity<ApiResponse<InboxItemResponse>> create(@Valid @RequestBody CreateInboxItemRequest request,
                                             @AuthenticationPrincipal Jwt jwt) {
        var userId = AuthenticatedUser.from(jwt).id();
        var item = new InboxItem();
        item.setTitle(request.title());
        item.setDescription(request.description());
        item.setStatus(InboxItemStatus.UNPROCESSED);
        item.setUserId(userId);
        var saved = inboxService.save(item);
        return ResponseEntity.created(URI.create("/api/inbox/" + saved.getId()))
            .body(ApiResponse.of(InboxItemResponse.from(saved)));
    }

    @GetMapping
    ApiResponse<List<InboxItemResponse>> list(@AuthenticationPrincipal Jwt jwt) {
        var userId = AuthenticatedUser.from(jwt).id();
        return ApiResponse.of(inboxService.getUnprocessedByUserId(userId).stream()
            .map(InboxItemResponse::from).toList());
    }

    @GetMapping("/{id}")
    ApiResponse<InboxItemResponse> get(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        var userId = AuthenticatedUser.from(jwt).id();
        return ApiResponse.of(inboxService.getByIdAndUserId(id, userId)
            .map(InboxItemResponse::from)
            .orElseThrow(() -> new ResourceNotFoundException("InboxItem", id)));
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> delete(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        var userId = AuthenticatedUser.from(jwt).id();
        inboxService.getByIdAndUserId(id, userId)
            .orElseThrow(() -> new ResourceNotFoundException("InboxItem", id));
        inboxService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/process")
    ResponseEntity<Void> process(@PathVariable Long id,
                                 @Valid @RequestBody ProcessInboxItemRequest request,
                                 @AuthenticationPrincipal Jwt jwt) {
        var userId = AuthenticatedUser.from(jwt).id();
        inboxProcessingService.process(id, request, userId);
        return ResponseEntity.ok().build();
    }
}
