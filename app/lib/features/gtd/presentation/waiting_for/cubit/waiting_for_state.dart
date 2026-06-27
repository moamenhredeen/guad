part of 'waiting_for_cubit.dart';

enum WaitingForStatusView { initial, loading, loaded, failure }

class WaitingForState extends Equatable {
  const WaitingForState({
    this.status = WaitingForStatusView.initial,
    this.items = const [],
    this.isMutating = false,
    this.errorMessage,
  });

  final WaitingForStatusView status;
  final List<WaitingForItem> items;
  final bool isMutating;
  final String? errorMessage;

  bool get isLoading => status == WaitingForStatusView.loading;
  bool get isEmpty => items.isEmpty;

  WaitingForState copyWith({
    WaitingForStatusView? status,
    List<WaitingForItem>? items,
    bool? isMutating,
    String? errorMessage,
    bool clearError = false,
  }) {
    return WaitingForState(
      status: status ?? this.status,
      items: items ?? this.items,
      isMutating: isMutating ?? this.isMutating,
      errorMessage: clearError ? null : errorMessage ?? this.errorMessage,
    );
  }

  @override
  List<Object?> get props => [status, items, isMutating, errorMessage];
}
