import 'package:guad/features/gtd/data/models/gtd_action_model.dart';
import 'package:guad/features/gtd/data/models/gtd_project_model.dart';
import 'package:guad/features/gtd/domain/entities/someday_maybe.dart';

class SomedayMaybeModel {
  const SomedayMaybeModel({this.actions = const [], this.projects = const []});

  final List<GtdActionModel> actions;
  final List<GtdProjectModel> projects;

  factory SomedayMaybeModel.fromJson(Map<String, dynamic> json) {
    final actions = json['actions'];
    final projects = json['projects'];

    return SomedayMaybeModel(
      actions: actions is List
          ? actions
                .whereType<Map<String, dynamic>>()
                .map(GtdActionModel.fromJson)
                .toList()
          : const [],
      projects: projects is List
          ? projects
                .whereType<Map<String, dynamic>>()
                .map(GtdProjectModel.fromJson)
                .toList()
          : const [],
    );
  }

  SomedayMaybe toDomain() {
    return SomedayMaybe(
      actions: actions.map((action) => action.toDomain()).toList(),
      projects: projects.map((project) => project.toDomain()).toList(),
    );
  }
}
