import 'dart:async';

import 'package:equatable/equatable.dart';
import 'package:flutter_bloc/flutter_bloc.dart';

import 'package:guad/features/gtd/domain/entities/gtd_context.dart';
import 'package:guad/features/gtd/domain/repositories/gtd_repository.dart';
import 'package:guad/features/gtd/domain/services/gtd_change_bus.dart';

part 'contexts_state.dart';

class ContextsCubit extends Cubit<ContextsState> {
  ContextsCubit(this._repository, this._changeBus)
    : super(const ContextsState()) {
    _changesSubscription = _changeBus.stream.listen(_onGtdChange);
  }

  final GtdRepository _repository;
  final GtdChangeBus _changeBus;
  final Object _changeOrigin = Object();
  late final StreamSubscription<GtdChange> _changesSubscription;

  Future<void> load({bool silent = false}) async {
    if (!silent || state.contexts.isEmpty) {
      emit(state.copyWith(status: ContextsStatus.loading, clearError: true));
    }
    try {
      final contexts = await _repository.getContexts();
      emit(state.copyWith(status: ContextsStatus.loaded, contexts: contexts));
    } catch (_) {
      emit(
        state.copyWith(
          status: ContextsStatus.failure,
          errorMessage: 'Could not load contexts.',
        ),
      );
    }
  }

  Future<void> create({required String name, String? description}) async {
    final trimmedName = name.trim();
    if (trimmedName.isEmpty) return;

    emit(state.copyWith(isMutating: true, clearError: true));
    try {
      final context = await _repository.createContext(
        name: trimmedName,
        description: _emptyToNull(description),
      );
      emit(
        state.copyWith(
          status: ContextsStatus.loaded,
          contexts: [context, ...state.contexts],
          isMutating: false,
        ),
      );
      _notifyChanged();
    } catch (_) {
      emit(
        state.copyWith(
          isMutating: false,
          errorMessage: 'Could not create context.',
        ),
      );
    }
  }

  Future<void> delete(int id) async {
    final previousContexts = state.contexts;
    emit(
      state.copyWith(
        contexts: previousContexts
            .where((context) => context.id != id)
            .toList(),
        clearError: true,
      ),
    );

    try {
      await _repository.deleteContext(id);
      _notifyChanged();
    } catch (_) {
      emit(
        state.copyWith(
          contexts: previousContexts,
          errorMessage: 'Could not delete context.',
        ),
      );
    }
  }

  void _onGtdChange(GtdChange change) {
    if (identical(change.source, _changeOrigin)) return;
    if (!change.affects(GtdCollection.contexts)) return;
    load(silent: true);
  }

  void _notifyChanged() {
    _changeBus.notify(
      GtdChange(
        collections: const {
          GtdCollection.contexts,
          GtdCollection.actions,
          GtdCollection.dashboard,
        },
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
