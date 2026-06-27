import 'package:guad/features/gtd/data/datasources/gtd_remote_data_source.dart';
import 'package:guad/features/gtd/domain/entities/inbox_item.dart';
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
    required InboxProcessAction action,
  }) {
    return _remoteDataSource.processInboxItem(id: id, action: action);
  }
}
