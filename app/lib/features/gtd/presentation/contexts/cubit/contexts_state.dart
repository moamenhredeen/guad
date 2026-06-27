part of 'contexts_cubit.dart';

enum ContextsStatus { initial, loading, loaded, failure }

class ContextsState extends Equatable {
  const ContextsState({
    this.status = ContextsStatus.initial,
    this.contexts = const [],
    this.isMutating = false,
    this.errorMessage,
  });

  final ContextsStatus status;
  final List<GtdContext> contexts;
  final bool isMutating;
  final String? errorMessage;

  bool get isLoading => status == ContextsStatus.loading;
  bool get isEmpty => contexts.isEmpty;

  ContextsState copyWith({
    ContextsStatus? status,
    List<GtdContext>? contexts,
    bool? isMutating,
    String? errorMessage,
    bool clearError = false,
  }) {
    return ContextsState(
      status: status ?? this.status,
      contexts: contexts ?? this.contexts,
      isMutating: isMutating ?? this.isMutating,
      errorMessage: clearError ? null : errorMessage ?? this.errorMessage,
    );
  }

  @override
  List<Object?> get props => [status, contexts, isMutating, errorMessage];
}
