import 'package:guad/features/gtd/data/models/gtd_model_helpers.dart';
import 'package:guad/features/gtd/domain/entities/gtd_action.dart';

class GtdActionModel {
  const GtdActionModel({
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

  factory GtdActionModel.fromJson(Map<String, dynamic> json) {
    return GtdActionModel(
      id: intFromJson(json['id']),
      description: json['description'] as String,
      notes: json['notes'] as String?,
      status: _statusFromApi(json['status'] as String?),
      energyLevel: nullableIntFromJson(json['energyLevel']),
      estimatedDuration: nullableIntFromJson(json['estimatedDuration']),
      projectName: json['projectName'] as String?,
      projectId: nullableIntFromJson(json['projectId']),
      areaName: json['areaName'] as String?,
      areaId: nullableIntFromJson(json['areaId']),
      createdAt: dateTimeFromJson(json['createdDate']),
      completedAt: dateTimeFromJson(json['completedDate']),
    );
  }

  GtdAction toDomain() {
    return GtdAction(
      id: id,
      description: description,
      notes: notes,
      status: status,
      energyLevel: energyLevel,
      estimatedDuration: estimatedDuration,
      projectName: projectName,
      projectId: projectId,
      areaName: areaName,
      areaId: areaId,
      createdAt: createdAt,
      completedAt: completedAt,
    );
  }

  static GtdActionStatus _statusFromApi(String? value) {
    return switch (value) {
      'IN_PROGRESS' => GtdActionStatus.inProgress,
      'COMPLETED' => GtdActionStatus.completed,
      'WAITING_FOR' => GtdActionStatus.waitingFor,
      'SOMEDAY_MAYBE' => GtdActionStatus.somedayMaybe,
      'SCHEDULED' => GtdActionStatus.scheduled,
      _ => GtdActionStatus.next,
    };
  }
}
