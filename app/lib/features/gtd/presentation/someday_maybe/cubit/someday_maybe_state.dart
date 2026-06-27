part of 'someday_maybe_cubit.dart';

enum SomedayMaybeStatus { initial, loading, loaded, failure }

class SomedayMaybeState extends Equatable {
  const SomedayMaybeState({
    this.status = SomedayMaybeStatus.initial,
    this.data,
    this.errorMessage,
  });

  final SomedayMaybeStatus status;
  final SomedayMaybe? data;
  final String? errorMessage;

  bool get isLoading => status == SomedayMaybeStatus.loading;
  bool get isEmpty =>
      data == null || (data!.actions.isEmpty && data!.projects.isEmpty);

  SomedayMaybeState copyWith({
    SomedayMaybeStatus? status,
    SomedayMaybe? data,
    String? errorMessage,
    bool clearError = false,
  }) {
    return SomedayMaybeState(
      status: status ?? this.status,
      data: data ?? this.data,
      errorMessage: clearError ? null : errorMessage ?? this.errorMessage,
    );
  }

  @override
  List<Object?> get props => [status, data, errorMessage];
}
