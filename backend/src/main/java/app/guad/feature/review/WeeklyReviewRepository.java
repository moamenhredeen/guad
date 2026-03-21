package app.guad.feature.review;

import org.springframework.data.repository.CrudRepository;
import java.util.Optional;
import java.util.UUID;

interface WeeklyReviewRepository extends CrudRepository<WeeklyReview, Long> {
    Optional<WeeklyReview> findFirstByUserIdAndCompletedAtIsNullOrderByStartedAtDesc(UUID userId);
    Optional<WeeklyReview> findFirstByUserIdAndCompletedAtIsNotNullOrderByCompletedAtDesc(UUID userId);
}
