package app.guad.feature.dashboard.api;

import java.time.Instant;

public record DashboardResponse(
    long inboxCount,
    long nextActionsCount,
    long activeProjectsCount,
    long waitingForCount,
    long somedayMaybeActionsCount,
    boolean weeklyReviewDue,
    Instant lastReviewDate
) {}
