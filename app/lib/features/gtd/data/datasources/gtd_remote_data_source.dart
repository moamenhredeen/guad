import 'package:dio/dio.dart';

import 'package:guad/features/gtd/data/models/inbox_item_model.dart';
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
    required InboxProcessAction action,
  }) {
    return _dio.post<void>(
      'inbox/$id/process',
      data: {'action': _processActionToApi(action)},
    );
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
}
