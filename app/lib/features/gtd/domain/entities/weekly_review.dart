enum WeeklyReviewStep {
  collectLoosePapers,
  getInboxToZero,
  reviewNextActions,
  reviewPreviousCalendar,
  reviewUpcomingCalendar,
  reviewWaitingFor,
  reviewProjects,
  reviewGoals,
  reviewSomedayMaybe,
  complete,
}

class WeeklyReview {
  const WeeklyReview({
    required this.id,
    required this.currentStep,
    this.startedAt,
    this.completedAt,
    this.notes,
  });

  final int id;
  final WeeklyReviewStep currentStep;
  final DateTime? startedAt;
  final DateTime? completedAt;
  final String? notes;

  bool get isComplete =>
      completedAt != null || currentStep == WeeklyReviewStep.complete;
}
