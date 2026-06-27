import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:go_router/go_router.dart';

import 'package:guad/features/gtd/domain/entities/gtd_dashboard.dart';
import 'package:guad/features/gtd/presentation/dashboard/cubit/dashboard_cubit.dart';
import 'package:guad/features/gtd/presentation/gtd_routes.dart';
import 'package:guad/features/notifications/presentation/notifications_routes.dart';

class DashboardScreen extends StatelessWidget {
  const DashboardScreen({super.key});

  @override
  Widget build(BuildContext context) {
    final cs = Theme.of(context).colorScheme;

    return BlocConsumer<DashboardCubit, DashboardState>(
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
          appBar: AppBar(
            title: const Text('GTD'),
            actions: [
              IconButton(
                icon: const Icon(Icons.notifications_outlined),
                tooltip: 'Notifications',
                onPressed: () =>
                    context.push(NotificationsRoutes.notifications),
              ),
              const SizedBox(width: 4),
            ],
          ),
          body: _DashboardBody(state: state),
        );
      },
    );
  }
}

class _DashboardBody extends StatelessWidget {
  const _DashboardBody({required this.state});

  final DashboardState state;

  @override
  Widget build(BuildContext context) {
    if (state.isLoading && state.dashboard == null) {
      return const Center(child: CircularProgressIndicator());
    }

    final dashboard = state.dashboard;
    if (dashboard == null) {
      return RefreshIndicator(
        onRefresh: () => context.read<DashboardCubit>().load(),
        child: ListView(
          physics: const AlwaysScrollableScrollPhysics(),
          children: const [
            SizedBox(height: 160),
            Center(child: Text('Dashboard unavailable')),
          ],
        ),
      );
    }

    return RefreshIndicator(
      onRefresh: () => context.read<DashboardCubit>().load(),
      child: ListView(
        padding: const EdgeInsets.fromLTRB(20, 8, 20, 96),
        children: [
          _ReviewPrompt(dashboard: dashboard),
          const SizedBox(height: 12),
          _StatsGrid(dashboard: dashboard),
          const SizedBox(height: 20),
          _SectionTitle(title: 'Organize'),
          _DestinationTile(
            icon: Icons.folder_outlined,
            title: 'Projects',
            subtitle: '${dashboard.activeProjectsCount} active',
            route: GtdRoutes.projects,
          ),
          _DestinationTile(
            icon: Icons.hourglass_empty_rounded,
            title: 'Waiting for',
            subtitle: '${dashboard.waitingForCount} delegated',
            route: GtdRoutes.waitingFor,
          ),
          _DestinationTile(
            icon: Icons.lightbulb_outline_rounded,
            title: 'Someday maybe',
            subtitle: '${dashboard.somedayMaybeActionsCount} actions',
            route: GtdRoutes.somedayMaybe,
          ),
          const SizedBox(height: 16),
          _SectionTitle(title: 'Setup'),
          _DestinationTile(
            icon: Icons.track_changes_rounded,
            title: 'Areas',
            subtitle: 'Responsibilities and standards',
            route: GtdRoutes.areas,
          ),
          _DestinationTile(
            icon: Icons.alternate_email_rounded,
            title: 'Contexts',
            subtitle: 'Places, tools, and modes',
            route: GtdRoutes.contexts,
          ),
          _DestinationTile(
            icon: Icons.fact_check_outlined,
            title: 'Weekly review',
            subtitle: dashboard.weeklyReviewDue ? 'Due now' : 'On track',
            route: GtdRoutes.weeklyReview,
          ),
        ],
      ),
    );
  }
}

class _ReviewPrompt extends StatelessWidget {
  const _ReviewPrompt({required this.dashboard});

  final GtdDashboard dashboard;

