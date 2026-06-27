import 'package:dio/dio.dart';

import 'package:guad/features/gtd/data/models/gtd_action_model.dart';
import 'package:guad/features/gtd/data/models/gtd_area_model.dart';
import 'package:guad/features/gtd/data/models/gtd_context_model.dart';
import 'package:guad/features/gtd/data/models/gtd_dashboard_model.dart';
import 'package:guad/features/gtd/data/models/gtd_project_model.dart';
import 'package:guad/features/gtd/data/models/inbox_item_model.dart';
import 'package:guad/features/gtd/data/models/someday_maybe_model.dart';
import 'package:guad/features/gtd/data/models/waiting_for_item_model.dart';
import 'package:guad/features/gtd/data/models/weekly_review_model.dart';
import 'package:guad/features/gtd/domain/entities/gtd_action.dart';
import 'package:guad/features/gtd/domain/entities/gtd_project.dart';
import 'package:guad/features/gtd/domain/entities/inbox_item.dart';

class GtdRemoteDataSource {
  const GtdRemoteDataSource(this._dio);

  final Dio _dio;

  Future<List<InboxItemModel>> getInboxItems() async {
    final response = await _dio.get<Map<String, dynamic>>('inbox');
    final data = response.data?['data'];
    if (data is! List) return const [];

    return data
        .whereType<Map<String, dynamic>>()
        .map(InboxItemModel.fromJson)
        .toList();
  }

  Future<InboxItemModel> createInboxItem({
    required String title,
    String? description,
  }) async {
    final response = await _dio.post<Map<String, dynamic>>(
      'inbox',
      data: {
        'title': title,
        if (description != null && description.isNotEmpty)
          'description': description,
      },
    );
    return InboxItemModel.fromJson(
      response.data?['data'] as Map<String, dynamic>,
    );
  }

  Future<void> deleteInboxItem(int id) {
    return _dio.delete<void>('inbox/$id');
  }

  Future<void> processInboxItem({
    required int id,
    required InboxProcessInput input,
  }) {
    final body = <String, Object?>{
      'action': _processActionToApi(input.action),
      if (_isNotBlank(input.description)) 'description': input.description,
      if (_isNotBlank(input.notes)) 'notes': input.notes,
      if (input.projectId != null) 'projectId': input.projectId,
      if (input.areaId != null) 'areaId': input.areaId,
      if (_isNotBlank(input.delegatedTo)) 'delegatedTo': input.delegatedTo,
      if (input.contextIds.isNotEmpty) 'contextIds': input.contextIds,
    };

    return _dio.post<void>('inbox/$id/process', data: body);
  }

  Future<List<GtdActionModel>> getNextActions() async {
    final response = await _dio.get<Map<String, dynamic>>(
      'actions',
      queryParameters: {'status': _actionStatusToApi(GtdActionStatus.next)},
    );
    final data = response.data?['data'];
    if (data is! List) return const [];

    return data
        .whereType<Map<String, dynamic>>()
        .map(GtdActionModel.fromJson)
        .toList();
  }

  Future<GtdActionModel> createNextAction({
    required String description,
    String? notes,
  }) async {
    final response = await _dio.post<Map<String, dynamic>>(
      'actions',
      data: {
        'description': description,
        if (notes != null && notes.isNotEmpty) 'notes': notes,
      },
    );
    return GtdActionModel.fromJson(
      response.data?['data'] as Map<String, dynamic>,
    );
  }

  Future<GtdActionModel> completeAction(int id) async {
    final response = await _dio.patch<Map<String, dynamic>>(
      'actions/$id/complete',
    );
    return GtdActionModel.fromJson(
      response.data?['data'] as Map<String, dynamic>,
    );
  }

  Future<void> deleteAction(int id) {
    return _dio.delete<void>('actions/$id');
  }

  Future<GtdDashboardModel> getDashboard() async {
    final response = await _dio.get<Map<String, dynamic>>('dashboard');
    return GtdDashboardModel.fromJson(_dataMap(response));
  }

  Future<List<GtdProjectModel>> getProjects() async {
    final response = await _dio.get<Map<String, dynamic>>('projects');
    return _dataList(response).map(GtdProjectModel.fromJson).toList();
  }

  Future<GtdProjectModel> createProject({
    required String name,
    String? description,
    String? desiredOutcome,
    int? areaId,
    String? color,
  }) async {
    final body = <String, Object?>{
      'name': name,
      if (description != null && description.isNotEmpty)
        'description': description,
      if (desiredOutcome != null && desiredOutcome.isNotEmpty)
        'desiredOutcome': desiredOutcome,
      if (color != null && color.isNotEmpty) 'color': color,
    };
    if (areaId != null) body['areaId'] = areaId;

    final response = await _dio.post<Map<String, dynamic>>(
      'projects',
      data: body,
    );
    return GtdProjectModel.fromJson(_dataMap(response));
  }

  Future<void> deleteProject(int id) {
    return _dio.delete<void>('projects/$id');
  }

  Future<GtdProjectModel> changeProjectStatus({
    required int id,
    required GtdProjectStatus status,
  }) async {
    final response = await _dio.patch<Map<String, dynamic>>(
      'projects/$id/status',
      data: {'status': _projectStatusToApi(status)},
    );
    return GtdProjectModel.fromJson(_dataMap(response));
  }

  Future<List<GtdAreaModel>> getAreas() async {
    final response = await _dio.get<Map<String, dynamic>>('areas');
    return _dataList(response).map(GtdAreaModel.fromJson).toList();
  }

  Future<GtdAreaModel> createArea({
    required String name,
    String? description,
  }) async {
    final response = await _dio.post<Map<String, dynamic>>(
      'areas',
      data: {
        'name': name,
        if (description != null && description.isNotEmpty)
          'description': description,
      },
    );
    return GtdAreaModel.fromJson(_dataMap(response));
  }

