import 'package:guad/features/gtd/data/models/gtd_model_helpers.dart';
import 'package:guad/features/gtd/domain/entities/gtd_area.dart';

class GtdAreaModel {
  const GtdAreaModel({required this.id, required this.name, this.description});

  final int id;
  final String name;
  final String? description;

  factory GtdAreaModel.fromJson(Map<String, dynamic> json) {
    return GtdAreaModel(
      id: intFromJson(json['id']),
      name: json['name'] as String? ?? '',
      description: json['description'] as String?,
    );
  }

  GtdArea toDomain() {
    return GtdArea(id: id, name: name, description: description);
  }
}
