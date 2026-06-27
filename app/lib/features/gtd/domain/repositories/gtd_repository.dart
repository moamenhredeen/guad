import 'package:guad/features/gtd/domain/entities/gtd_action.dart';
import 'package:guad/features/gtd/domain/entities/gtd_area.dart';
import 'package:guad/features/gtd/domain/entities/gtd_context.dart';
import 'package:guad/features/gtd/domain/entities/gtd_dashboard.dart';
import 'package:guad/features/gtd/domain/entities/gtd_project.dart';
import 'package:guad/features/gtd/domain/entities/inbox_item.dart';
import 'package:guad/features/gtd/domain/entities/someday_maybe.dart';
import 'package:guad/features/gtd/domain/entities/waiting_for_item.dart';
import 'package:guad/features/gtd/domain/entities/weekly_review.dart';

abstract class GtdRepository {
  Future<List<InboxItem>> getInboxItems();

  Future<InboxItem> createInboxItem({
    required String title,
    String? description,
  });

  Future<void> deleteInboxItem(int id);

  Future<void> processInboxItem({
    required int id,
    required InboxProcessInput input,
  });

  Future<List<GtdAction>> getNextActions();

  Future<GtdAction> createNextAction({
    required String description,
    String? notes,
  });

  Future<GtdAction> completeAction(int id);

  Future<void> deleteAction(int id);

  Future<GtdDashboard> getDashboard();

  Future<List<GtdProject>> getProjects();

  Future<GtdProject> createProject({
    required String name,
    String? description,
    String? desiredOutcome,
    int? areaId,
    String? color,
  });

  Future<void> deleteProject(int id);

  Future<GtdProject> changeProjectStatus({
    required int id,
    required GtdProjectStatus status,
  });

  Future<List<GtdArea>> getAreas();

  Future<GtdArea> createArea({required String name, String? description});

  Future<void> deleteArea(int id);

  Future<List<GtdContext>> getContexts();

  Future<GtdContext> createContext({
    required String name,
    String? description,
    String? color,
    String? iconKey,
  });

  Future<void> deleteContext(int id);

  Future<List<WaitingForItem>> getWaitingForItems();

  Future<WaitingForItem> createWaitingForItem({
    required String title,
    String? delegatedTo,
    String? notes,
    int? projectId,
  });

  Future<WaitingForItem> resolveWaitingForItem(int id);

  Future<void> deleteWaitingForItem(int id);

  Future<SomedayMaybe> getSomedayMaybe();

  Future<WeeklyReview?> getCurrentWeeklyReview();

  Future<WeeklyReview?> getLastWeeklyReview();

  Future<WeeklyReview> startWeeklyReview();

  Future<WeeklyReview> advanceWeeklyReviewStep(int id);

  Future<WeeklyReview> completeWeeklyReview(int id);
}
