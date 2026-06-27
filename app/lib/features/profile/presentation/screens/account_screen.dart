import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';

import 'package:guad/gen/l10n/app_localizations.dart';
import 'package:guad/features/auth/presentation/bloc/auth_bloc.dart';

class AccountScreen extends StatelessWidget {
  const AccountScreen({super.key});

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context);
    final cs = Theme.of(context).colorScheme;
    final tt = Theme.of(context).textTheme;
    final user = context.select((AuthBloc bloc) => bloc.state.user);
    final bioAvailable = context.select(
      (AuthBloc bloc) => bloc.state.biometricAvailable,
    );
    final bioEnabled = context.select(
      (AuthBloc bloc) => bloc.state.biometricEnabled,
    );
    final bioLoading = context.select((AuthBloc bloc) => bloc.state.isLoading);

    return Scaffold(
      appBar: AppBar(title: Text(l10n.accountTitle)),
      body: ListView(
        padding: EdgeInsets.zero,
        children: [
          _SectionHeader(l10n.accountContact),

          _VerifiedField(
            icon: Icons.email_outlined,
            label: l10n.personalInfoEmail,
            value: user?.email ?? '—',
            onTap: () => _showVerificationSheet(
              context,
              field: l10n.accountEmail,
              description: l10n.accountEmailVerifyHint,
            ),
          ),

          _VerifiedField(
            icon: Icons.phone_outlined,
            label: l10n.personalInfoPhone,
            value: user?.phoneNumber ?? l10n.accountNotSet,
            onTap: () => _showVerificationSheet(
              context,
              field: l10n.accountPhone,
              description: l10n.accountPhoneVerifyHint,
            ),
          ),

          Divider(color: cs.outlineVariant),

          _SectionHeader(l10n.accountSecurity),

          ListTile(
            leading: Icon(Icons.lock_outline_rounded, color: cs.primary),
            title: Text(l10n.accountChangePassword),
            trailing: const Icon(Icons.chevron_right_rounded),
            onTap: () => _showVerificationSheet(
              context,
              field: l10n.accountChangePassword,
              description: l10n.accountPasswordVerifyHint,
            ),
          ),

          if (bioAvailable)
            SwitchListTile(
              secondary: Icon(Icons.fingerprint_rounded, color: cs.primary),
              title: Text(l10n.biometricEnable),
              subtitle: Text(l10n.biometricEnableHint),
              value: bioEnabled,
              onChanged: bioLoading
                  ? null
                  : (val) {
                      if (val) {
                        context.read<AuthBloc>().add(
                          const AuthBiometricEnabled(),
                        );
                      } else {
                        context.read<AuthBloc>().add(
                          const AuthBiometricDisabled(),
                        );
                      }
                    },
            ),

          Divider(color: cs.outlineVariant),

          Padding(
            padding: const EdgeInsets.fromLTRB(20, 16, 20, 16),
            child: Row(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Icon(
                  Icons.info_outline_rounded,
                  size: 16,
                  color: cs.onSurfaceVariant,
                ),
                const SizedBox(width: 8),
                Expanded(
                  child: Text(
                    l10n.accountSecurityNote,
                    style: tt.bodySmall?.copyWith(color: cs.onSurfaceVariant),
                  ),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }

  void _showVerificationSheet(
    BuildContext context, {
    required String field,
    required String description,
  }) {
    final cs = Theme.of(context).colorScheme;
    final tt = Theme.of(context).textTheme;

    showModalBottomSheet(
      context: context,
      builder: (_) => SafeArea(
        child: Padding(
          padding: const EdgeInsets.fromLTRB(24, 24, 24, 16),
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              Container(
                width: 48,
                height: 48,
                decoration: BoxDecoration(
                  color: cs.secondaryContainer,
                  shape: BoxShape.circle,
                ),
                child: Icon(
                  Icons.verified_user_outlined,
                  color: cs.onSecondaryContainer,
                ),
              ),
              const SizedBox(height: 16),
              Text(
                field,
                style: tt.titleLarge?.copyWith(fontWeight: FontWeight.w600),
              ),
              const SizedBox(height: 8),
              Text(
                description,
                style: tt.bodyMedium?.copyWith(color: cs.onSurfaceVariant),
                textAlign: TextAlign.center,
              ),
              const SizedBox(height: 24),
              FilledButton(
                onPressed: () => Navigator.pop(context),
                child: const Text('OK'),
              ),
            ],
          ),
        ),
      ),
    );
  }
}

class _SectionHeader extends StatelessWidget {
  final String label;
  const _SectionHeader(this.label);

  @override
  Widget build(BuildContext context) {
    final cs = Theme.of(context).colorScheme;
    final tt = Theme.of(context).textTheme;
    return Padding(
      padding: const EdgeInsets.fromLTRB(20, 20, 20, 4),
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

class _VerifiedField extends StatelessWidget {
  final IconData icon;
  final String label;
  final String value;
  final VoidCallback onTap;

  const _VerifiedField({
    required this.icon,
    required this.label,
    required this.value,
    required this.onTap,
  });

  @override
  Widget build(BuildContext context) {
    final cs = Theme.of(context).colorScheme;
    final tt = Theme.of(context).textTheme;

    return ListTile(
      leading: Icon(icon, color: cs.primary),
      title: Text(
        label,
        style: tt.bodySmall?.copyWith(color: cs.onSurfaceVariant),
      ),
      subtitle: Text(value, style: tt.bodyLarge?.copyWith(color: cs.onSurface)),
      trailing: Row(
        mainAxisSize: MainAxisSize.min,
        children: [
          Icon(Icons.shield_outlined, size: 14, color: cs.onSurfaceVariant),
          const SizedBox(width: 4),
          const Icon(Icons.chevron_right_rounded),
        ],
      ),
      onTap: onTap,
    );
  }
}
