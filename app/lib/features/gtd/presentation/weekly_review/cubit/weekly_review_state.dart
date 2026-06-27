part of 'weekly_review_cubit.dart';

enum WeeklyReviewStatus { initial, loading, loaded, failure }

class WeeklyReviewState extends Equatable {
  const WeeklyReviewState({
    this.status = WeeklyReviewStatus.initial,
    this.review,
    this.isMutating = false,
    this.errorMessage,
  });

  final WeeklyReviewStatus status;
  final WeeklyReview? review;
  final bool isMutating;
  final String? errorMessage;

  bool get isLoading => status == WeeklyReviewStatus.loading;

  WeeklyReviewState copyWith({
    WeeklyReviewStatus? status,
    WeeklyReview? review,
    bool? isMutating,
    String? errorMessage,
    bool clearReview = false,
    bool clearError = false,
  }) {
    return WeeklyReviewState(
      status: status ?? this.status,
      review: clearReview ? null : review ?? this.review,
      isMutating: isMutating ?? this.isMutating,
      errorMessage: clearError ? null : errorMessage ?? this.errorMessage,
    );
  }

  @override
  List<Object?> get props => [status, review, isMutating, errorMessage];
}
