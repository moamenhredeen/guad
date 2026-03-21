package app.guad.feature.review.api;

import app.guad.core.ApiResponse;
import app.guad.feature.review.WeeklyReviewService;
import app.guad.security.AuthenticatedUser;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/reviews")
class WeeklyReviewRestController {

    private final WeeklyReviewService reviewService;

    WeeklyReviewRestController(WeeklyReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    ResponseEntity<ApiResponse<WeeklyReviewResponse>> start(@AuthenticationPrincipal Jwt jwt) {
        var userId = AuthenticatedUser.from(jwt).id();
        var review = reviewService.startReview(userId);
        return ResponseEntity.created(URI.create("/api/reviews/" + review.getId()))
            .body(ApiResponse.of(WeeklyReviewResponse.from(review)));
    }

    @GetMapping("/current")
    ResponseEntity<ApiResponse<WeeklyReviewResponse>> current(@AuthenticationPrincipal Jwt jwt) {
        var userId = AuthenticatedUser.from(jwt).id();
        return reviewService.getCurrentReview(userId)
            .map(r -> ResponseEntity.ok(ApiResponse.of(WeeklyReviewResponse.from(r))))
            .orElseGet(() -> ResponseEntity.noContent().build());
    }

    @PatchMapping("/{id}/step")
    ApiResponse<WeeklyReviewResponse> advanceStep(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        var userId = AuthenticatedUser.from(jwt).id();
        return ApiResponse.of(WeeklyReviewResponse.from(reviewService.advanceStep(id, userId)));
    }

    @PostMapping("/{id}/complete")
    ApiResponse<WeeklyReviewResponse> complete(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        var userId = AuthenticatedUser.from(jwt).id();
        return ApiResponse.of(WeeklyReviewResponse.from(reviewService.completeReview(id, userId)));
    }

    @GetMapping("/last")
    ResponseEntity<ApiResponse<WeeklyReviewResponse>> last(@AuthenticationPrincipal Jwt jwt) {
        var userId = AuthenticatedUser.from(jwt).id();
        return reviewService.getLastCompletedReview(userId)
            .map(r -> ResponseEntity.ok(ApiResponse.of(WeeklyReviewResponse.from(r))))
            .orElseGet(() -> ResponseEntity.noContent().build());
    }
}
