import 'dart:async';

import 'package:equatable/equatable.dart';
import 'package:flutter_bloc/flutter_bloc.dart';

import 'package:guad/features/gtd/domain/entities/waiting_for_item.dart';
import 'package:guad/features/gtd/domain/repositories/gtd_repository.dart';
import 'package:guad/features/gtd/domain/services/gtd_change_bus.dart';

part 'waiting_for_state.dart';

class WaitingForCubit extends Cubit<WaitingForState> {
  WaitingForCubit(this._repository, this._changeBus)
    : super(const WaitingForState()) {
    _changesSubscription = _changeBus.stream.listen(_onGtdChange);
  }

  final GtdRepository _repository;
  final GtdChangeBus _changeBus;
  final Object _changeOrigin = Object();
  late final StreamSubscription<GtdChange> _changesSubscription;

  Future<void> load({bool silent = false}) async {
    if (!silent || state.items.isEmpty) {
      emit(
        state.copyWith(status: WaitingForStatusView.loading, clearError: true),
      );
    }
    try {
      final items = await _repository.getWaitingForItems();
      emit(state.copyWith(status: WaitingForStatusView.loaded, items: items));
    } catch (_) {
      emit(
        state.copyWith(
          status: WaitingForStatusView.failure,
          errorMessage: 'Could not load waiting-for items.',
        ),
      );
    }
  }

  Future<void> create({
    required String title,
    String? delegatedTo,
    String? notes,
  }) async {
    final trimmedTitle = title.trim();
    if (trimmedTitle.isEmpty) return;

    emit(state.copyWith(isMutating: true, clearError: true));
    try {
      final item = await _repository.createWaitingForItem(
        title: trimmedTitle,
        delegatedTo: _emptyToNull(delegatedTo),
        notes: _emptyToNull(notes),
      );
      emit(
        state.copyWith(
          status: WaitingForStatusView.loaded,
          items: [item, ...state.items],
          isMutating: false,
        ),
      );
      _notifyChanged();
    } catch (_) {
      emit(
        state.copyWith(
          isMutating: false,
          errorMessage: 'Could not create waiting-for item.',
        ),
      );
    }
  }

  Future<void> resolve(int id) async {
    final previousItems = state.items;
    emit(
      state.copyWith(
        items: previousItems.where((item) => item.id != id).toList(),
        clearError: true,
      ),
    );

    try {
      await _repository.resolveWaitingForItem(id);
      _notifyChanged();
    } catch (_) {
      emit(
        state.copyWith(
          items: previousItems,
          errorMessage: 'Could not resolve waiting-for item.',
        ),
      );
    }
  }

  Future<void> delete(int id) async {
    final previousItems = state.items;
    emit(
      state.copyWith(
        items: previousItems.where((item) => item.id != id).toList(),
        clearError: true,
      ),
    );

    try {
      await _repository.deleteWaitingForItem(id);
      _notifyChanged();
    } catch (_) {
      emit(
        state.copyWith(
          items: previousItems,
          errorMessage: 'Could not delete waiting-for item.',
        ),
      );
    }
  }

  void _onGtdChange(GtdChange change) {
    if (identical(change.source, _changeOrigin)) return;
    if (!change.affects(GtdCollection.waitingFor)) return;
    load(silent: true);
  }

  void _notifyChanged() {
    _changeBus.notify(
      GtdChange(
        collections: const {GtdCollection.waitingFor, GtdCollection.dashboard},
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
