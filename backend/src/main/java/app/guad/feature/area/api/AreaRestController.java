package app.guad.feature.area.api;

import app.guad.core.ResourceNotFoundException;
import app.guad.feature.area.Area;
import app.guad.feature.area.AreaRepository;
import app.guad.feature.area.AreaService;
import app.guad.security.AuthenticatedUser;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/areas")
class AreaRestController {

    private final AreaService areaService;
    private final AreaRepository areaRepository;

    AreaRestController(AreaService areaService, AreaRepository areaRepository) {
        this.areaService = areaService;
        this.areaRepository = areaRepository;
    }

    @GetMapping
    List<AreaResponse> list(@AuthenticationPrincipal Jwt jwt) {
        var userId = AuthenticatedUser.from(jwt).id();
        return areaRepository.findAllByUserId(userId).stream()
            .map(AreaResponse::from).toList();
    }

    @PostMapping
    ResponseEntity<AreaResponse> create(@Valid @RequestBody CreateAreaRequest request,
                                        @AuthenticationPrincipal Jwt jwt) {
        var userId = AuthenticatedUser.from(jwt).id();
        var area = new Area();
        area.setName(request.name());
        area.setDescription(request.description());
        area.setUserId(userId);
        var saved = areaService.save(area);
        return ResponseEntity.created(URI.create("/api/areas/" + saved.getId()))
            .body(AreaResponse.from(saved));
    }

    @PutMapping("/{id}")
    AreaResponse update(@PathVariable Long id, @Valid @RequestBody CreateAreaRequest request,
                        @AuthenticationPrincipal Jwt jwt) {
        var userId = AuthenticatedUser.from(jwt).id();
        var area = areaRepository.findByIdAndUserId(id, userId)
            .orElseThrow(() -> new ResourceNotFoundException("Area", id));
        area.setName(request.name());
        area.setDescription(request.description());
        return AreaResponse.from(areaService.save(area));
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> delete(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        var userId = AuthenticatedUser.from(jwt).id();
        areaRepository.findByIdAndUserId(id, userId)
            .orElseThrow(() -> new ResourceNotFoundException("Area", id));
        areaService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