  Future<void> deleteArea(int id) {
    return _dio.delete<void>('areas/$id');
  }

  Future<List<GtdContextModel>> getContexts() async {
    final response = await _dio.get<Map<String, dynamic>>('contexts');
    return _dataList(response).map(GtdContextModel.fromJson).toList();
  }

  Future<GtdContextModel> createContext({
    required String name,
    String? description,
    String? color,
    String? iconKey,
  }) async {
    final response = await _dio.post<Map<String, dynamic>>(
      'contexts',
      data: {
        'name': name,
        if (description != null && description.isNotEmpty)
          'description': description,
        if (color != null && color.isNotEmpty) 'color': color,
        if (iconKey != null && iconKey.isNotEmpty) 'iconKey': iconKey,
      },
    );
    return GtdContextModel.fromJson(_dataMap(response));
  }

  Future<void> deleteContext(int id) {
    return _dio.delete<void>('contexts/$id');
  }

  Future<List<WaitingForItemModel>> getWaitingForItems() async {
    final response = await _dio.get<Map<String, dynamic>>('waiting-for');
    return _dataList(response).map(WaitingForItemModel.fromJson).toList();
  }

  Future<WaitingForItemModel> createWaitingForItem({
    required String title,
    String? delegatedTo,
    String? notes,
    int? projectId,
  }) async {
    final body = <String, Object?>{
      'title': title,
      if (delegatedTo != null && delegatedTo.isNotEmpty)
        'delegatedTo': delegatedTo,
      if (notes != null && notes.isNotEmpty) 'notes': notes,
    };
    if (projectId != null) body['projectId'] = projectId;

    final response = await _dio.post<Map<String, dynamic>>(
      'waiting-for',
      data: body,
    );
    return WaitingForItemModel.fromJson(_dataMap(response));
  }

  Future<WaitingForItemModel> resolveWaitingForItem(int id) async {
    final response = await _dio.patch<Map<String, dynamic>>(
      'waiting-for/$id/resolve',
    );
    return WaitingForItemModel.fromJson(_dataMap(response));
  }

  Future<void> deleteWaitingForItem(int id) {
    return _dio.delete<void>('waiting-for/$id');
  }

  Future<SomedayMaybeModel> getSomedayMaybe() async {
    final response = await _dio.get<Map<String, dynamic>>('someday-maybe');
    return SomedayMaybeModel.fromJson(_dataMap(response));
  }

  Future<WeeklyReviewModel?> getCurrentWeeklyReview() async {
    final response = await _dio.get<Map<String, dynamic>>('reviews/current');
    if (response.statusCode == 204 || response.data?['data'] == null) {
      return null;
    }
    return WeeklyReviewModel.fromJson(_dataMap(response));
  }

  Future<WeeklyReviewModel?> getLastWeeklyReview() async {
    final response = await _dio.get<Map<String, dynamic>>('reviews/last');
    if (response.statusCode == 204 || response.data?['data'] == null) {
      return null;
    }
    return WeeklyReviewModel.fromJson(_dataMap(response));
  }

  Future<WeeklyReviewModel> startWeeklyReview() async {
    final response = await _dio.post<Map<String, dynamic>>('reviews');
    return WeeklyReviewModel.fromJson(_dataMap(response));
  }

  Future<WeeklyReviewModel> advanceWeeklyReviewStep(int id) async {
    final response = await _dio.patch<Map<String, dynamic>>('reviews/$id/step');
    return WeeklyReviewModel.fromJson(_dataMap(response));
  }

  Future<WeeklyReviewModel> completeWeeklyReview(int id) async {
    final response = await _dio.post<Map<String, dynamic>>(
      'reviews/$id/complete',
    );
    return WeeklyReviewModel.fromJson(_dataMap(response));
  }

  String _processActionToApi(InboxProcessAction action) {
    return switch (action) {
      InboxProcessAction.nextAction => 'NEXT_ACTION',
      InboxProcessAction.project => 'PROJECT',
      InboxProcessAction.waitingFor => 'WAITING_FOR',
      InboxProcessAction.somedayMaybe => 'SOMEDAY_MAYBE',
      InboxProcessAction.reference => 'REFERENCE',
      InboxProcessAction.trash => 'TRASH',
    };
  }

  String _actionStatusToApi(GtdActionStatus status) {
    return switch (status) {
      GtdActionStatus.next => 'NEXT',
      GtdActionStatus.inProgress => 'IN_PROGRESS',
      GtdActionStatus.completed => 'COMPLETED',
      GtdActionStatus.waitingFor => 'WAITING_FOR',
      GtdActionStatus.somedayMaybe => 'SOMEDAY_MAYBE',
      GtdActionStatus.scheduled => 'SCHEDULED',
    };
  }

  String _projectStatusToApi(GtdProjectStatus status) {
    return switch (status) {
      GtdProjectStatus.active => 'ACTIVE',
      GtdProjectStatus.completed => 'COMPLETED',
      GtdProjectStatus.somedayMaybe => 'SOMEDAY_MAYBE',
    };
  }

  Map<String, dynamic> _dataMap(Response<Map<String, dynamic>> response) {
    return response.data?['data'] as Map<String, dynamic>? ?? const {};
  }

  List<Map<String, dynamic>> _dataList(
    Response<Map<String, dynamic>> response,
  ) {
    final data = response.data?['data'];
    if (data is! List) return const [];
    return data.whereType<Map<String, dynamic>>().toList();
  }

  bool _isNotBlank(String? value) {
    return value?.trim().isNotEmpty == true;
  }
}
