package app.guad.feature.review;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class WeeklyReviewService {
    private final WeeklyReviewRepository repository;

    public WeeklyReviewService(WeeklyReviewRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public WeeklyReview startReview(UUID userId) {
        var review = new WeeklyReview();
        review.setStartedAt(Instant.now());
        review.setCurrentStep(ReviewStep.CLEAR_INBOX);
        review.setUserId(userId);
        return repository.save(review);
    }

    public Optional<WeeklyReview> getCurrentReview(UUID userId) {
        return repository.findFirstByUserIdAndCompletedAtIsNullOrderByStartedAtDesc(userId);
    }

    public Optional<WeeklyReview> getLastCompletedReview(UUID userId) {
        return repository.findFirstByUserIdAndCompletedAtIsNotNullOrderByCompletedAtDesc(userId);
    }

    @Transactional
    public WeeklyReview advanceStep(Long id, UUID userId) {
        var review = repository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Review not found"));
        if (!review.getUserId().equals(userId)) {
            throw new IllegalArgumentException("Review not found");
        }
        var steps = ReviewStep.values();
        int currentOrdinal = review.getCurrentStep().ordinal();
        if (currentOrdinal < steps.length - 1) {
            review.setCurrentStep(steps[currentOrdinal + 1]);
        }
        return repository.save(review);
    }

    @Transactional
    public WeeklyReview completeReview(Long id, UUID userId) {
        var review = repository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Review not found"));
        if (!review.getUserId().equals(userId)) {
            throw new IllegalArgumentException("Review not found");
        }
        review.setCurrentStep(ReviewStep.DONE);
        review.setCompletedAt(Instant.now());
        return repository.save(review);
    }
}
