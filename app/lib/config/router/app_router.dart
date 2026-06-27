import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';

import 'package:guad/app/shell/splash_screen.dart';
import 'package:guad/app/shell/tabs_screen.dart';
import 'package:guad/config/router/app_router_notifier.dart';
import 'package:guad/features/auth/presentation/auth_routes.dart';
import 'package:guad/features/gtd/presentation/gtd_routes.dart';
import 'package:guad/features/notifications/presentation/notifications_routes.dart';
import 'package:guad/features/profile/presentation/profile_routes.dart';

abstract class AppRoutes {
  static const splash = '/';
}

final _rootNavigatorKey = GlobalKey<NavigatorState>();

GoRouter createRouter(AppRouterNotifier notifier) {
  return GoRouter(
    navigatorKey: _rootNavigatorKey,
    refreshListenable: notifier,
    initialLocation: AppRoutes.splash,
    debugLogDiagnostics: true,
    redirect: (context, state) {
      final isAuthenticated = notifier.isAuthenticated;
      final loc = state.matchedLocation;

      if (notifier.isInitializing) {
        return loc == AppRoutes.splash ? null : AppRoutes.splash;
      }
      if (!isAuthenticated) {
        return loc == AuthRoutes.login ? null : AuthRoutes.login;
      }
      if (loc == AppRoutes.splash || loc == AuthRoutes.login) {
        return GtdRoutes.inbox;
      }

      return null;
    },
    routes: [
      GoRoute(
        path: AppRoutes.splash,
        pageBuilder: (_, _) => const NoTransitionPage(child: SplashScreen()),
      ),
      ...AuthRoutes.routes,
      ...NotificationsRoutes.routes,
      ...ProfileRoutes.routes,

      // Tab shell
      StatefulShellRoute.indexedStack(
        builder: (context, state, shell) => TabsScreen(navigationShell: shell),
        branches: [
          StatefulShellBranch(routes: [GtdRoutes.inboxTabRoute]),
          StatefulShellBranch(routes: [GtdRoutes.actionsTabRoute]),
          StatefulShellBranch(routes: [ProfileRoutes.profileTabRoute]),
        ],
      ),
    ],
  );
}
