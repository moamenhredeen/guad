enum GtdActionStatus {
  next,
  inProgress,
  completed,
  waitingFor,
  somedayMaybe,
  scheduled,
}

class GtdAction {
  const GtdAction({
    required this.id,
    required this.description,
    this.notes,
    required this.status,
    this.energyLevel,
    this.estimatedDuration,
    this.projectName,
    this.projectId,
    this.areaName,
    this.areaId,
    this.createdAt,
    this.completedAt,
  });

  final int id;
  final String description;
  final String? notes;
  final GtdActionStatus status;
  final int? energyLevel;
  final int? estimatedDuration;
  final String? projectName;
  final int? projectId;
  final String? areaName;
  final int? areaId;
  final DateTime? createdAt;
  final DateTime? completedAt;
}
