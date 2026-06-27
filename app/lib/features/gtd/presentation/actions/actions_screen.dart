import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:go_router/go_router.dart';

import 'package:guad/features/gtd/domain/entities/gtd_action.dart';
import 'package:guad/features/gtd/presentation/actions/cubit/actions_cubit.dart';
import 'package:guad/features/notifications/presentation/notifications_routes.dart';
import 'package:guad/gen/l10n/app_localizations.dart';

class ActionsScreen extends StatelessWidget {
  const ActionsScreen({super.key});

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context);
    final cs = Theme.of(context).colorScheme;

    return BlocConsumer<ActionsCubit, ActionsState>(
      listenWhen: (previous, current) =>
          previous.errorMessage != current.errorMessage &&
          current.errorMessage != null,
      listener: (context, state) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Text(state.errorMessage!),
            behavior: SnackBarBehavior.floating,
            backgroundColor: cs.errorContainer,
            showCloseIcon: true,
          ),
        );
      },
      builder: (context, state) {
        return Scaffold(
          appBar: AppBar(
            title: Text(l10n.navActions),
            actions: [
              IconButton(
                icon: const Icon(Icons.notifications_outlined),
                tooltip: l10n.notificationsTitle,
                onPressed: () =>
                    context.push(NotificationsRoutes.notifications),
              ),
              const SizedBox(width: 4),
            ],
          ),
          body: _ActionsBody(state: state),
          floatingActionButton: FloatingActionButton.small(
            onPressed: state.isMutating
                ? null
                : () => _showCreateSheet(context),
            tooltip: l10n.actionsAdd,
            child: const Icon(Icons.add_rounded),
          ),
        );
      },
    );
  }

  Future<void> _showCreateSheet(BuildContext context) {
    return showModalBottomSheet<void>(
      context: context,
      isScrollControlled: true,
      builder: (_) => BlocProvider.value(
        value: context.read<ActionsCubit>(),
        child: const _CreateActionSheet(),
      ),
    );
  }
}

class _ActionsBody extends StatelessWidget {
  const _ActionsBody({required this.state});

  final ActionsState state;

  @override
  Widget build(BuildContext context) {
    if (state.isLoading) {
      return const Center(child: CircularProgressIndicator());
    }

    if (state.isEmpty) {
      return RefreshIndicator(
        onRefresh: () => context.read<ActionsCubit>().load(),
        child: ListView(
          physics: const AlwaysScrollableScrollPhysics(),
          children: const [SizedBox(height: 160), _ActionsEmptyState()],
        ),
      );
    }

    return RefreshIndicator(
      onRefresh: () => context.read<ActionsCubit>().load(),
      child: ListView.separated(
        padding: const EdgeInsets.fromLTRB(20, 4, 20, 96),
        itemCount: state.actions.length,
        separatorBuilder: (context, _) => Divider(
          height: 1,
          thickness: 1,
          color: Theme.of(context).colorScheme.outlineVariant,
        ),
        itemBuilder: (context, index) {
          final action = state.actions[index];
          return Dismissible(
            key: ValueKey(action.id),
            direction: DismissDirection.endToStart,
            background: const _DeleteBackground(),
            onDismissed: (_) => context.read<ActionsCubit>().delete(action.id),
            child: _ActionTile(action: action),
          );
        },
      ),
    );
  }
}

class _ActionTile extends StatelessWidget {
  const _ActionTile({required this.action});

  final GtdAction action;

  @override
  Widget build(BuildContext context) {
    final cs = Theme.of(context).colorScheme;
    final tt = Theme.of(context).textTheme;
    final l10n = AppLocalizations.of(context);
    final notes = action.notes?.trim();
    final projectName = action.projectName?.trim();
    final duration = action.estimatedDuration;

    return ListTile(
      minVerticalPadding: 12,
      contentPadding: EdgeInsets.zero,
      leading: IconButton(
        icon: const Icon(Icons.radio_button_unchecked_rounded),
        tooltip: l10n.actionsComplete,
        color: cs.onSurfaceVariant,
        onPressed: () => context.read<ActionsCubit>().complete(action.id),
      ),
      title: Text(
        action.description,
        style: tt.bodyLarge?.copyWith(
          color: cs.onSurface,
          fontWeight: FontWeight.w500,
        ),
      ),
      subtitle: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          if (notes?.isNotEmpty == true)
            Padding(
              padding: const EdgeInsets.only(top: 3),
              child: Text(
                notes!,
                maxLines: 2,
                overflow: TextOverflow.ellipsis,
                style: tt.bodySmall?.copyWith(color: cs.onSurfaceVariant),
              ),
            ),
          if (projectName?.isNotEmpty == true || duration != null)
            Padding(
              padding: const EdgeInsets.only(top: 6),
              child: Wrap(
                spacing: 6,
                runSpacing: 6,
                children: [
                  if (projectName?.isNotEmpty == true)
                    _ActionMetaChip(
                      icon: Icons.folder_outlined,
                      label: projectName!,
                    ),
                  if (duration != null)
                    _ActionMetaChip(
                      icon: Icons.schedule_rounded,
                      label: l10n.actionsMinutes(duration),
                    ),
                ],
              ),
            ),
        ],
      ),
    );
  }
}

class _ActionMetaChip extends StatelessWidget {
  const _ActionMetaChip({required this.icon, required this.label});

  final IconData icon;
  final String label;

