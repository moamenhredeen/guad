import 'package:flutter/material.dart';

import 'package:guad/gen/l10n/app_localizations.dart';

class NotificationsScreen extends StatelessWidget {
  const NotificationsScreen({super.key});

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context);
    final cs = Theme.of(context).colorScheme;
    final tt = Theme.of(context).textTheme;

    return Scaffold(
      appBar: AppBar(title: Text(l10n.notificationsTitle)),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Icon(
              Icons.notifications_none_rounded,
              size: 64,
              color: cs.outlineVariant,
            ),
            const SizedBox(height: 16),
            Text(
              l10n.notificationsEmpty,
              style: tt.titleMedium?.copyWith(color: cs.onSurfaceVariant),
            ),
            const SizedBox(height: 4),
            Text(
              l10n.notificationsCaughtUp,
              style: tt.bodySmall?.copyWith(color: cs.outlineVariant),
            ),
          ],
        ),
      ),
    );
  }
}
