import 'package:guad/features/gtd/domain/entities/weekly_review.dart';

String weeklyReviewStepLabel(WeeklyReviewStep step) {
  return switch (step) {
    WeeklyReviewStep.collectLoosePapers => 'Collect loose papers and materials',
    WeeklyReviewStep.getInboxToZero => 'Get inbox to zero',
    WeeklyReviewStep.reviewNextActions => 'Review next actions',
    WeeklyReviewStep.reviewPreviousCalendar => 'Review previous calendar',
    WeeklyReviewStep.reviewUpcomingCalendar => 'Review upcoming calendar',
    WeeklyReviewStep.reviewWaitingFor => 'Review waiting-for list',
    WeeklyReviewStep.reviewProjects => 'Review projects',
    WeeklyReviewStep.reviewGoals => 'Review goals and areas',
    WeeklyReviewStep.reviewSomedayMaybe => 'Review someday maybe',
    WeeklyReviewStep.complete => 'Review complete',
  };
}
