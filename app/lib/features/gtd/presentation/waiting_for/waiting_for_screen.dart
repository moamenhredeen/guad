import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';

import 'package:guad/features/gtd/domain/entities/waiting_for_item.dart';
import 'package:guad/features/gtd/presentation/waiting_for/cubit/waiting_for_cubit.dart';
import 'package:guad/features/gtd/presentation/shared/gtd_empty_list.dart';
import 'package:guad/features/gtd/presentation/shared/gtd_list_widgets.dart';
import 'package:guad/features/gtd/presentation/waiting_for/widgets/waiting_for_sheet.dart';

class WaitingForScreen extends StatelessWidget {
  const WaitingForScreen({super.key});

  @override
  Widget build(BuildContext context) {
    final cs = Theme.of(context).colorScheme;

    return BlocConsumer<WaitingForCubit, WaitingForState>(
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
          appBar: AppBar(title: const Text('Waiting for')),
          body: _WaitingForBody(state: state),
          floatingActionButton: FloatingActionButton.small(
            tooltip: 'Add',
            onPressed: state.isMutating
                ? null
                : () => _showWaitingSheet(context),
            child: const Icon(Icons.add_rounded),
          ),
        );
      },
    );
  }

  Future<void> _showWaitingSheet(BuildContext context) {
    return showModalBottomSheet<void>(
      context: context,
      isScrollControlled: true,
      builder: (_) => BlocProvider.value(
        value: context.read<WaitingForCubit>(),
        child: const WaitingForSheet(),
      ),
    );
  }
}

class _WaitingForBody extends StatelessWidget {
  const _WaitingForBody({required this.state});

  final WaitingForState state;

  @override
  Widget build(BuildContext context) {
    if (state.isLoading) {
      return const Center(child: CircularProgressIndicator());
    }

    if (state.isEmpty) {
      return RefreshIndicator(
        onRefresh: () => context.read<WaitingForCubit>().load(),
        child: const GtdEmptyListView(title: 'Nothing delegated'),
      );
    }

    return RefreshIndicator(
      onRefresh: () => context.read<WaitingForCubit>().load(),
      child: ListView.separated(
        padding: const EdgeInsets.fromLTRB(20, 4, 20, 96),
        itemCount: state.items.length,
        separatorBuilder: (context, _) => Divider(
          height: 1,
          color: Theme.of(context).colorScheme.outlineVariant,
        ),
        itemBuilder: (context, index) {
          final item = state.items[index];
          return Dismissible(
            key: ValueKey(item.id),
            direction: DismissDirection.endToStart,
            background: const GtdDeleteBackground(),
            onDismissed: (_) => context.read<WaitingForCubit>().delete(item.id),
            child: _WaitingForTile(item: item),
          );
        },
      ),
    );
  }
}

class _WaitingForTile extends StatelessWidget {
  const _WaitingForTile({required this.item});

  final WaitingForItem item;

  @override
  Widget build(BuildContext context) {
    return ListTile(
      contentPadding: EdgeInsets.zero,
      leading: IconButton(
        icon: const Icon(Icons.radio_button_unchecked_rounded),
        tooltip: 'Resolve',
        onPressed: () => context.read<WaitingForCubit>().resolve(item.id),
      ),
      title: Text(item.title),
      subtitle: GtdOptionalSubtitle(
        primary: item.delegatedTo == null
            ? null
            : 'Delegated to ${item.delegatedTo}',
        secondary: item.projectName ?? item.notes,
      ),
    );
  }
}
