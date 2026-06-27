import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:go_router/go_router.dart';

import 'package:guad/features/auth/presentation/bloc/auth_bloc.dart';
import 'package:guad/features/profile/presentation/profile_routes.dart';
import 'package:guad/gen/l10n/app_localizations.dart';

class PersonalInfoScreen extends StatelessWidget {
  const PersonalInfoScreen({super.key});

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context);
    final cs = Theme.of(context).colorScheme;
    final tt = Theme.of(context).textTheme;
    final user = context.select((AuthBloc bloc) => bloc.state.user);

    return Scaffold(
      appBar: AppBar(
        title: Text(l10n.personalInfoTitle),
        actions: [
          IconButton(
            icon: const Icon(Icons.edit_outlined),
            tooltip: 'Edit',
            onPressed: () => context.push(ProfileRoutes.editPersonalInfo),
          ),
        ],
      ),
      body: ListView(
        padding: EdgeInsets.zero,
        children: [
          Container(
            width: double.infinity,
            color: cs.surface,
            padding: const EdgeInsets.symmetric(vertical: 28),
            child: Column(
              children: [
                CircleAvatar(
                  radius: 44,
                  backgroundColor: cs.surfaceContainer,
                  child: Text(
                    user?.firstName.isNotEmpty == true
                        ? user!.firstName[0].toUpperCase()
                        : '?',
                    style: tt.displaySmall?.copyWith(
                      color: cs.onSurface,
                      fontWeight: FontWeight.w600,
                    ),
                  ),
                ),
                const SizedBox(height: 12),
                Text(
                  user?.fullName ?? '—',
                  style: tt.titleLarge?.copyWith(fontWeight: FontWeight.w600),
                ),
              ],
            ),
          ),

          Divider(color: cs.outlineVariant),

          _Field(
            icon: Icons.person_outline_rounded,
            label: l10n.personalInfoFirstName,
            value: user?.firstName ?? '—',
          ),
          Divider(indent: 72, color: cs.outlineVariant),
          _Field(
            icon: Icons.person_outline_rounded,
            label: l10n.personalInfoLastName,
            value: user?.lastName ?? '—',
          ),

          Divider(color: cs.outlineVariant),
        ],
      ),
    );
  }
}

class _Field extends StatelessWidget {
  final IconData icon;
  final String label;
  final String value;

  const _Field({required this.icon, required this.label, required this.value});

  @override
  Widget build(BuildContext context) {
    final cs = Theme.of(context).colorScheme;
    final tt = Theme.of(context).textTheme;

    return ListTile(
      leading: Icon(icon),
      title: Text(
        label,
        style: tt.bodySmall?.copyWith(color: cs.onSurfaceVariant),
      ),
      subtitle: Text(value, style: tt.bodyLarge?.copyWith(color: cs.onSurface)),
    );
  }
}
