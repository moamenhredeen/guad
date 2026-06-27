import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:go_router/go_router.dart';

import 'package:guad/features/gtd/domain/entities/inbox_item.dart';
import 'package:guad/features/gtd/presentation/inbox/cubit/inbox_cubit.dart';
import 'package:guad/features/notifications/presentation/notifications_routes.dart';
import 'package:guad/gen/l10n/app_localizations.dart';

class InboxScreen extends StatelessWidget {
  const InboxScreen({super.key});

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context);
    final cs = Theme.of(context).colorScheme;

    return BlocConsumer<InboxCubit, InboxState>(
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
            title: Text(l10n.navInbox),
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
          body: _InboxBody(state: state),
          floatingActionButton: FloatingActionButton.small(
            onPressed: state.isMutating
                ? null
                : () => _showCaptureSheet(context),
            tooltip: l10n.inboxCapture,
            child: const Icon(Icons.add_rounded),
          ),
        );
      },
    );
  }

  Future<void> _showCaptureSheet(BuildContext context) {
    return showModalBottomSheet<void>(
      context: context,
      isScrollControlled: true,
      builder: (_) => BlocProvider.value(
        value: context.read<InboxCubit>(),
        child: const _CaptureInboxSheet(),
      ),
    );
  }
}

class _InboxBody extends StatelessWidget {
  const _InboxBody({required this.state});

  final InboxState state;

  @override
  Widget build(BuildContext context) {
    if (state.isLoading) {
      return const Center(child: CircularProgressIndicator());
    }

    if (state.isEmpty) {
      return RefreshIndicator(
        onRefresh: () => context.read<InboxCubit>().load(),
        child: ListView(
          physics: const AlwaysScrollableScrollPhysics(),
          children: const [SizedBox(height: 160), _InboxEmptyState()],
        ),
      );
    }

    return RefreshIndicator(
      onRefresh: () => context.read<InboxCubit>().load(),
      child: ListView.separated(
        padding: const EdgeInsets.fromLTRB(20, 4, 20, 96),
        itemCount: state.items.length,
        separatorBuilder: (context, _) => Divider(
          height: 1,
          thickness: 1,
          color: Theme.of(context).colorScheme.outlineVariant,
        ),
        itemBuilder: (context, index) {
          final item = state.items[index];
          return Dismissible(
            key: ValueKey(item.id),
            direction: DismissDirection.endToStart,
            background: const _DeleteBackground(),
            onDismissed: (_) => context.read<InboxCubit>().delete(item.id),
            child: _InboxItemTile(item: item),
          );
        },
      ),
    );
  }
}

class _InboxItemTile extends StatelessWidget {
  const _InboxItemTile({required this.item});

  final InboxItem item;

  @override
  Widget build(BuildContext context) {
    final cs = Theme.of(context).colorScheme;
    final tt = Theme.of(context).textTheme;
    final l10n = AppLocalizations.of(context);
    final description = item.description?.trim();

    return ListTile(
      minVerticalPadding: 12,
      contentPadding: EdgeInsets.zero,
      title: Text(
        item.title,
        style: tt.bodyLarge?.copyWith(
          color: cs.onSurface,
          fontWeight: FontWeight.w500,
        ),
      ),
      subtitle: description?.isNotEmpty == true
          ? Padding(
              padding: const EdgeInsets.only(top: 3),
              child: Text(
                description!,
                maxLines: 2,
                overflow: TextOverflow.ellipsis,
                style: tt.bodySmall?.copyWith(color: cs.onSurfaceVariant),
              ),
            )
          : null,
      trailing: PopupMenuButton<InboxProcessAction>(
        tooltip: l10n.inboxProcessTooltip,
        icon: Icon(Icons.more_horiz_rounded, color: cs.onSurfaceVariant),
        padding: EdgeInsets.zero,
        onSelected: (action) =>
            context.read<InboxCubit>().process(id: item.id, action: action),
        itemBuilder: (_) => [
          PopupMenuItem(
            value: InboxProcessAction.nextAction,
            child: Text(l10n.inboxProcessNextAction),
          ),
          PopupMenuItem(
            value: InboxProcessAction.project,
            child: Text(l10n.inboxProcessProject),
          ),
          PopupMenuItem(
            value: InboxProcessAction.waitingFor,
            child: Text(l10n.inboxProcessWaitingFor),
          ),
          PopupMenuItem(
            value: InboxProcessAction.somedayMaybe,
            child: Text(l10n.inboxProcessSomedayMaybe),
          ),
          PopupMenuItem(
            value: InboxProcessAction.reference,
            child: Text(l10n.inboxProcessReference),
          ),
          PopupMenuItem(
            value: InboxProcessAction.trash,
            child: Text(l10n.inboxProcessTrash),
          ),
        ],
      ),
    );
  }
}

