import 'package:guad/features/gtd/domain/entities/inbox_item.dart';

abstract class GtdRepository {
  Future<List<InboxItem>> getInboxItems();

  Future<InboxItem> createInboxItem({
    required String title,
    String? description,
  });

  Future<void> deleteInboxItem(int id);

  Future<void> processInboxItem({
    required int id,
    required InboxProcessAction action,
  });
}
