package app.guad.feature.dashboard;

import app.guad.feature.action.ActionService;
import app.guad.feature.action.ActionStatus;
import app.guad.feature.dashboard.api.DashboardResponse;
import app.guad.feature.inbox.InboxItemStatus;
import app.guad.feature.inbox.InboxService;
import app.guad.feature.project.ProjectRepository;
import app.guad.feature.project.ProjectStatus;
import app.guad.feature.review.WeeklyReviewService;
import app.guad.feature.waitingfor.WaitingForItemStatus;
import app.guad.feature.waitingfor.WaitingForRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
public class DashboardService {

    private final InboxService inboxService;
    private final ActionService actionService;
    private final ProjectRepository projectRepository;
    private final WaitingForRepository waitingForRepository;
    private final WeeklyReviewService weeklyReviewService;

    public DashboardService(InboxService inboxService, ActionService actionService,
                             ProjectRepository projectRepository, WaitingForRepository waitingForRepository,
                             WeeklyReviewService weeklyReviewService) {
        this.inboxService = inboxService;
        this.actionService = actionService;
        this.projectRepository = projectRepository;
        this.waitingForRepository = waitingForRepository;
        this.weeklyReviewService = weeklyReviewService;
    }

    @Transactional(readOnly = true)
    public DashboardResponse getDashboard(UUID userId) {
        long inboxCount = inboxService.countByUserIdAndStatus(userId, InboxItemStatus.UNPROCESSED);
        long nextActionsCount = actionService.countByUserIdAndStatus(userId, ActionStatus.NEXT);
        long activeProjectsCount = projectRepository.countByUserIdAndStatus(userId, ProjectStatus.ACTIVE);
        long waitingForCount = waitingForRepository.countByUserIdAndStatus(userId, WaitingForItemStatus.WAITING);
        long somedayMaybeCount = actionService.countByUserIdAndStatus(userId, ActionStatus.SOMEDAY_MAYBE);

        var lastReview = weeklyReviewService.getLastCompletedReview(userId).orElse(null);
        Instant lastReviewDate = lastReview != null ? lastReview.getCompletedAt() : null;
        boolean weeklyReviewDue = lastReviewDate == null ||
            lastReviewDate.isBefore(Instant.now().minus(7, ChronoUnit.DAYS));

        return new DashboardResponse(inboxCount, nextActionsCount, activeProjectsCount,
            waitingForCount, somedayMaybeCount, weeklyReviewDue, lastReviewDate);
    }
}