class _InboxEmptyState extends StatelessWidget {
  const _InboxEmptyState();

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
            Icon(Icons.inbox_outlined, size: 40, color: cs.outlineVariant),
            const SizedBox(height: 12),
            Text(
              l10n.inboxEmptyTitle,
              style: tt.bodyLarge?.copyWith(color: cs.onSurfaceVariant),
            ),
            const SizedBox(height: 4),
            Text(
              l10n.inboxEmptySubtitle,
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

class _CaptureInboxSheet extends StatefulWidget {
  const _CaptureInboxSheet();

  @override
  State<_CaptureInboxSheet> createState() => _CaptureInboxSheetState();
}

class _CaptureInboxSheetState extends State<_CaptureInboxSheet> {
  final _titleController = TextEditingController();
  final _descriptionController = TextEditingController();
  final _formKey = GlobalKey<FormState>();

  @override
  void dispose() {
    _titleController.dispose();
    _descriptionController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final bottomInset = MediaQuery.viewInsetsOf(context).bottom;
    final isMutating = context.select(
      (InboxCubit cubit) => cubit.state.isMutating,
    );
    final l10n = AppLocalizations.of(context);

    return SafeArea(
      child: Padding(
        padding: EdgeInsets.fromLTRB(20, 20, 20, 16 + bottomInset),
        child: Form(
          key: _formKey,
          child: Column(
            mainAxisSize: MainAxisSize.min,
            crossAxisAlignment: CrossAxisAlignment.stretch,
            children: [
              Text(
                l10n.inboxCapture,
                style: Theme.of(
                  context,
                ).textTheme.titleLarge?.copyWith(fontWeight: FontWeight.w600),
              ),
              const SizedBox(height: 16),
              TextFormField(
                controller: _titleController,
                autofocus: true,
                textInputAction: TextInputAction.next,
                decoration: InputDecoration(
                  labelText: l10n.inboxCapturePrompt,
                  prefixIcon: const Icon(Icons.inbox_outlined),
                ),
                validator: (value) => value?.trim().isEmpty == true
                    ? l10n.inboxTitleRequired
                    : null,
              ),
              const SizedBox(height: 12),
              TextField(
                controller: _descriptionController,
                minLines: 2,
                maxLines: 4,
                decoration: InputDecoration(
                  labelText: l10n.inboxNotes,
                  prefixIcon: const Icon(Icons.notes_outlined),
                ),
              ),
              const SizedBox(height: 20),
              FilledButton.icon(
                onPressed: isMutating ? null : _submit,
                icon: isMutating
                    ? const SizedBox.square(
                        dimension: 18,
                        child: CircularProgressIndicator(strokeWidth: 2),
                      )
                    : const Icon(Icons.add_rounded),
                label: Text(l10n.inboxAddToInbox),
              ),
            ],
          ),
        ),
      ),
    );
  }

  Future<void> _submit() async {
    if (_formKey.currentState?.validate() != true) return;
    await context.read<InboxCubit>().create(
      title: _titleController.text,
      description: _descriptionController.text,
    );
    if (!mounted) return;
    Navigator.of(context).pop();
  }
}
