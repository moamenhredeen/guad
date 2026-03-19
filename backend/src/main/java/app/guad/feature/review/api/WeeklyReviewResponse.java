package app.guad.feature.review.api;

import app.guad.feature.review.WeeklyReview;
import java.time.Instant;

public record WeeklyReviewResponse(Long id, Instant startedAt, Instant completedAt, String currentStep, String notes) {
    public static WeeklyReviewResponse from(WeeklyReview review) {
        return new WeeklyReviewResponse(review.getId(), review.getStartedAt(), review.getCompletedAt(),
            review.getCurrentStep().name(), review.getNotes());
    }
}
