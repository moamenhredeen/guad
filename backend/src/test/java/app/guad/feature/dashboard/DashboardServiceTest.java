package app.guad.feature.dashboard;

import app.guad.feature.action.ActionService;
import app.guad.feature.action.ActionStatus;
import app.guad.feature.inbox.InboxItemStatus;
import app.guad.feature.inbox.InboxService;
import app.guad.feature.project.ProjectService;
import app.guad.feature.project.ProjectStatus;
import app.guad.feature.review.WeeklyReview;
import app.guad.feature.review.WeeklyReviewService;
import app.guad.feature.waitingfor.WaitingForItemStatus;
import app.guad.feature.waitingfor.WaitingForService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    InboxService inboxService;

    @Mock
    ActionService actionService;

    @Mock
    ProjectService projectService;

    @Mock
    WaitingForService waitingForService;

    @Mock
    WeeklyReviewService weeklyReviewService;

    @InjectMocks
    DashboardService dashboardService;

    private final UUID userId = UUID.randomUUID();

    @Test
    void getDashboard_returnsCorrectCounts() {
        when(inboxService.countByUserIdAndStatus(userId, InboxItemStatus.UNPROCESSED)).thenReturn(5L);
        when(actionService.countByUserIdAndStatus(userId, ActionStatus.NEXT)).thenReturn(3L);
        when(projectService.countByUserIdAndStatus(userId, ProjectStatus.ACTIVE)).thenReturn(2L);
        when(waitingForService.countByUserIdAndStatus(userId, WaitingForItemStatus.WAITING)).thenReturn(4L);
        when(actionService.countByUserIdAndStatus(userId, ActionStatus.SOMEDAY_MAYBE)).thenReturn(1L);

        var review = new WeeklyReview();
        review.setCompletedAt(Instant.now().minus(1, ChronoUnit.DAYS));
        when(weeklyReviewService.getLastCompletedReview(userId)).thenReturn(Optional.of(review));

        var result = dashboardService.getDashboard(userId);

        assertThat(result.inboxCount()).isEqualTo(5L);
        assertThat(result.nextActionsCount()).isEqualTo(3L);
        assertThat(result.activeProjectsCount()).isEqualTo(2L);
        assertThat(result.waitingForCount()).isEqualTo(4L);
        assertThat(result.somedayMaybeActionsCount()).isEqualTo(1L);
        assertThat(result.weeklyReviewDue()).isFalse();
        assertThat(result.lastReviewDate()).isNotNull();
    }

    @Test
    void getDashboard_noReviewExists_weeklyReviewDueIsTrue() {
        when(inboxService.countByUserIdAndStatus(userId, InboxItemStatus.UNPROCESSED)).thenReturn(0L);
        when(actionService.countByUserIdAndStatus(userId, ActionStatus.NEXT)).thenReturn(0L);
        when(projectService.countByUserIdAndStatus(userId, ProjectStatus.ACTIVE)).thenReturn(0L);
        when(waitingForService.countByUserIdAndStatus(userId, WaitingForItemStatus.WAITING)).thenReturn(0L);
        when(actionService.countByUserIdAndStatus(userId, ActionStatus.SOMEDAY_MAYBE)).thenReturn(0L);
        when(weeklyReviewService.getLastCompletedReview(userId)).thenReturn(Optional.empty());

        var result = dashboardService.getDashboard(userId);

        assertThat(result.weeklyReviewDue()).isTrue();
        assertThat(result.lastReviewDate()).isNull();
    }

    @Test
    void getDashboard_recentReviewExists_weeklyReviewDueIsFalse() {
        when(inboxService.countByUserIdAndStatus(userId, InboxItemStatus.UNPROCESSED)).thenReturn(0L);
        when(actionService.countByUserIdAndStatus(userId, ActionStatus.NEXT)).thenReturn(0L);
        when(projectService.countByUserIdAndStatus(userId, ProjectStatus.ACTIVE)).thenReturn(0L);
        when(waitingForService.countByUserIdAndStatus(userId, WaitingForItemStatus.WAITING)).thenReturn(0L);
        when(actionService.countByUserIdAndStatus(userId, ActionStatus.SOMEDAY_MAYBE)).thenReturn(0L);

        var review = new WeeklyReview();
        review.setCompletedAt(Instant.now().minus(3, ChronoUnit.DAYS));
        when(weeklyReviewService.getLastCompletedReview(userId)).thenReturn(Optional.of(review));

        var result = dashboardService.getDashboard(userId);

        assertThat(result.weeklyReviewDue()).isFalse();
    }

    @Test
    void getDashboard_oldReviewExists_weeklyReviewDueIsTrue() {
        when(inboxService.countByUserIdAndStatus(userId, InboxItemStatus.UNPROCESSED)).thenReturn(0L);
        when(actionService.countByUserIdAndStatus(userId, ActionStatus.NEXT)).thenReturn(0L);
        when(projectService.countByUserIdAndStatus(userId, ProjectStatus.ACTIVE)).thenReturn(0L);
        when(waitingForService.countByUserIdAndStatus(userId, WaitingForItemStatus.WAITING)).thenReturn(0L);
        when(actionService.countByUserIdAndStatus(userId, ActionStatus.SOMEDAY_MAYBE)).thenReturn(0L);

        var review = new WeeklyReview();
        review.setCompletedAt(Instant.now().minus(10, ChronoUnit.DAYS));
        when(weeklyReviewService.getLastCompletedReview(userId)).thenReturn(Optional.of(review));

        var result = dashboardService.getDashboard(userId);

        assertThat(result.weeklyReviewDue()).isTrue();
    }
}
