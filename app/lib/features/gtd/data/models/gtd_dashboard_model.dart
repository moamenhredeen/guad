import 'package:guad/features/gtd/data/models/gtd_model_helpers.dart';
import 'package:guad/features/gtd/domain/entities/gtd_dashboard.dart';

class GtdDashboardModel {
  const GtdDashboardModel({
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

  factory GtdDashboardModel.fromJson(Map<String, dynamic> json) {
    return GtdDashboardModel(
      inboxCount: intFromJson(json['inboxCount']),
      nextActionsCount: intFromJson(json['nextActionsCount']),
      activeProjectsCount: intFromJson(json['activeProjectsCount']),
      waitingForCount: intFromJson(json['waitingForCount']),
      somedayMaybeActionsCount: intFromJson(json['somedayMaybeActionsCount']),
      weeklyReviewDue: json['weeklyReviewDue'] == true,
      lastReviewDate: dateTimeFromJson(json['lastReviewDate']),
    );
  }

  GtdDashboard toDomain() {
    return GtdDashboard(
      inboxCount: inboxCount,
      nextActionsCount: nextActionsCount,
      activeProjectsCount: activeProjectsCount,
      waitingForCount: waitingForCount,
      somedayMaybeActionsCount: somedayMaybeActionsCount,
      weeklyReviewDue: weeklyReviewDue,
      lastReviewDate: lastReviewDate,
    );
  }
}
