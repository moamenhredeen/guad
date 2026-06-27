import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';

import 'package:guad/app/shell/splash_screen.dart';
import 'package:guad/app/shell/tabs_screen.dart';
import 'package:guad/config/router/app_router_notifier.dart';
import 'package:guad/config/router/screen_paths.dart';
import 'package:guad/features/auth/presentation/screens/login_screen.dart';
import 'package:guad/features/gtd/presentation/inbox/inbox_screen.dart';
import 'package:guad/features/notifications/presentation/screens/notifications_screen.dart';
import 'package:guad/features/profile/presentation/screens/account_screen.dart';
import 'package:guad/features/profile/presentation/screens/personal_info/edit_personal_info_screen.dart';
import 'package:guad/features/profile/presentation/screens/personal_info/personal_info_screen.dart';
import 'package:guad/features/profile/presentation/screens/profile_screen.dart';

final _rootNavigatorKey = GlobalKey<NavigatorState>();

GoRouter createRouter(AppRouterNotifier notifier) {
  return GoRouter(
    navigatorKey: _rootNavigatorKey,
    refreshListenable: notifier,
    initialLocation: ScreenPaths.splash,
    debugLogDiagnostics: true,
    redirect: (context, state) {
      final isAuthenticated = notifier.isAuthenticated;
      final loc = state.matchedLocation;

      if (notifier.isInitializing) {
        return loc == ScreenPaths.splash ? null : ScreenPaths.splash;
      }
      if (!isAuthenticated) {
        return loc == ScreenPaths.login ? null : ScreenPaths.login;
      }
      if (loc == ScreenPaths.splash || loc == ScreenPaths.login) {
        return ScreenPaths.inbox;
      }

      return null;
    },
    routes: [
      GoRoute(
        path: ScreenPaths.splash,
        pageBuilder: (_, _) => const NoTransitionPage(child: SplashScreen()),
      ),
      GoRoute(
        path: ScreenPaths.login,
        pageBuilder: (_, _) => const MaterialPage(child: LoginScreen()),
      ),
      GoRoute(
        path: ScreenPaths.notifications,
        builder: (_, _) => const NotificationsScreen(),
      ),
      GoRoute(
        path: ScreenPaths.personalInfo,
        builder: (_, _) => const PersonalInfoScreen(),
      ),
      GoRoute(
        path: ScreenPaths.editPersonalInfo,
        builder: (_, _) => const EditPersonalInfoScreen(),
      ),
      GoRoute(
        path: ScreenPaths.account,
        builder: (_, _) => const AccountScreen(),
      ),

      // Tab shell
      StatefulShellRoute.indexedStack(
        builder: (context, state, shell) => TabsScreen(navigationShell: shell),
        branches: [
          StatefulShellBranch(
            routes: [
              GoRoute(
                path: ScreenPaths.inbox,
                builder: (_, _) => const InboxScreen(),
              ),
            ],
          ),
          StatefulShellBranch(
            routes: [
              GoRoute(
                path: ScreenPaths.profile,
                builder: (_, _) => const ProfileScreen(),
              ),
            ],
          ),
        ],
      ),
    ],
  );
}
