import 'package:go_router/go_router.dart';

import 'package:guad/features/gtd/presentation/inbox/inbox_screen.dart';

abstract class GtdRoutes {
  static const inbox = '/tabs/inbox';

  static final inboxTabRoute = GoRoute(
    path: inbox,
    builder: (_, _) => const InboxScreen(),
  );
}
