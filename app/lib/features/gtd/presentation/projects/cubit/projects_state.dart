part of 'projects_cubit.dart';

enum ProjectsStatus { initial, loading, loaded, failure }

class ProjectsState extends Equatable {
  const ProjectsState({
    this.status = ProjectsStatus.initial,
    this.projects = const [],
    this.isMutating = false,
    this.errorMessage,
  });

  final ProjectsStatus status;
  final List<GtdProject> projects;
  final bool isMutating;
  final String? errorMessage;

  bool get isLoading => status == ProjectsStatus.loading;
  bool get isEmpty => projects.isEmpty;

  ProjectsState copyWith({
    ProjectsStatus? status,
    List<GtdProject>? projects,
    bool? isMutating,
    String? errorMessage,
    bool clearError = false,
  }) {
    return ProjectsState(
      status: status ?? this.status,
      projects: projects ?? this.projects,
      isMutating: isMutating ?? this.isMutating,
      errorMessage: clearError ? null : errorMessage ?? this.errorMessage,
    );
  }

  @override
  List<Object?> get props => [status, projects, isMutating, errorMessage];
}
