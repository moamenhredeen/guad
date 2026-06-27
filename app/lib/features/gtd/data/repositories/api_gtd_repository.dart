import 'package:guad/features/gtd/data/datasources/gtd_remote_data_source.dart';
import 'package:guad/features/gtd/domain/entities/gtd_action.dart';
import 'package:guad/features/gtd/domain/entities/gtd_area.dart';
import 'package:guad/features/gtd/domain/entities/gtd_context.dart';
import 'package:guad/features/gtd/domain/entities/gtd_dashboard.dart';
import 'package:guad/features/gtd/domain/entities/gtd_project.dart';
import 'package:guad/features/gtd/domain/entities/inbox_item.dart';
import 'package:guad/features/gtd/domain/entities/someday_maybe.dart';
import 'package:guad/features/gtd/domain/entities/waiting_for_item.dart';
import 'package:guad/features/gtd/domain/entities/weekly_review.dart';
import 'package:guad/features/gtd/domain/repositories/gtd_repository.dart';

class ApiGtdRepository implements GtdRepository {
  const ApiGtdRepository(this._remoteDataSource);

  final GtdRemoteDataSource _remoteDataSource;

  @override
  Future<List<InboxItem>> getInboxItems() async {
    final items = await _remoteDataSource.getInboxItems();
    return items.map((item) => item.toDomain()).toList();
  }

  @override
  Future<InboxItem> createInboxItem({
    required String title,
    String? description,
  }) async {
    final item = await _remoteDataSource.createInboxItem(
      title: title,
      description: description,
    );
    return item.toDomain();
  }

  @override
  Future<void> deleteInboxItem(int id) {
    return _remoteDataSource.deleteInboxItem(id);
  }

  @override
  Future<void> processInboxItem({
    required int id,
    required InboxProcessInput input,
  }) {
    return _remoteDataSource.processInboxItem(id: id, input: input);
  }

  @override
  Future<List<GtdAction>> getNextActions() async {
    final actions = await _remoteDataSource.getNextActions();
    return actions.map((action) => action.toDomain()).toList();
  }

  @override
  Future<GtdAction> createNextAction({
    required String description,
    String? notes,
  }) async {
    final action = await _remoteDataSource.createNextAction(
      description: description,
      notes: notes,
    );
    return action.toDomain();
  }

  @override
  Future<GtdAction> completeAction(int id) async {
    final action = await _remoteDataSource.completeAction(id);
    return action.toDomain();
  }

  @override
  Future<void> deleteAction(int id) {
    return _remoteDataSource.deleteAction(id);
  }

  @override
  Future<GtdDashboard> getDashboard() async {
    final dashboard = await _remoteDataSource.getDashboard();
    return dashboard.toDomain();
  }

  @override
  Future<List<GtdProject>> getProjects() async {
    final projects = await _remoteDataSource.getProjects();
    return projects.map((project) => project.toDomain()).toList();
  }

  @override
  Future<GtdProject> createProject({
    required String name,
    String? description,
    String? desiredOutcome,
    int? areaId,
    String? color,
  }) async {
    final project = await _remoteDataSource.createProject(
      name: name,
      description: description,
      desiredOutcome: desiredOutcome,
      areaId: areaId,
      color: color,
    );
    return project.toDomain();
  }

  @override
  Future<void> deleteProject(int id) {
    return _remoteDataSource.deleteProject(id);
  }

  @override
  Future<GtdProject> changeProjectStatus({
    required int id,
    required GtdProjectStatus status,
  }) async {
    final project = await _remoteDataSource.changeProjectStatus(
      id: id,
      status: status,
    );
    return project.toDomain();
  }

  @override
  Future<List<GtdArea>> getAreas() async {
    final areas = await _remoteDataSource.getAreas();
    return areas.map((area) => area.toDomain()).toList();
  }

  @override
  Future<GtdArea> createArea({
    required String name,
    String? description,
  }) async {
    final area = await _remoteDataSource.createArea(
      name: name,
      description: description,
    );
    return area.toDomain();
  }

  @override
  Future<void> deleteArea(int id) {
    return _remoteDataSource.deleteArea(id);
  }

  @override
  Future<List<GtdContext>> getContexts() async {
    final contexts = await _remoteDataSource.getContexts();
    return contexts.map((context) => context.toDomain()).toList();
  }

  @override
  Future<GtdContext> createContext({
    required String name,
    String? description,
    String? color,
    String? iconKey,
  }) async {
    final context = await _remoteDataSource.createContext(
      name: name,
      description: description,
      color: color,
      iconKey: iconKey,
    );
    return context.toDomain();
  }

  @override
  Future<void> deleteContext(int id) {
    return _remoteDataSource.deleteContext(id);
  }

  @override
  Future<List<WaitingForItem>> getWaitingForItems() async {
    final items = await _remoteDataSource.getWaitingForItems();
    return items.map((item) => item.toDomain()).toList();
  }

  @override
  Future<WaitingForItem> createWaitingForItem({
    required String title,
    String? delegatedTo,
    String? notes,
    int? projectId,
  }) async {
    final item = await _remoteDataSource.createWaitingForItem(
      title: title,
      delegatedTo: delegatedTo,
      notes: notes,
      projectId: projectId,
    );
    return item.toDomain();
  }

  @override
  Future<WaitingForItem> resolveWaitingForItem(int id) async {
    final item = await _remoteDataSource.resolveWaitingForItem(id);
    return item.toDomain();
  }

  @override
  Future<void> deleteWaitingForItem(int id) {
    return _remoteDataSource.deleteWaitingForItem(id);
  }

  @override
  Future<SomedayMaybe> getSomedayMaybe() async {
    final somedayMaybe = await _remoteDataSource.getSomedayMaybe();
    return somedayMaybe.toDomain();
  }

  @override
  Future<WeeklyReview?> getCurrentWeeklyReview() async {
    final review = await _remoteDataSource.getCurrentWeeklyReview();
    return review?.toDomain();
  }

  @override
  Future<WeeklyReview?> getLastWeeklyReview() async {
    final review = await _remoteDataSource.getLastWeeklyReview();
    return review?.toDomain();
  }

  @override
  Future<WeeklyReview> startWeeklyReview() async {
    final review = await _remoteDataSource.startWeeklyReview();
    return review.toDomain();
  }

  @override
  Future<WeeklyReview> advanceWeeklyReviewStep(int id) async {
    final review = await _remoteDataSource.advanceWeeklyReviewStep(id);
    return review.toDomain();
  }

  @override
  Future<WeeklyReview> completeWeeklyReview(int id) async {
    final review = await _remoteDataSource.completeWeeklyReview(id);
    return review.toDomain();
  }
}
