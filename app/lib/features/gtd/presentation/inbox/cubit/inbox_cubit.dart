import 'package:equatable/equatable.dart';
import 'package:flutter_bloc/flutter_bloc.dart';

import 'package:guad/features/gtd/domain/entities/capture.dart';
import 'package:guad/features/gtd/domain/repositories/gtd_repository.dart';
import 'package:guad/features/gtd/domain/services/gtd_change_bus.dart';

part 'inbox_state.dart';

class InboxCubit extends Cubit<InboxState> {
  InboxCubit(this._repository, this._changeBus) : super(const InboxState());

  final GtdRepository _repository;
  final GtdChangeBus _changeBus;
  final Object _changeOrigin = Object();

  Future<void> load() async {
    emit(state.copyWith(status: InboxStatus.loading, clearError: true));
    try {
      final items = await _repository.getCaptures();
      emit(state.copyWith(status: InboxStatus.loaded, items: items));
    } catch (_) {
      emit(
        state.copyWith(
          status: InboxStatus.failure,
          errorMessage: 'Could not load inbox.',
        ),
      );
    }
  }

  Future<void> create({required String title, String? description}) async {
    final trimmedTitle = title.trim();
    final trimmedDescription = description?.trim();
    if (trimmedTitle.isEmpty) return;

    emit(state.copyWith(isMutating: true, clearError: true));
    try {
      final item = await _repository.createCapture(
        title: trimmedTitle,
        description: trimmedDescription?.isEmpty == true
            ? null
            : trimmedDescription,
      );
      emit(
        state.copyWith(
          status: InboxStatus.loaded,
          items: [item, ...state.items],
          isMutating: false,
        ),
      );
      _changeBus.notify(
        GtdChange(
          collections: const {GtdCollection.inbox, GtdCollection.dashboard},
          source: _changeOrigin,
        ),
      );
    } catch (_) {
      emit(
        state.copyWith(
          isMutating: false,
          errorMessage: 'Could not add inbox item.',
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
      await _repository.deleteCapture(id);
      _changeBus.notify(
        GtdChange(
          collections: const {GtdCollection.inbox, GtdCollection.dashboard},
          source: _changeOrigin,
        ),
      );
    } catch (_) {
      emit(
        state.copyWith(
          items: previousItems,
          errorMessage: 'Could not delete inbox item.',
        ),
      );
    }
  }

  Future<void> process({
    required int id,
    required InboxProcessInput input,
  }) async {
    final previousItems = state.items;
    emit(
      state.copyWith(
        items: previousItems.where((item) => item.id != id).toList(),
        clearError: true,
      ),
    );

    try {
      await _repository.processCapture(id: id, input: input);
      _changeBus.notify(
        GtdChange(
          collections: _collectionsAffectedBy(input.action),
          source: _changeOrigin,
        ),
      );
    } catch (_) {
      emit(
        state.copyWith(
          items: previousItems,
          errorMessage: 'Could not process inbox item.',
        ),
      );
    }
  }

  Set<GtdCollection> _collectionsAffectedBy(InboxProcessAction action) {
    return switch (action) {
      InboxProcessAction.nextAction => const {
        GtdCollection.inbox,
        GtdCollection.actions,
        GtdCollection.dashboard,
      },
      InboxProcessAction.project => const {
        GtdCollection.inbox,
        GtdCollection.projects,
        GtdCollection.dashboard,
      },
      InboxProcessAction.waitingFor => const {
        GtdCollection.inbox,
        GtdCollection.waitingFor,
        GtdCollection.dashboard,
      },
      InboxProcessAction.somedayMaybe => const {
        GtdCollection.inbox,
        GtdCollection.somedayMaybe,
        GtdCollection.dashboard,
      },
      InboxProcessAction.reference => const {
        GtdCollection.inbox,
        GtdCollection.reference,
        GtdCollection.dashboard,
      },
      InboxProcessAction.trash => const {
        GtdCollection.inbox,
        GtdCollection.dashboard,
      },
    };
  }
}
