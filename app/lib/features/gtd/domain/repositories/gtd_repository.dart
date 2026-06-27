import 'package:guad/features/gtd/domain/entities/gtd_action.dart';
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

  Future<List<GtdAction>> getNextActions();

  Future<GtdAction> createNextAction({
    required String description,
    String? notes,
  });

  Future<GtdAction> completeAction(int id);

  Future<void> deleteAction(int id);
}
