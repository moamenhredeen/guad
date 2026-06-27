import 'package:guad/features/gtd/data/models/gtd_model_helpers.dart';
import 'package:guad/features/gtd/domain/entities/waiting_for_item.dart';

class WaitingForItemModel {
  const WaitingForItemModel({
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

  factory WaitingForItemModel.fromJson(Map<String, dynamic> json) {
    return WaitingForItemModel(
      id: intFromJson(json['id']),
      title: json['title'] as String? ?? '',
      delegatedTo: json['delegatedTo'] as String?,
      notes: json['notes'] as String?,
      status: json['status'] == 'RESOLVED'
          ? WaitingForStatus.resolved
          : WaitingForStatus.waiting,
      projectName: json['projectName'] as String?,
      projectId: nullableIntFromJson(json['projectId']),
      createdAt: dateTimeFromJson(json['createdDate']),
    );
  }

  WaitingForItem toDomain() {
    return WaitingForItem(
      id: id,
      title: title,
      delegatedTo: delegatedTo,
      notes: notes,
      status: status,
      projectName: projectName,
      projectId: projectId,
      createdAt: createdAt,
    );
  }
}
