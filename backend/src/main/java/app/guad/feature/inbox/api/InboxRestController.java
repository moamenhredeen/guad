package app.guad.feature.inbox.api;

import app.guad.core.ApiResponse;
import app.guad.core.ResourceNotFoundException;
import app.guad.feature.inbox.Capture;
import app.guad.feature.inbox.CaptureStatus;
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
    ResponseEntity<ApiResponse<CaptureResponse>> create(@Valid @RequestBody CreateCaptureRequest request,
                                             @AuthenticationPrincipal Jwt jwt) {
        var userId = AuthenticatedUser.from(jwt).id();
        var item = new Capture();
        item.setTitle(request.title());
        item.setDescription(request.description());
        item.setStatus(CaptureStatus.UNPROCESSED);
        item.setUserId(userId);
        var saved = inboxService.save(item);
        return ResponseEntity.created(URI.create("/api/inbox/" + saved.getId()))
            .body(ApiResponse.of(CaptureResponse.from(saved)));
    }

    @GetMapping
    ApiResponse<List<CaptureResponse>> list(@AuthenticationPrincipal Jwt jwt) {
        var userId = AuthenticatedUser.from(jwt).id();
        return ApiResponse.of(inboxService.getUnprocessedByUserId(userId).stream()
            .map(CaptureResponse::from).toList());
    }

    @GetMapping("/{id}")
    ApiResponse<CaptureResponse> get(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        var userId = AuthenticatedUser.from(jwt).id();
        return ApiResponse.of(inboxService.getByIdAndUserId(id, userId)
            .map(CaptureResponse::from)
            .orElseThrow(() -> new ResourceNotFoundException("Capture", id)));
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> delete(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        var userId = AuthenticatedUser.from(jwt).id();
        inboxService.getByIdAndUserId(id, userId)
            .orElseThrow(() -> new ResourceNotFoundException("Capture", id));
        inboxService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/process")
    ResponseEntity<Void> process(@PathVariable Long id,
                                 @Valid @RequestBody ProcessCaptureRequest request,
                                 @AuthenticationPrincipal Jwt jwt) {
        var userId = AuthenticatedUser.from(jwt).id();
        inboxProcessingService.process(id, request, userId);
        return ResponseEntity.ok().build();
    }
}
