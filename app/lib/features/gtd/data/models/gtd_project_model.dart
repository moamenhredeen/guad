import 'package:guad/features/gtd/data/models/gtd_model_helpers.dart';
import 'package:guad/features/gtd/domain/entities/gtd_project.dart';

class GtdProjectModel {
  const GtdProjectModel({
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

  factory GtdProjectModel.fromJson(Map<String, dynamic> json) {
    return GtdProjectModel(
      id: intFromJson(json['id']),
      name: json['name'] as String? ?? '',
      description: json['description'] as String?,
      desiredOutcome: json['desiredOutcome'] as String?,
      status: _statusFromApi(json['status'] as String?),
      areaName: json['areaName'] as String?,
      areaId: nullableIntFromJson(json['areaId']),
      color: json['color'] as String?,
      nextActionCount: intFromJson(json['nextActionCount']),
      createdAt: dateTimeFromJson(json['createdDate']),
    );
  }

  GtdProject toDomain() {
    return GtdProject(
      id: id,
      name: name,
      description: description,
      desiredOutcome: desiredOutcome,
      status: status,
      areaName: areaName,
      areaId: areaId,
      color: color,
      nextActionCount: nextActionCount,
      createdAt: createdAt,
    );
  }

  static GtdProjectStatus _statusFromApi(String? value) {
    return switch (value) {
      'COMPLETED' => GtdProjectStatus.completed,
      'SOMEDAY_MAYBE' => GtdProjectStatus.somedayMaybe,
      _ => GtdProjectStatus.active,
    };
  }
}
