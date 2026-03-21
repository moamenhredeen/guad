package app.guad.feature.review;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WeeklyReviewServiceTest {

    @Mock
    WeeklyReviewRepository repository;

    @InjectMocks
    WeeklyReviewService weeklyReviewService;

    @Test
    void startReview_createsNewReviewWithClearInboxStep() {
        var userId = UUID.randomUUID();
        var saved = new WeeklyReview();
        saved.setId(1L);
        saved.setCurrentStep(ReviewStep.CLEAR_INBOX);
        saved.setUserId(userId);

        when(repository.save(any(WeeklyReview.class))).thenReturn(saved);

        var result = weeklyReviewService.startReview(userId);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getCurrentStep()).isEqualTo(ReviewStep.CLEAR_INBOX);
        assertThat(result.getUserId()).isEqualTo(userId);
        verify(repository).save(any(WeeklyReview.class));
    }

    @Test
    void getCurrentReview_found_returnsOptionalWithReview() {
        var userId = UUID.randomUUID();
        var review = new WeeklyReview();
        review.setId(1L);
        when(repository.findFirstByUserIdAndCompletedAtIsNullOrderByStartedAtDesc(userId))
            .thenReturn(Optional.of(review));

        var result = weeklyReviewService.getCurrentReview(userId);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
    }

    @Test
    void getCurrentReview_notFound_returnsEmptyOptional() {
        var userId = UUID.randomUUID();
        when(repository.findFirstByUserIdAndCompletedAtIsNullOrderByStartedAtDesc(userId))
            .thenReturn(Optional.empty());

        var result = weeklyReviewService.getCurrentReview(userId);

        assertThat(result).isEmpty();
    }

    @Test
    void getLastCompletedReview_found_returnsOptionalWithReview() {
        var userId = UUID.randomUUID();
        var review = new WeeklyReview();
        review.setId(2L);
        when(repository.findFirstByUserIdAndCompletedAtIsNotNullOrderByCompletedAtDesc(userId))
            .thenReturn(Optional.of(review));

        var result = weeklyReviewService.getLastCompletedReview(userId);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(2L);
    }

    @Test
    void getLastCompletedReview_notFound_returnsEmptyOptional() {
        var userId = UUID.randomUUID();
        when(repository.findFirstByUserIdAndCompletedAtIsNotNullOrderByCompletedAtDesc(userId))
            .thenReturn(Optional.empty());

        var result = weeklyReviewService.getLastCompletedReview(userId);

        assertThat(result).isEmpty();
    }

    @Test
    void advanceStep_advancesToNextStep() {
        var userId = UUID.randomUUID();
        var review = new WeeklyReview();
        review.setId(1L);
        review.setCurrentStep(ReviewStep.CLEAR_INBOX);
        review.setUserId(userId);

        when(repository.findById(1L)).thenReturn(Optional.of(review));
        when(repository.save(review)).thenReturn(review);

        var result = weeklyReviewService.advanceStep(1L, userId);

        assertThat(result.getCurrentStep()).isEqualTo(ReviewStep.REVIEW_NEXT_ACTIONS);
        verify(repository).save(review);
    }

    @Test
    void advanceStep_atLastStep_doesNotAdvanceBeyond() {
        var userId = UUID.randomUUID();
        var review = new WeeklyReview();
        review.setId(1L);
        review.setCurrentStep(ReviewStep.DONE);
        review.setUserId(userId);

        when(repository.findById(1L)).thenReturn(Optional.of(review));
        when(repository.save(review)).thenReturn(review);

        var result = weeklyReviewService.advanceStep(1L, userId);

        assertThat(result.getCurrentStep()).isEqualTo(ReviewStep.DONE);
    }

    @Test
    void advanceStep_notFound_throwsIllegalArgumentException() {
        var userId = UUID.randomUUID();
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> weeklyReviewService.advanceStep(99L, userId))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Review not found");
    }

    @Test
    void advanceStep_wrongUser_throwsIllegalArgumentException() {
        var userId = UUID.randomUUID();
        var otherUserId = UUID.randomUUID();
        var review = new WeeklyReview();
        review.setId(1L);
        review.setCurrentStep(ReviewStep.CLEAR_INBOX);
        review.setUserId(otherUserId);

        when(repository.findById(1L)).thenReturn(Optional.of(review));

        assertThatThrownBy(() -> weeklyReviewService.advanceStep(1L, userId))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Review not found");
    }

    @Test
    void completeReview_setsStepToDoneAndCompletedAt() {
        var userId = UUID.randomUUID();
        var review = new WeeklyReview();
        review.setId(1L);
        review.setCurrentStep(ReviewStep.REVIEW_SOMEDAY_MAYBE);
        review.setUserId(userId);

        when(repository.findById(1L)).thenReturn(Optional.of(review));
        when(repository.save(review)).thenReturn(review);

        var result = weeklyReviewService.completeReview(1L, userId);

        assertThat(result.getCurrentStep()).isEqualTo(ReviewStep.DONE);
        assertThat(result.getCompletedAt()).isNotNull();
        verify(repository).save(review);
    }

    @Test
    void completeReview_notFound_throwsIllegalArgumentException() {
        var userId = UUID.randomUUID();
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> weeklyReviewService.completeReview(99L, userId))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Review not found");
    }

    @Test
    void completeReview_wrongUser_throwsIllegalArgumentException() {
        var userId = UUID.randomUUID();
        var otherUserId = UUID.randomUUID();
        var review = new WeeklyReview();
        review.setId(1L);
        review.setUserId(otherUserId);

        when(repository.findById(1L)).thenReturn(Optional.of(review));

        assertThatThrownBy(() -> weeklyReviewService.completeReview(1L, userId))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Review not found");
    }
}
