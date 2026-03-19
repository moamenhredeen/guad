package app.guad.feature.context.api;

import app.guad.core.ResourceNotFoundException;
import app.guad.feature.context.Context;
import app.guad.feature.context.ContextRepository;
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
    private final ContextRepository contextRepository;

    ContextRestController(ContextService contextService, ContextRepository contextRepository) {
        this.contextService = contextService;
        this.contextRepository = contextRepository;
    }

    @GetMapping
    List<ContextResponse> list(@AuthenticationPrincipal Jwt jwt) {
        var userId = AuthenticatedUser.from(jwt).id();
        return contextRepository.findAllByUserId(userId).stream()
            .map(ContextResponse::from).toList();
    }

    @PostMapping
    ResponseEntity<ContextResponse> create(@Valid @RequestBody CreateContextRequest request,
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
            .body(ContextResponse.from(saved));
    }

    @PutMapping("/{id}")
    ContextResponse update(@PathVariable Long id, @Valid @RequestBody CreateContextRequest request,
                           @AuthenticationPrincipal Jwt jwt) {
        var userId = AuthenticatedUser.from(jwt).id();
        var context = contextRepository.findByIdAndUserId(id, userId)
            .orElseThrow(() -> new ResourceNotFoundException("Context", id));
        context.setName(request.name());
        context.setDescription(request.description());
        context.setColor(request.color());
        context.setIconKey(request.iconKey());
        return ContextResponse.from(contextService.save(context));
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> delete(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        var userId = AuthenticatedUser.from(jwt).id();
        contextRepository.findByIdAndUserId(id, userId)
            .orElseThrow(() -> new ResourceNotFoundException("Context", id));
        contextService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
