import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:go_router/go_router.dart';

import 'package:guad/config/router/screen_paths.dart';
import 'package:guad/gen/l10n/app_localizations.dart';
import 'package:guad/features/auth/presentation/bloc/auth_bloc.dart';

class InboxScreen extends StatelessWidget {
  const InboxScreen({super.key});

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context);
    final cs = Theme.of(context).colorScheme;
    final tt = Theme.of(context).textTheme;
    final user = context.select((AuthBloc bloc) => bloc.state.user);

    return Scaffold(
      appBar: AppBar(
        title: Text(l10n.navInbox),
        actions: [
          IconButton(
            icon: const Icon(Icons.notifications_outlined),
            tooltip: l10n.notificationsTitle,
            onPressed: () => context.push(ScreenPaths.notifications),
          ),
          const SizedBox(width: 4),
        ],
      ),
      body: ListView(
        padding: const EdgeInsets.fromLTRB(16, 8, 16, 32),
        children: [
          if (user != null)
            Padding(
              padding: const EdgeInsets.only(bottom: 20),
              child: Text(
                l10n.inboxWelcome(user.firstName),
                style: tt.titleMedium?.copyWith(color: cs.onSurfaceVariant),
              ),
            ),
          _ActionTile(
            icon: Icons.person_outline_rounded,
            title: l10n.profilePersonalInfo,
            subtitle: user?.fullName ?? '',
            onTap: () => context.push(ScreenPaths.personalInfo),
          ),
          const SizedBox(height: 12),
          _ActionTile(
            icon: Icons.manage_accounts_outlined,
            title: l10n.profileAccountSettings,
            subtitle: user?.email ?? '',
            onTap: () => context.push(ScreenPaths.account),
          ),
          const SizedBox(height: 12),
          _ActionTile(
            icon: Icons.notifications_outlined,
            title: l10n.profileNotifications,
            subtitle: l10n.notificationsCaughtUp,
            onTap: () => context.push(ScreenPaths.notifications),
          ),
        ],
      ),
    );
  }
}

class _ActionTile extends StatelessWidget {
  const _ActionTile({
    required this.icon,
    required this.title,
    required this.subtitle,
    required this.onTap,
  });

  final IconData icon;
  final String title;
  final String subtitle;
  final VoidCallback onTap;

  @override
  Widget build(BuildContext context) {
    final cs = Theme.of(context).colorScheme;
    final tt = Theme.of(context).textTheme;

    return Material(
      color: cs.surfaceContainer,
      borderRadius: BorderRadius.circular(8),
      child: InkWell(
        borderRadius: BorderRadius.circular(8),
        onTap: onTap,
        child: Padding(
          padding: const EdgeInsets.all(16),
          child: Row(
            children: [
              Icon(icon, color: cs.primary),
              const SizedBox(width: 16),
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      title,
                      style: tt.titleMedium?.copyWith(
                        fontWeight: FontWeight.w600,
                      ),
                    ),
                    if (subtitle.isNotEmpty) ...[
                      const SizedBox(height: 4),
                      Text(
                        subtitle,
                        style: tt.bodySmall?.copyWith(
                          color: cs.onSurfaceVariant,
                        ),
                      ),
                    ],
                  ],
                ),
              ),
              const Icon(Icons.chevron_right_rounded),
            ],
          ),
        ),
      ),
    );
  }
}
