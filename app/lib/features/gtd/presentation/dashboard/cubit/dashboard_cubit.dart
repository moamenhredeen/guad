import 'dart:async';

import 'package:equatable/equatable.dart';
import 'package:flutter_bloc/flutter_bloc.dart';

import 'package:guad/features/gtd/domain/entities/gtd_dashboard.dart';
import 'package:guad/features/gtd/domain/repositories/gtd_repository.dart';
import 'package:guad/features/gtd/domain/services/gtd_change_bus.dart';

part 'dashboard_state.dart';

class DashboardCubit extends Cubit<DashboardState> {
  DashboardCubit(this._repository, this._changeBus)
    : super(const DashboardState()) {
    _changesSubscription = _changeBus.stream.listen(_onGtdChange);
  }

  final GtdRepository _repository;
  final GtdChangeBus _changeBus;
  late final StreamSubscription<GtdChange> _changesSubscription;

  Future<void> load({bool silent = false}) async {
    if (!silent || state.dashboard == null) {
      emit(state.copyWith(status: DashboardStatus.loading, clearError: true));
    }
    try {
      final dashboard = await _repository.getDashboard();
      emit(
        state.copyWith(
          status: DashboardStatus.loaded,
          dashboard: dashboard,
          clearError: true,
        ),
      );
    } catch (_) {
      emit(
        state.copyWith(
          status: DashboardStatus.failure,
          errorMessage: 'Could not load dashboard.',
        ),
      );
    }
  }

  void _onGtdChange(GtdChange change) {
    if (!change.affects(GtdCollection.dashboard)) return;
    load(silent: true);
  }

  @override
  Future<void> close() {
    _changesSubscription.cancel();
    return super.close();
  }
}
