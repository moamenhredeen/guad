part of 'actions_cubit.dart';

enum ActionsStatus { initial, loading, loaded, failure }

class ActionsState extends Equatable {
  const ActionsState({
    this.status = ActionsStatus.initial,
    this.actions = const [],
    this.isMutating = false,
    this.errorMessage,
  });

  final ActionsStatus status;
  final List<GtdAction> actions;
  final bool isMutating;
  final String? errorMessage;

  bool get isLoading => status == ActionsStatus.loading;
  bool get isEmpty => actions.isEmpty;

  ActionsState copyWith({
    ActionsStatus? status,
    List<GtdAction>? actions,
    bool? isMutating,
    String? errorMessage,
    bool clearError = false,
  }) {
    return ActionsState(
      status: status ?? this.status,
      actions: actions ?? this.actions,
      isMutating: isMutating ?? this.isMutating,
      errorMessage: clearError ? null : errorMessage ?? this.errorMessage,
    );
  }

  @override
  List<Object?> get props => [status, actions, isMutating, errorMessage];
}
