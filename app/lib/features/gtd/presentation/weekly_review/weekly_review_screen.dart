import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';

import 'package:guad/features/gtd/domain/entities/weekly_review.dart';
import 'package:guad/features/gtd/presentation/shared/gtd_empty_list.dart';
import 'package:guad/features/gtd/presentation/weekly_review/cubit/weekly_review_cubit.dart';
import 'package:guad/features/gtd/presentation/weekly_review/widgets/weekly_review_step_label.dart';

class WeeklyReviewScreen extends StatelessWidget {
  const WeeklyReviewScreen({super.key});

  @override
  Widget build(BuildContext context) {
    final cs = Theme.of(context).colorScheme;

    return BlocConsumer<WeeklyReviewCubit, WeeklyReviewState>(
      listenWhen: (previous, current) =>
          previous.errorMessage != current.errorMessage &&
          current.errorMessage != null,
      listener: (context, state) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Text(state.errorMessage!),
            behavior: SnackBarBehavior.floating,
            backgroundColor: cs.errorContainer,
            showCloseIcon: true,
          ),
        );
      },
      builder: (context, state) {
        return Scaffold(
          appBar: AppBar(title: const Text('Weekly review')),
          body: _WeeklyReviewBody(state: state),
        );
      },
    );
  }
}

class _WeeklyReviewBody extends StatelessWidget {
  const _WeeklyReviewBody({required this.state});

  final WeeklyReviewState state;

  @override
  Widget build(BuildContext context) {
    if (state.isLoading) {
      return const Center(child: CircularProgressIndicator());
    }

    final review = state.review;
    if (review == null) {
      return RefreshIndicator(
        onRefresh: () => context.read<WeeklyReviewCubit>().load(),
        child: ListView(
          physics: const AlwaysScrollableScrollPhysics(),
          padding: const EdgeInsets.all(20),
          children: [
            const GtdEmptyList(title: 'No active review'),
            const SizedBox(height: 20),
            FilledButton.icon(
              onPressed: state.isMutating
                  ? null
                  : () => context.read<WeeklyReviewCubit>().start(),
              icon: const Icon(Icons.play_arrow_rounded),
              label: const Text('Start review'),
            ),
          ],
        ),
      );
    }

    return RefreshIndicator(
      onRefresh: () => context.read<WeeklyReviewCubit>().load(),
      child: _ReviewSteps(review: review, isMutating: state.isMutating),
    );
  }
}

class _ReviewSteps extends StatelessWidget {
  const _ReviewSteps({required this.review, required this.isMutating});

  final WeeklyReview review;
  final bool isMutating;

  @override
  Widget build(BuildContext context) {
    return ListView(
      padding: const EdgeInsets.fromLTRB(20, 8, 20, 96),
      children: [
        Text(
          weeklyReviewStepLabel(review.currentStep),
          style: Theme.of(context).textTheme.headlineSmall,
        ),
        const SizedBox(height: 8),
        Text(
          'Move through the GTD review checklist and complete it when everything is current.',
          style: Theme.of(context).textTheme.bodyMedium?.copyWith(
            color: Theme.of(context).colorScheme.onSurfaceVariant,
          ),
        ),
        const SizedBox(height: 20),
        FilledButton.icon(
          onPressed: review.isComplete || isMutating
              ? null
              : () => context.read<WeeklyReviewCubit>().advance(),
          icon: const Icon(Icons.arrow_forward_rounded),
          label: const Text('Next step'),
        ),
        const SizedBox(height: 8),
        OutlinedButton.icon(
          onPressed: review.isComplete || isMutating
              ? null
              : () => context.read<WeeklyReviewCubit>().complete(),
          icon: const Icon(Icons.done_all_rounded),
          label: const Text('Complete review'),
        ),
      ],
    );
  }
}
