part of 'inbox_cubit.dart';

enum InboxStatus { initial, loading, loaded, failure }

class InboxState extends Equatable {
  const InboxState({
    this.status = InboxStatus.initial,
    this.items = const [],
    this.isMutating = false,
    this.errorMessage,
  });

  final InboxStatus status;
  final List<InboxItem> items;
  final bool isMutating;
  final String? errorMessage;

  bool get isLoading => status == InboxStatus.loading;
  bool get isEmpty => items.isEmpty;

  InboxState copyWith({
    InboxStatus? status,
    List<InboxItem>? items,
    bool? isMutating,
    String? errorMessage,
    bool clearError = false,
  }) {
    return InboxState(
      status: status ?? this.status,
      items: items ?? this.items,
      isMutating: isMutating ?? this.isMutating,
      errorMessage: clearError ? null : errorMessage ?? this.errorMessage,
    );
  }

  @override
  List<Object?> get props => [status, items, isMutating, errorMessage];
}
