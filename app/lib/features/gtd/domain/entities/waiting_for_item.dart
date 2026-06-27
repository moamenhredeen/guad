enum WaitingForStatus { waiting, resolved }

class WaitingForItem {
  const WaitingForItem({
    required this.id,
    required this.title,
    this.delegatedTo,
    this.notes,
    required this.status,
    this.projectName,
    this.projectId,
    this.createdAt,
  });

  final int id;
  final String title;
  final String? delegatedTo;
  final String? notes;
  final WaitingForStatus status;
  final String? projectName;
  final int? projectId;
  final DateTime? createdAt;
}
