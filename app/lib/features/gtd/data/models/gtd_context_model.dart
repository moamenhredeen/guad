import 'package:guad/features/gtd/data/models/gtd_model_helpers.dart';
import 'package:guad/features/gtd/domain/entities/gtd_context.dart';

class GtdContextModel {
  const GtdContextModel({
    required this.id,
    required this.name,
    this.description,
    this.color,
    this.iconKey,
  });

  final int id;
  final String name;
  final String? description;
  final String? color;
  final String? iconKey;

  factory GtdContextModel.fromJson(Map<String, dynamic> json) {
    return GtdContextModel(
      id: intFromJson(json['id']),
      name: json['name'] as String? ?? '',
      description: json['description'] as String?,
      color: json['color'] as String?,
      iconKey: json['iconKey'] as String?,
    );
  }

  GtdContext toDomain() {
    return GtdContext(
      id: id,
      name: name,
      description: description,
      color: color,
      iconKey: iconKey,
    );
  }
}
