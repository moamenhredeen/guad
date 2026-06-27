import 'dart:async';

import 'package:equatable/equatable.dart';
import 'package:flutter_bloc/flutter_bloc.dart';

import 'package:guad/features/gtd/domain/entities/weekly_review.dart';
import 'package:guad/features/gtd/domain/repositories/gtd_repository.dart';
import 'package:guad/features/gtd/domain/services/gtd_change_bus.dart';

part 'weekly_review_state.dart';

class WeeklyReviewCubit extends Cubit<WeeklyReviewState> {
  WeeklyReviewCubit(this._repository, this._changeBus)
    : super(const WeeklyReviewState()) {
    _changesSubscription = _changeBus.stream.listen(_onGtdChange);
  }

  final GtdRepository _repository;
  final GtdChangeBus _changeBus;
  final Object _changeOrigin = Object();
  late final StreamSubscription<GtdChange> _changesSubscription;

  Future<void> load({bool silent = false}) async {
    if (!silent || state.review == null) {
      emit(
        state.copyWith(status: WeeklyReviewStatus.loading, clearError: true),
      );
    }
    try {
      final review = await _repository.getCurrentWeeklyReview();
      emit(
        state.copyWith(
          status: WeeklyReviewStatus.loaded,
          review: review,
          clearReview: review == null,
        ),
      );
    } catch (_) {
      emit(
        state.copyWith(
          status: WeeklyReviewStatus.failure,
          errorMessage: 'Could not load weekly review.',
        ),
      );
    }
  }

  Future<void> start() async {
    emit(state.copyWith(isMutating: true, clearError: true));
    try {
      final review = await _repository.startWeeklyReview();
      emit(
        state.copyWith(
          status: WeeklyReviewStatus.loaded,
          review: review,
          isMutating: false,
        ),
      );
      _notifyDashboardChanged();
    } catch (_) {
      emit(
        state.copyWith(
          isMutating: false,
          errorMessage: 'Could not start weekly review.',
        ),
      );
    }
  }

  Future<void> advance() async {
    final review = state.review;
    if (review == null || review.isComplete) return;
    emit(state.copyWith(isMutating: true, clearError: true));
    try {
      final updated = await _repository.advanceWeeklyReviewStep(review.id);
      emit(state.copyWith(review: updated, isMutating: false));
      _notifyDashboardChanged();
    } catch (_) {
      emit(
        state.copyWith(
          isMutating: false,
          errorMessage: 'Could not advance weekly review.',
        ),
      );
    }
  }

  Future<void> complete() async {
    final review = state.review;
    if (review == null || review.isComplete) return;
    emit(state.copyWith(isMutating: true, clearError: true));
    try {
      final updated = await _repository.completeWeeklyReview(review.id);
      emit(state.copyWith(review: updated, isMutating: false));
      _notifyDashboardChanged();
    } catch (_) {
      emit(
        state.copyWith(
          isMutating: false,
          errorMessage: 'Could not complete weekly review.',
        ),
      );
    }
  }

  void _onGtdChange(GtdChange change) {
    if (identical(change.source, _changeOrigin)) return;
    if (!change.affects(GtdCollection.dashboard)) return;
    load(silent: true);
  }

  void _notifyDashboardChanged() {
    _changeBus.notify(
      GtdChange(
        collections: const {GtdCollection.dashboard},
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
