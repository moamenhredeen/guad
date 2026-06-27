import 'package:guad/features/gtd/domain/entities/gtd_action.dart';
import 'package:guad/features/gtd/domain/entities/gtd_project.dart';

class SomedayMaybe {
  const SomedayMaybe({this.actions = const [], this.projects = const []});

  final List<GtdAction> actions;
  final List<GtdProject> projects;
}
