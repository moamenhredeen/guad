import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:go_router/go_router.dart';

import 'package:guad/config/router/screen_paths.dart';
import 'package:guad/gen/l10n/app_localizations.dart';
import 'package:guad/features/auth/presentation/bloc/auth_bloc.dart';
import 'package:guad/presentation/blocs/locale/locale_cubit.dart';

class ProfileScreen extends StatelessWidget {
  const ProfileScreen({super.key});

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context);
    final cs = Theme.of(context).colorScheme;
    final tt = Theme.of(context).textTheme;
    final user = context.select((AuthBloc bloc) => bloc.state.user);

    return Scaffold(
      appBar: AppBar(title: Text(l10n.profileTitle)),
      body: ListView(
        children: [
          Container(
            width: double.infinity,
            color: cs.primaryContainer,
            padding: const EdgeInsets.symmetric(vertical: 32),
            child: Column(
              children: [
                CircleAvatar(
                  radius: 40,
                  backgroundColor: cs.primary,
                  child: Text(
                    user?.firstName.isNotEmpty == true
                        ? user!.firstName[0].toUpperCase()
                        : '?',
                    style: tt.displaySmall?.copyWith(
                      color: cs.onPrimary,
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                ),
                const SizedBox(height: 12),
                Text(
                  user?.fullName ?? '—',
                  style: tt.titleLarge?.copyWith(
                    color: cs.onPrimaryContainer,
                    fontWeight: FontWeight.w600,
                  ),
                ),
                const SizedBox(height: 4),
                Text(
                  user?.email ?? '',
                  style: tt.bodyMedium?.copyWith(
                    color: cs.onPrimaryContainer.withValues(alpha: 0.7),
                  ),
                ),
              ],
            ),
          ),

          _SectionHeader(l10n.profileAccount),
          ListTile(
            leading: const Icon(Icons.person_outline_rounded),
            title: Text(l10n.profilePersonalInfo),
            trailing: const Icon(Icons.chevron_right_rounded),
            onTap: () => context.push(ScreenPaths.personalInfo),
          ),
          ListTile(
            leading: const Icon(Icons.manage_accounts_outlined),
            title: Text(l10n.profileAccountSettings),
            trailing: const Icon(Icons.chevron_right_rounded),
            onTap: () => context.push(ScreenPaths.account),
          ),
          ListTile(
            leading: const Icon(Icons.notifications_outlined),
            title: Text(l10n.profileNotifications),
            trailing: const Icon(Icons.chevron_right_rounded),
            onTap: () => context.push(ScreenPaths.notifications),
          ),

          _SectionHeader(l10n.profileApp),
          ListTile(
            leading: const Icon(Icons.language_outlined),
            title: Text(l10n.profileSettings),
            trailing: const Icon(Icons.chevron_right_rounded),
            onTap: () => _showLanguageSheet(context),
          ),
          ListTile(
            leading: const Icon(Icons.help_outline_rounded),
            title: Text(l10n.profileHelp),
            trailing: const Icon(Icons.chevron_right_rounded),
            onTap: () {},
          ),

          const _SectionHeader(''),
          ListTile(
            leading: Icon(Icons.logout_rounded, color: cs.error),
            title: Text(l10n.profileSignOut, style: TextStyle(color: cs.error)),
            onTap: () =>
                context.read<AuthBloc>().add(const AuthLogoutRequested()),
          ),
          const SizedBox(height: 16),
        ],
      ),
    );
  }
}

void _showLanguageSheet(BuildContext context) {
  final cs = Theme.of(context).colorScheme;
  final current = context.read<LocaleCubit>().state.languageCode;

  showModalBottomSheet(
    context: context,
    builder: (_) => SafeArea(
      child: Column(
        mainAxisSize: MainAxisSize.min,
        children: [
          const SizedBox(height: 8),
          _LanguageTile(
            label: 'English',
            selected: current == 'en',
            cs: cs,
            onTap: () {
              context.read<LocaleCubit>().setLocale(const Locale('en'));
              Navigator.pop(context);
            },
          ),
          _LanguageTile(
            label: 'العربية',
            selected: current == 'ar',
            cs: cs,
            onTap: () {
              context.read<LocaleCubit>().setLocale(const Locale('ar'));
              Navigator.pop(context);
            },
          ),
          const SizedBox(height: 8),
        ],
      ),
    ),
  );
}

class _LanguageTile extends StatelessWidget {
  final String label;
  final bool selected;
  final ColorScheme cs;
  final VoidCallback onTap;
  const _LanguageTile({
    required this.label,
    required this.selected,
    required this.cs,
    required this.onTap,
  });

  @override
  Widget build(BuildContext context) {
    return ListTile(
      title: Text(label),
      trailing: selected
          ? Icon(Icons.check_circle_rounded, color: cs.primary)
          : Icon(
              Icons.radio_button_unchecked_rounded,
              color: cs.outlineVariant,
            ),
      onTap: onTap,
    );
  }
}

class _SectionHeader extends StatelessWidget {
  final String label;
  const _SectionHeader(this.label);

  @override
  Widget build(BuildContext context) {
    if (label.isEmpty) return const SizedBox(height: 8);
    final cs = Theme.of(context).colorScheme;
    final tt = Theme.of(context).textTheme;
    return Padding(
      padding: const EdgeInsets.fromLTRB(16, 20, 16, 4),
      child: Text(
        label.toUpperCase(),
        style: tt.labelSmall?.copyWith(
          color: cs.onSurfaceVariant,
          letterSpacing: 0.8,
          fontWeight: FontWeight.w600,
        ),
      ),
    );
  }
}
