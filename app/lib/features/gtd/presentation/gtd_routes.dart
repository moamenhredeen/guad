import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:go_router/go_router.dart';

import 'package:guad/features/gtd/domain/repositories/gtd_repository.dart';
import 'package:guad/features/gtd/domain/services/gtd_change_bus.dart';
import 'package:guad/features/gtd/presentation/actions/actions_screen.dart';
import 'package:guad/features/gtd/presentation/actions/cubit/actions_cubit.dart';
import 'package:guad/features/gtd/presentation/inbox/cubit/inbox_cubit.dart';
import 'package:guad/features/gtd/presentation/inbox/inbox_screen.dart';

abstract class GtdRoutes {
  static const inbox = '/tabs/inbox';
  static const actions = '/tabs/actions';

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
}
