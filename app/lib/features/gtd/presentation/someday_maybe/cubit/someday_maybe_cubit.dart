import 'dart:async';

import 'package:equatable/equatable.dart';
import 'package:flutter_bloc/flutter_bloc.dart';

import 'package:guad/features/gtd/domain/entities/someday_maybe.dart';
import 'package:guad/features/gtd/domain/repositories/gtd_repository.dart';
import 'package:guad/features/gtd/domain/services/gtd_change_bus.dart';

part 'someday_maybe_state.dart';

class SomedayMaybeCubit extends Cubit<SomedayMaybeState> {
  SomedayMaybeCubit(this._repository, this._changeBus)
    : super(const SomedayMaybeState()) {
    _changesSubscription = _changeBus.stream.listen(_onGtdChange);
  }

  final GtdRepository _repository;
  final GtdChangeBus _changeBus;
  late final StreamSubscription<GtdChange> _changesSubscription;

  Future<void> load({bool silent = false}) async {
    if (!silent || state.data == null) {
      emit(
        state.copyWith(status: SomedayMaybeStatus.loading, clearError: true),
      );
    }
    try {
      final data = await _repository.getSomedayMaybe();
      emit(state.copyWith(status: SomedayMaybeStatus.loaded, data: data));
    } catch (_) {
      emit(
        state.copyWith(
          status: SomedayMaybeStatus.failure,
          errorMessage: 'Could not load someday maybe.',
        ),
      );
    }
  }

  void _onGtdChange(GtdChange change) {
    if (!change.affects(GtdCollection.somedayMaybe)) return;
    load(silent: true);
  }

  @override
  Future<void> close() {
    _changesSubscription.cancel();
    return super.close();
  }
}
