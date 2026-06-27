import 'package:go_router/go_router.dart';

import 'package:guad/features/notifications/presentation/screens/notifications_screen.dart';

abstract class NotificationsRoutes {
  static const notifications = '/notifications';

  static final routes = <RouteBase>[
    GoRoute(
      path: notifications,
      builder: (_, _) => const NotificationsScreen(),
    ),
  ];
}
