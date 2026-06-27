import 'dart:async';

import 'package:equatable/equatable.dart';
import 'package:flutter_bloc/flutter_bloc.dart';

import 'package:guad/features/gtd/domain/entities/gtd_project.dart';
import 'package:guad/features/gtd/domain/repositories/gtd_repository.dart';
import 'package:guad/features/gtd/domain/services/gtd_change_bus.dart';

part 'projects_state.dart';

class ProjectsCubit extends Cubit<ProjectsState> {
  ProjectsCubit(this._repository, this._changeBus)
    : super(const ProjectsState()) {
    _changesSubscription = _changeBus.stream.listen(_onGtdChange);
  }

  final GtdRepository _repository;
  final GtdChangeBus _changeBus;
  final Object _changeOrigin = Object();
  late final StreamSubscription<GtdChange> _changesSubscription;

  Future<void> load({bool silent = false}) async {
    if (!silent || state.projects.isEmpty) {
      emit(state.copyWith(status: ProjectsStatus.loading, clearError: true));
    }
    try {
      final projects = await _repository.getProjects();
      emit(state.copyWith(status: ProjectsStatus.loaded, projects: projects));
    } catch (_) {
      emit(
        state.copyWith(
          status: ProjectsStatus.failure,
          errorMessage: 'Could not load projects.',
        ),
      );
    }
  }

  Future<void> create({
    required String name,
    String? description,
    String? desiredOutcome,
  }) async {
    final trimmedName = name.trim();
    if (trimmedName.isEmpty) return;

    emit(state.copyWith(isMutating: true, clearError: true));
    try {
      final project = await _repository.createProject(
        name: trimmedName,
        description: _emptyToNull(description),
        desiredOutcome: _emptyToNull(desiredOutcome),
      );
      emit(
        state.copyWith(
          status: ProjectsStatus.loaded,
          projects: [project, ...state.projects],
          isMutating: false,
        ),
      );
      _notifyChanged();
    } catch (_) {
      emit(
        state.copyWith(
          isMutating: false,
          errorMessage: 'Could not create project.',
        ),
      );
    }
  }

  Future<void> delete(int id) async {
    final previousProjects = state.projects;
    emit(
      state.copyWith(
        projects: previousProjects
            .where((project) => project.id != id)
            .toList(),
        clearError: true,
      ),
    );

    try {
      await _repository.deleteProject(id);
      _notifyChanged();
    } catch (_) {
      emit(
        state.copyWith(
          projects: previousProjects,
          errorMessage: 'Could not delete project.',
        ),
      );
    }
  }

  void _onGtdChange(GtdChange change) {
    if (identical(change.source, _changeOrigin)) return;
    if (!change.affects(GtdCollection.projects)) return;
    load(silent: true);
  }

  void _notifyChanged() {
    _changeBus.notify(
      GtdChange(
        collections: const {GtdCollection.projects, GtdCollection.dashboard},
        source: _changeOrigin,
      ),
    );
  }

  @override
  Future<void> close() {
    _changesSubscription.cancel();
    return super.close();
  }
}

String? _emptyToNull(String? value) {
  final trimmed = value?.trim();
  return trimmed == null || trimmed.isEmpty ? null : trimmed;
}
