class GtdDashboard {
  const GtdDashboard({
    required this.inboxCount,
    required this.nextActionsCount,
    required this.activeProjectsCount,
    required this.waitingForCount,
    required this.somedayMaybeActionsCount,
    required this.weeklyReviewDue,
    this.lastReviewDate,
  });

  final int inboxCount;
  final int nextActionsCount;
  final int activeProjectsCount;
  final int waitingForCount;
  final int somedayMaybeActionsCount;
  final bool weeklyReviewDue;
  final DateTime? lastReviewDate;
}
