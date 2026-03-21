package app.guad.feature.context.api;

import app.guad.core.ApiResponse;
import app.guad.core.ResourceNotFoundException;
import app.guad.feature.context.Context;
import app.guad.feature.context.ContextService;
import app.guad.security.AuthenticatedUser;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/contexts")
class ContextRestController {

    private final ContextService contextService;

    ContextRestController(ContextService contextService) {
        this.contextService = contextService;
    }

    @GetMapping
    ApiResponse<List<ContextResponse>> list(@AuthenticationPrincipal Jwt jwt) {
        var userId = AuthenticatedUser.from(jwt).id();
        return ApiResponse.of(contextService.findAllByUserId(userId).stream()
            .map(ContextResponse::from).toList());
    }

    @PostMapping
    ResponseEntity<ApiResponse<ContextResponse>> create(@Valid @RequestBody CreateContextRequest request,
                                           @AuthenticationPrincipal Jwt jwt) {
        var userId = AuthenticatedUser.from(jwt).id();
        var context = new Context();
        context.setName(request.name());
        context.setDescription(request.description());
        context.setColor(request.color());
        context.setIconKey(request.iconKey());
        context.setUserId(userId);
        var saved = contextService.save(context);
        return ResponseEntity.created(URI.create("/api/contexts/" + saved.getId()))
            .body(ApiResponse.of(ContextResponse.from(saved)));
    }

    @PutMapping("/{id}")
    ApiResponse<ContextResponse> update(@PathVariable Long id, @Valid @RequestBody CreateContextRequest request,
                           @AuthenticationPrincipal Jwt jwt) {
        var userId = AuthenticatedUser.from(jwt).id();
        var context = contextService.findByIdAndUserId(id, userId)
            .orElseThrow(() -> new ResourceNotFoundException("Context", id));
        context.setName(request.name());
        context.setDescription(request.description());
        context.setColor(request.color());
        context.setIconKey(request.iconKey());
        return ApiResponse.of(ContextResponse.from(contextService.save(context)));
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> delete(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        var userId = AuthenticatedUser.from(jwt).id();
        contextService.findByIdAndUserId(id, userId)
            .orElseThrow(() -> new ResourceNotFoundException("Context", id));
        contextService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
