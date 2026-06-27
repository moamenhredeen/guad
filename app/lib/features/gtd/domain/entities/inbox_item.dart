enum InboxItemStatus { unprocessed, processing, processed }

enum InboxProcessAction {
  nextAction,
  project,
  waitingFor,
  somedayMaybe,
  reference,
  trash,
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
