import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';

import 'package:guad/features/gtd/presentation/contexts/cubit/contexts_cubit.dart';
import 'package:guad/features/gtd/presentation/contexts/widgets/context_sheet.dart';
import 'package:guad/features/gtd/presentation/shared/gtd_list_widgets.dart';

class ContextsScreen extends StatelessWidget {
  const ContextsScreen({super.key});

  @override
  Widget build(BuildContext context) {
    final cs = Theme.of(context).colorScheme;

    return BlocConsumer<ContextsCubit, ContextsState>(
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
          appBar: AppBar(title: const Text('Contexts')),
          body: _ContextsBody(state: state),
          floatingActionButton: FloatingActionButton.small(
            tooltip: 'Add',
            onPressed: state.isMutating
                ? null
                : () => _showContextSheet(context),
            child: const Icon(Icons.add_rounded),
          ),
        );
      },
    );
  }

  Future<void> _showContextSheet(BuildContext context) {
    return showModalBottomSheet<void>(
      context: context,
      isScrollControlled: true,
      builder: (_) => BlocProvider.value(
        value: context.read<ContextsCubit>(),
        child: const ContextSheet(),
      ),
    );
  }
}

class _ContextsBody extends StatelessWidget {
  const _ContextsBody({required this.state});

  final ContextsState state;

  @override
  Widget build(BuildContext context) {
    if (state.isLoading) {
      return const Center(child: CircularProgressIndicator());
    }

    return RefreshIndicator(
      onRefresh: () => context.read<ContextsCubit>().load(),
      child: GtdSimpleList(
        rows: state.contexts
            .map(
              (gtdContext) => GtdSimpleRow(
                id: gtdContext.id,
                title: gtdContext.name,
                subtitle: gtdContext.description,
              ),
            )
            .toList(),
        onDelete: (id) => context.read<ContextsCubit>().delete(id),
      ),
    );
  }
}