  @override
  Widget build(BuildContext context) {
    final cs = Theme.of(context).colorScheme;
    final tt = Theme.of(context).textTheme;

    return DecoratedBox(
      decoration: BoxDecoration(
        color: cs.surfaceContainerHighest,
        borderRadius: BorderRadius.circular(8),
      ),
      child: Padding(
        padding: const EdgeInsets.symmetric(horizontal: 7, vertical: 4),
        child: Row(
          mainAxisSize: MainAxisSize.min,
          children: [
            Icon(icon, size: 14, color: cs.onSurfaceVariant),
            const SizedBox(width: 4),
            Text(
              label,
              style: tt.labelSmall?.copyWith(color: cs.onSurfaceVariant),
            ),
          ],
        ),
      ),
    );
  }
}

class _ActionsEmptyState extends StatelessWidget {
  const _ActionsEmptyState();

  @override
  Widget build(BuildContext context) {
    final cs = Theme.of(context).colorScheme;
    final tt = Theme.of(context).textTheme;
    final l10n = AppLocalizations.of(context);

    return Center(
      child: Padding(
        padding: const EdgeInsets.symmetric(horizontal: 32),
        child: Column(
          children: [
            Icon(
              Icons.check_circle_outline_rounded,
              size: 40,
              color: cs.outlineVariant,
            ),
            const SizedBox(height: 12),
            Text(
              l10n.actionsEmptyTitle,
              style: tt.bodyLarge?.copyWith(color: cs.onSurfaceVariant),
            ),
            const SizedBox(height: 4),
            Text(
              l10n.actionsEmptySubtitle,
              style: tt.bodySmall?.copyWith(color: cs.outline),
              textAlign: TextAlign.center,
            ),
          ],
        ),
      ),
    );
  }
}

class _DeleteBackground extends StatelessWidget {
  const _DeleteBackground();

  @override
  Widget build(BuildContext context) {
    final cs = Theme.of(context).colorScheme;

    return DecoratedBox(
      decoration: BoxDecoration(color: cs.surfaceContainerHigh),
      child: Align(
        alignment: Alignment.centerRight,
        child: Padding(
          padding: const EdgeInsets.only(right: 20),
          child: Icon(Icons.delete_outline_rounded, color: cs.error),
        ),
      ),
    );
  }
}

class _CreateActionSheet extends StatefulWidget {
  const _CreateActionSheet();

  @override
  State<_CreateActionSheet> createState() => _CreateActionSheetState();
}

class _CreateActionSheetState extends State<_CreateActionSheet> {
  final _descriptionController = TextEditingController();
  final _notesController = TextEditingController();
  final _descriptionFocusNode = FocusNode();
  final _formKey = GlobalKey<FormState>();

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addPostFrameCallback((_) {
      if (!mounted) return;
      _descriptionFocusNode.requestFocus();
    });
  }

  @override
  void dispose() {
    _descriptionController.dispose();
    _notesController.dispose();
    _descriptionFocusNode.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final bottomInset = MediaQuery.viewInsetsOf(context).bottom;
    final isMutating = context.select(
      (ActionsCubit cubit) => cubit.state.isMutating,
    );
    final l10n = AppLocalizations.of(context);
    final cs = Theme.of(context).colorScheme;
    final tt = Theme.of(context).textTheme;

    return SafeArea(
      child: Padding(
        padding: EdgeInsets.fromLTRB(20, 12, 20, 10 + bottomInset),
        child: Form(
          key: _formKey,
          child: Column(
            mainAxisSize: MainAxisSize.min,
            crossAxisAlignment: CrossAxisAlignment.stretch,
            children: [
              TextFormField(
                controller: _descriptionController,
                focusNode: _descriptionFocusNode,
                textInputAction: TextInputAction.next,
                style: tt.titleMedium?.copyWith(
                  color: cs.onSurface,
                  fontWeight: FontWeight.w500,
                ),
                decoration: _bareInputDecoration(
                  context,
                  hintText: l10n.actionsPrompt,
                ),
                validator: (value) => value?.trim().isEmpty == true
                    ? l10n.actionsDescriptionRequired
                    : null,
              ),
              TextField(
                controller: _notesController,
                minLines: 1,
                maxLines: 4,
                style: tt.bodyMedium?.copyWith(color: cs.onSurfaceVariant),
                decoration: _bareInputDecoration(
                  context,
                  hintText: l10n.actionsNotes,
                ),
              ),
              const SizedBox(height: 8),
              Row(
                mainAxisAlignment: MainAxisAlignment.end,
                children: [
                  FilledButton(
                    onPressed: isMutating ? null : _submit,
                    style: FilledButton.styleFrom(
                      minimumSize: const Size(0, 40),
                      padding: const EdgeInsets.symmetric(horizontal: 14),
                    ),
                    child: SizedBox(
                      height: 20,
                      child: Center(
                        child: isMutating
                            ? const SizedBox.square(
                                dimension: 18,
                                child: CircularProgressIndicator(
                                  strokeWidth: 2,
                                ),
                              )
                            : Text(l10n.actionsAddAction),
                      ),
                    ),
                  ),
                ],
              ),
            ],
          ),
        ),
      ),
    );
  }

  Future<void> _submit() async {
    if (_formKey.currentState?.validate() != true) return;
    await context.read<ActionsCubit>().create(
      description: _descriptionController.text,
      notes: _notesController.text,
    );
    if (!mounted) return;
    Navigator.of(context).pop();
  }

  InputDecoration _bareInputDecoration(
    BuildContext context, {
    required String hintText,
  }) {
    final cs = Theme.of(context).colorScheme;
    return InputDecoration(
      hintText: hintText,
      hintStyle: TextStyle(color: cs.onSurfaceVariant.withValues(alpha: 0.7)),
      filled: false,
      isDense: true,
      contentPadding: const EdgeInsets.symmetric(vertical: 7),
      border: InputBorder.none,
      enabledBorder: InputBorder.none,
      focusedBorder: InputBorder.none,
      errorBorder: InputBorder.none,
      focusedErrorBorder: InputBorder.none,
    );
  }
}
