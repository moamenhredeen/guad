enum GtdProjectStatus { active, completed, somedayMaybe }

class GtdProject {
  const GtdProject({
    required this.id,
    required this.name,
    this.description,
    this.desiredOutcome,
    required this.status,
    this.areaName,
    this.areaId,
    this.color,
    this.nextActionCount = 0,
    this.createdAt,
  });

  final int id;
  final String name;
  final String? description;
  final String? desiredOutcome;
  final GtdProjectStatus status;
  final String? areaName;
  final int? areaId;
  final String? color;
  final int nextActionCount;
  final DateTime? createdAt;
}
