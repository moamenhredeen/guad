part of 'areas_cubit.dart';

enum AreasStatus { initial, loading, loaded, failure }

class AreasState extends Equatable {
  const AreasState({
    this.status = AreasStatus.initial,
    this.areas = const [],
    this.isMutating = false,
    this.errorMessage,
  });

  final AreasStatus status;
  final List<GtdArea> areas;
  final bool isMutating;
  final String? errorMessage;

  bool get isLoading => status == AreasStatus.loading;
  bool get isEmpty => areas.isEmpty;

  AreasState copyWith({
    AreasStatus? status,
    List<GtdArea>? areas,
    bool? isMutating,
    String? errorMessage,
    bool clearError = false,
  }) {
    return AreasState(
      status: status ?? this.status,
      areas: areas ?? this.areas,
      isMutating: isMutating ?? this.isMutating,
      errorMessage: clearError ? null : errorMessage ?? this.errorMessage,
    );
  }

  @override
  List<Object?> get props => [status, areas, isMutating, errorMessage];
}
