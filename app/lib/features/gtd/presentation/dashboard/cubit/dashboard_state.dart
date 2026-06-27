part of 'dashboard_cubit.dart';

enum DashboardStatus { initial, loading, loaded, failure }

class DashboardState extends Equatable {
  const DashboardState({
    this.status = DashboardStatus.initial,
    this.dashboard,
    this.errorMessage,
  });

  final DashboardStatus status;
  final GtdDashboard? dashboard;
  final String? errorMessage;

  bool get isLoading => status == DashboardStatus.loading;

  DashboardState copyWith({
    DashboardStatus? status,
    GtdDashboard? dashboard,
    String? errorMessage,
    bool clearError = false,
  }) {
    return DashboardState(
      status: status ?? this.status,
      dashboard: dashboard ?? this.dashboard,
      errorMessage: clearError ? null : errorMessage ?? this.errorMessage,
    );
  }

  @override
  List<Object?> get props => [status, dashboard, errorMessage];
}
