import 'dart:async';

import 'package:equatable/equatable.dart';
import 'package:flutter_bloc/flutter_bloc.dart';

import 'package:guad/features/gtd/domain/entities/gtd_area.dart';
import 'package:guad/features/gtd/domain/repositories/gtd_repository.dart';
import 'package:guad/features/gtd/domain/services/gtd_change_bus.dart';

part 'areas_state.dart';

class AreasCubit extends Cubit<AreasState> {
  AreasCubit(this._repository, this._changeBus) : super(const AreasState()) {
    _changesSubscription = _changeBus.stream.listen(_onGtdChange);
  }

  final GtdRepository _repository;
  final GtdChangeBus _changeBus;
  final Object _changeOrigin = Object();
  late final StreamSubscription<GtdChange> _changesSubscription;

  Future<void> load({bool silent = false}) async {
    if (!silent || state.areas.isEmpty) {
      emit(state.copyWith(status: AreasStatus.loading, clearError: true));
    }
    try {
      final areas = await _repository.getAreas();
      emit(state.copyWith(status: AreasStatus.loaded, areas: areas));
    } catch (_) {
      emit(
        state.copyWith(
          status: AreasStatus.failure,
          errorMessage: 'Could not load areas.',
        ),
      );
    }
  }

  Future<void> create({required String name, String? description}) async {
    final trimmedName = name.trim();
    if (trimmedName.isEmpty) return;

    emit(state.copyWith(isMutating: true, clearError: true));
    try {
      final area = await _repository.createArea(
        name: trimmedName,
        description: _emptyToNull(description),
      );
      emit(
        state.copyWith(
          status: AreasStatus.loaded,
          areas: [area, ...state.areas],
          isMutating: false,
        ),
      );
      _notifyChanged();
    } catch (_) {
      emit(
        state.copyWith(
          isMutating: false,
          errorMessage: 'Could not create area.',
        ),
      );
    }
  }

  Future<void> delete(int id) async {
    final previousAreas = state.areas;
    emit(
      state.copyWith(
        areas: previousAreas.where((area) => area.id != id).toList(),
        clearError: true,
      ),
    );

    try {
      await _repository.deleteArea(id);
      _notifyChanged();
    } catch (_) {
      emit(
        state.copyWith(
          areas: previousAreas,
          errorMessage: 'Could not delete area.',
        ),
      );
    }
  }

  void _onGtdChange(GtdChange change) {
    if (identical(change.source, _changeOrigin)) return;
    if (!change.affects(GtdCollection.areas)) return;
    load(silent: true);
  }

  void _notifyChanged() {
    _changeBus.notify(
      GtdChange(
        collections: const {
          GtdCollection.areas,
          GtdCollection.projects,
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
