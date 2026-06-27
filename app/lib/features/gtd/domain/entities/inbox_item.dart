enum InboxItemStatus { unprocessed, processing, processed }

enum InboxProcessAction {
  nextAction,
  project,
  waitingFor,
  somedayMaybe,
  reference,
  trash,
}

class InboxProcessInput {
  const InboxProcessInput({
    required this.action,
    this.description,
    this.notes,
    this.projectId,
    this.areaId,
    this.delegatedTo,
    this.contextIds = const [],
  });

  final InboxProcessAction action;
  final String? description;
  final String? notes;
  final int? projectId;
  final int? areaId;
  final String? delegatedTo;
  final List<int> contextIds;
}

class InboxItem {
  const InboxItem({
    required this.id,
    required this.title,
    this.description,
    required this.status,
    this.createdAt,
  });

  final int id;
  final String title;
  final String? description;
  final InboxItemStatus status;
  final DateTime? createdAt;
}