  @override
  Widget build(BuildContext context) {
    final cs = Theme.of(context).colorScheme;
    final tt = Theme.of(context).textTheme;
    final text = dashboard.weeklyReviewDue
        ? 'Weekly review is due'
        : 'Weekly review is current';

    return Material(
      color: dashboard.weeklyReviewDue
          ? cs.primaryContainer
          : cs.surfaceContainerHigh,
      borderRadius: BorderRadius.circular(8),
      child: InkWell(
        borderRadius: BorderRadius.circular(8),
        onTap: () => context.push(GtdRoutes.weeklyReview),
        child: Padding(
          padding: const EdgeInsets.all(16),
          child: Row(
            children: [
              Icon(
                dashboard.weeklyReviewDue
                    ? Icons.notification_important_outlined
                    : Icons.verified_outlined,
                color: dashboard.weeklyReviewDue
                    ? cs.onPrimaryContainer
                    : cs.onSurfaceVariant,
              ),
              const SizedBox(width: 12),
              Expanded(
                child: Text(
                  text,
                  style: tt.titleMedium?.copyWith(
                    color: dashboard.weeklyReviewDue
                        ? cs.onPrimaryContainer
                        : cs.onSurface,
                    fontWeight: FontWeight.w600,
                  ),
                ),
              ),
              Icon(Icons.chevron_right_rounded, color: cs.onSurfaceVariant),
            ],
          ),
        ),
      ),
    );
  }
}

class _StatsGrid extends StatelessWidget {
  const _StatsGrid({required this.dashboard});

  final GtdDashboard dashboard;

  @override
  Widget build(BuildContext context) {
    return GridView.count(
      crossAxisCount: 2,
      shrinkWrap: true,
      physics: const NeverScrollableScrollPhysics(),
      childAspectRatio: 1.9,
      mainAxisSpacing: 8,
      crossAxisSpacing: 8,
      children: [
        _StatTile(label: 'Inbox', value: dashboard.inboxCount),
        _StatTile(label: 'Next', value: dashboard.nextActionsCount),
        _StatTile(label: 'Projects', value: dashboard.activeProjectsCount),
        _StatTile(label: 'Waiting', value: dashboard.waitingForCount),
      ],
    );
  }
}

class _StatTile extends StatelessWidget {
  const _StatTile({required this.label, required this.value});

  final String label;
  final int value;

  @override
  Widget build(BuildContext context) {
    final cs = Theme.of(context).colorScheme;
    final tt = Theme.of(context).textTheme;

    return DecoratedBox(
      decoration: BoxDecoration(
        color: cs.surfaceContainerHighest,
        borderRadius: BorderRadius.circular(8),
      ),
      child: Padding(
        padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 12),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Text(
              '$value',
              style: tt.headlineSmall?.copyWith(
                color: cs.onSurface,
                fontWeight: FontWeight.w700,
              ),
            ),
            Text(label, style: tt.bodySmall?.copyWith(color: cs.outline)),
          ],
        ),
      ),
    );
  }
}

class _SectionTitle extends StatelessWidget {
  const _SectionTitle({required this.title});

  final String title;

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.fromLTRB(4, 0, 4, 6),
      child: Text(
        title,
        style: Theme.of(context).textTheme.labelLarge?.copyWith(
          color: Theme.of(context).colorScheme.onSurfaceVariant,
        ),
      ),
    );
  }
}

class _DestinationTile extends StatelessWidget {
  const _DestinationTile({
    required this.icon,
    required this.title,
    required this.subtitle,
    required this.route,
  });

  final IconData icon;
  final String title;
  final String subtitle;
  final String route;

  @override
  Widget build(BuildContext context) {
    final cs = Theme.of(context).colorScheme;

    return ListTile(
      contentPadding: const EdgeInsets.symmetric(horizontal: 4),
      leading: Icon(icon, color: cs.onSurfaceVariant),
      title: Text(title),
      subtitle: Text(subtitle),
      trailing: const Icon(Icons.chevron_right_rounded),
      onTap: () => context.push(route),
    );
  }
}
