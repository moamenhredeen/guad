import 'package:equatable/equatable.dart';
import 'package:flutter_bloc/flutter_bloc.dart';

import 'package:guad/features/gtd/domain/entities/inbox_item.dart';
import 'package:guad/features/gtd/domain/repositories/gtd_repository.dart';

part 'inbox_state.dart';

class InboxCubit extends Cubit<InboxState> {
  InboxCubit(this._repository) : super(const InboxState());

  final GtdRepository _repository;

  Future<void> load() async {
    emit(state.copyWith(status: InboxStatus.loading, clearError: true));
    try {
      final items = await _repository.getInboxItems();
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
      final item = await _repository.createInboxItem(
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
      await _repository.deleteInboxItem(id);
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
    required InboxProcessAction action,
  }) async {
    final previousItems = state.items;
    emit(
      state.copyWith(
        items: previousItems.where((item) => item.id != id).toList(),
        clearError: true,
      ),
    );

    try {
      await _repository.processInboxItem(id: id, action: action);
    } catch (_) {
      emit(
        state.copyWith(
          items: previousItems,
          errorMessage: 'Could not process inbox item.',
        ),
      );
    }
  }
}
