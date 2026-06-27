import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:go_router/go_router.dart';

import 'package:guad/features/gtd/domain/repositories/gtd_repository.dart';
import 'package:guad/features/gtd/domain/services/gtd_change_bus.dart';
import 'package:guad/features/gtd/presentation/actions/actions_screen.dart';
import 'package:guad/features/gtd/presentation/actions/cubit/actions_cubit.dart';
import 'package:guad/features/gtd/presentation/areas/cubit/areas_cubit.dart';
import 'package:guad/features/gtd/presentation/areas/areas_screen.dart';
import 'package:guad/features/gtd/presentation/contexts/cubit/contexts_cubit.dart';
import 'package:guad/features/gtd/presentation/contexts/contexts_screen.dart';
import 'package:guad/features/gtd/presentation/dashboard/cubit/dashboard_cubit.dart';
import 'package:guad/features/gtd/presentation/dashboard/dashboard_screen.dart';
import 'package:guad/features/gtd/presentation/inbox/cubit/inbox_cubit.dart';
import 'package:guad/features/gtd/presentation/inbox/inbox_screen.dart';
import 'package:guad/features/gtd/presentation/projects/cubit/projects_cubit.dart';
import 'package:guad/features/gtd/presentation/projects/projects_screen.dart';
import 'package:guad/features/gtd/presentation/someday_maybe/cubit/someday_maybe_cubit.dart';
import 'package:guad/features/gtd/presentation/someday_maybe/someday_maybe_screen.dart';
import 'package:guad/features/gtd/presentation/waiting_for/cubit/waiting_for_cubit.dart';
import 'package:guad/features/gtd/presentation/waiting_for/waiting_for_screen.dart';
import 'package:guad/features/gtd/presentation/weekly_review/cubit/weekly_review_cubit.dart';
import 'package:guad/features/gtd/presentation/weekly_review/weekly_review_screen.dart';

abstract class GtdRoutes {
  static const dashboard = '/tabs/gtd';
  static const inbox = '/tabs/inbox';
  static const actions = '/tabs/actions';
  static const projects = '/gtd/projects';
  static const waitingFor = '/gtd/waiting-for';
  static const somedayMaybe = '/gtd/someday-maybe';
  static const areas = '/gtd/areas';
  static const contexts = '/gtd/contexts';
  static const weeklyReview = '/gtd/weekly-review';

  static final dashboardTabRoute = GoRoute(
    path: dashboard,
    builder: (context, _) => BlocProvider(
      create: (_) => DashboardCubit(
        context.read<GtdRepository>(),
        context.read<GtdChangeBus>(),
      )..load(),
      child: const DashboardScreen(),
    ),
  );

  static final inboxTabRoute = GoRoute(
    path: inbox,
    builder: (context, _) => BlocProvider(
      create: (_) => InboxCubit(
        context.read<GtdRepository>(),
        context.read<GtdChangeBus>(),
      )..load(),
      child: const InboxScreen(),
    ),
  );

  static final actionsTabRoute = GoRoute(
    path: actions,
    builder: (context, _) => BlocProvider(
      create: (_) => ActionsCubit(
        context.read<GtdRepository>(),
        context.read<GtdChangeBus>(),
      )..load(),
      child: const ActionsScreen(),
    ),
  );

  static final routes = [
    GoRoute(
      path: projects,
      builder: (context, _) => BlocProvider(
        create: (_) => ProjectsCubit(
          context.read<GtdRepository>(),
          context.read<GtdChangeBus>(),
        )..load(),
        child: const ProjectsScreen(),
      ),
    ),
    GoRoute(
      path: waitingFor,
      builder: (context, _) => BlocProvider(
        create: (_) => WaitingForCubit(
          context.read<GtdRepository>(),
          context.read<GtdChangeBus>(),
        )..load(),
        child: const WaitingForScreen(),
      ),
    ),
    GoRoute(
      path: somedayMaybe,
      builder: (context, _) => BlocProvider(
        create: (_) => SomedayMaybeCubit(
          context.read<GtdRepository>(),
          context.read<GtdChangeBus>(),
        )..load(),
        child: const SomedayMaybeScreen(),
      ),
    ),
    GoRoute(
      path: areas,
      builder: (context, _) => BlocProvider(
        create: (_) => AreasCubit(
          context.read<GtdRepository>(),
          context.read<GtdChangeBus>(),
        )..load(),
        child: const AreasScreen(),
      ),
    ),
    GoRoute(
      path: contexts,
      builder: (context, _) => BlocProvider(
        create: (_) => ContextsCubit(
          context.read<GtdRepository>(),
          context.read<GtdChangeBus>(),
        )..load(),
        child: const ContextsScreen(),
      ),
    ),
    GoRoute(
      path: weeklyReview,
      builder: (context, _) => BlocProvider(
        create: (_) => WeeklyReviewCubit(
          context.read<GtdRepository>(),
          context.read<GtdChangeBus>(),
        )..load(),
        child: const WeeklyReviewScreen(),
      ),
    ),
  ];
}
