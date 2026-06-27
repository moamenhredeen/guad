import 'package:guad/features/gtd/data/models/gtd_model_helpers.dart';
import 'package:guad/features/gtd/domain/entities/weekly_review.dart';

class WeeklyReviewModel {
  const WeeklyReviewModel({
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

  factory WeeklyReviewModel.fromJson(Map<String, dynamic> json) {
    return WeeklyReviewModel(
      id: intFromJson(json['id']),
      currentStep: _stepFromApi(json['currentStep'] as String?),
      startedAt: dateTimeFromJson(json['startedAt']),
      completedAt: dateTimeFromJson(json['completedAt']),
      notes: json['notes'] as String?,
    );
  }

  WeeklyReview toDomain() {
    return WeeklyReview(
      id: id,
      currentStep: currentStep,
      startedAt: startedAt,
      completedAt: completedAt,
      notes: notes,
    );
  }

  static WeeklyReviewStep _stepFromApi(String? value) {
    return switch (value) {
      'GET_INBOX_TO_ZERO' => WeeklyReviewStep.getInboxToZero,
      'REVIEW_NEXT_ACTIONS' => WeeklyReviewStep.reviewNextActions,
      'REVIEW_PREVIOUS_CALENDAR' => WeeklyReviewStep.reviewPreviousCalendar,
      'REVIEW_UPCOMING_CALENDAR' => WeeklyReviewStep.reviewUpcomingCalendar,
      'REVIEW_WAITING_FOR' => WeeklyReviewStep.reviewWaitingFor,
      'REVIEW_PROJECTS' => WeeklyReviewStep.reviewProjects,
      'REVIEW_GOALS' => WeeklyReviewStep.reviewGoals,
      'REVIEW_SOMEDAY_MAYBE' => WeeklyReviewStep.reviewSomedayMaybe,
      'COMPLETE' => WeeklyReviewStep.complete,
      _ => WeeklyReviewStep.collectLoosePapers,
    };
  }
}
