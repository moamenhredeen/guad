import 'dart:async';

import 'package:equatable/equatable.dart';
import 'package:flutter_bloc/flutter_bloc.dart';

import 'package:guad/features/gtd/domain/entities/gtd_action.dart';
import 'package:guad/features/gtd/domain/repositories/gtd_repository.dart';
import 'package:guad/features/gtd/domain/services/gtd_change_bus.dart';

part 'actions_state.dart';

class ActionsCubit extends Cubit<ActionsState> {
  ActionsCubit(this._repository, this._changeBus) : super(const ActionsState()) {
    _changesSubscription = _changeBus.stream.listen(_onGtdChange);
  }

  final GtdRepository _repository;
  final GtdChangeBus _changeBus;
  final Object _changeOrigin = Object();
  late final StreamSubscription<GtdChange> _changesSubscription;

  Future<void> load({bool silent = false}) async {
    if (silent && state.actions.isNotEmpty) {
      emit(state.copyWith(clearError: true));
    } else {
      emit(state.copyWith(status: ActionsStatus.loading, clearError: true));
    }
    try {
      final actions = await _repository.getNextActions();
      emit(state.copyWith(status: ActionsStatus.loaded, actions: actions));
    } catch (_) {
      emit(
        state.copyWith(
          status: ActionsStatus.failure,
          errorMessage: 'Could not load actions.',
        ),
      );
    }
  }

  Future<void> create({required String description, String? notes}) async {
    final trimmedDescription = description.trim();
    final trimmedNotes = notes?.trim();
    if (trimmedDescription.isEmpty) return;

    emit(state.copyWith(isMutating: true, clearError: true));
    try {
      final action = await _repository.createNextAction(
        description: trimmedDescription,
        notes: trimmedNotes?.isEmpty == true ? null : trimmedNotes,
      );
      emit(
        state.copyWith(
          status: ActionsStatus.loaded,
          actions: [action, ...state.actions],
          isMutating: false,
        ),
      );
      _notifyActionsChanged();
    } catch (_) {
      emit(
        state.copyWith(
          isMutating: false,
          errorMessage: 'Could not add action.',
        ),
      );
    }
  }

  Future<void> complete(int id) async {
    final previousActions = state.actions;
    emit(
      state.copyWith(
        actions: previousActions.where((action) => action.id != id).toList(),
        clearError: true,
      ),
    );

    try {
      await _repository.completeAction(id);
      _notifyActionsChanged();
    } catch (_) {
      emit(
        state.copyWith(
          actions: previousActions,
          errorMessage: 'Could not complete action.',
        ),
      );
    }
  }

  Future<void> delete(int id) async {
    final previousActions = state.actions;
    emit(
      state.copyWith(
        actions: previousActions.where((action) => action.id != id).toList(),
        clearError: true,
      ),
    );

    try {
      await _repository.deleteAction(id);
      _notifyActionsChanged();
    } catch (_) {
      emit(
        state.copyWith(
          actions: previousActions,
          errorMessage: 'Could not delete action.',
        ),
      );
    }
  }

  void _onGtdChange(GtdChange change) {
    if (identical(change.source, _changeOrigin)) return;
    if (!change.affects(GtdCollection.actions)) return;
    load(silent: true);
  }

  void _notifyActionsChanged() {
    _changeBus.notify(
      GtdChange(
        collections: const {GtdCollection.actions, GtdCollection.dashboard},
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
