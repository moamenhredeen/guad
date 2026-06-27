import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';

import 'package:guad/features/auth/presentation/screens/login_screen.dart';

abstract class AuthRoutes {
  static const login = '/login';

  static final routes = <RouteBase>[
    GoRoute(
      path: login,
      pageBuilder: (_, _) => const MaterialPage(child: LoginScreen()),
    ),
  ];
}
