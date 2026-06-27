import 'package:go_router/go_router.dart';

import 'package:guad/features/profile/presentation/screens/account_screen.dart';
import 'package:guad/features/profile/presentation/screens/personal_info/edit_personal_info_screen.dart';
import 'package:guad/features/profile/presentation/screens/personal_info/personal_info_screen.dart';
import 'package:guad/features/profile/presentation/screens/profile_screen.dart';

abstract class ProfileRoutes {
  static const profile = '/tabs/profile';
  static const personalInfo = '/personal-info';
  static const editPersonalInfo = '/personal-info/edit';
  static const account = '/account';

  static final routes = <RouteBase>[
    GoRoute(path: personalInfo, builder: (_, _) => const PersonalInfoScreen()),
    GoRoute(
      path: editPersonalInfo,
      builder: (_, _) => const EditPersonalInfoScreen(),
    ),
    GoRoute(path: account, builder: (_, _) => const AccountScreen()),
  ];

  static final profileTabRoute = GoRoute(
    path: profile,
    builder: (_, _) => const ProfileScreen(),
  );
}
