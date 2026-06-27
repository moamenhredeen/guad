import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';

import 'package:guad/features/gtd/presentation/areas/cubit/areas_cubit.dart';
import 'package:guad/features/gtd/presentation/areas/widgets/area_sheet.dart';
import 'package:guad/features/gtd/presentation/shared/gtd_list_widgets.dart';

class AreasScreen extends StatelessWidget {
  const AreasScreen({super.key});

  @override
  Widget build(BuildContext context) {
    final cs = Theme.of(context).colorScheme;

    return BlocConsumer<AreasCubit, AreasState>(
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
          appBar: AppBar(title: const Text('Areas')),
          body: _AreasBody(state: state),
          floatingActionButton: FloatingActionButton.small(
            tooltip: 'Add',
            onPressed: state.isMutating ? null : () => _showAreaSheet(context),
            child: const Icon(Icons.add_rounded),
          ),
        );
      },
    );
  }

  Future<void> _showAreaSheet(BuildContext context) {
    return showModalBottomSheet<void>(
      context: context,
      isScrollControlled: true,
      builder: (_) => BlocProvider.value(
        value: context.read<AreasCubit>(),
        child: const AreaSheet(),
      ),
    );
  }
}

class _AreasBody extends StatelessWidget {
  const _AreasBody({required this.state});

  final AreasState state;

  @override
  Widget build(BuildContext context) {
    if (state.isLoading) {
      return const Center(child: CircularProgressIndicator());
    }

    return RefreshIndicator(
      onRefresh: () => context.read<AreasCubit>().load(),
      child: GtdSimpleList(
        rows: state.areas
            .map(
              (area) => GtdSimpleRow(
                id: area.id,
                title: area.name,
                subtitle: area.description,
              ),
            )
            .toList(),
        onDelete: (id) => context.read<AreasCubit>().delete(id),
      ),
    );
  }
}
