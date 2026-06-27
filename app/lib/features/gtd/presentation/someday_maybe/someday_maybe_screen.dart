import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';

import 'package:guad/features/gtd/presentation/someday_maybe/cubit/someday_maybe_cubit.dart';
import 'package:guad/features/gtd/presentation/shared/gtd_empty_list.dart';

class SomedayMaybeScreen extends StatelessWidget {
  const SomedayMaybeScreen({super.key});

  @override
  Widget build(BuildContext context) {
    final cs = Theme.of(context).colorScheme;

    return BlocConsumer<SomedayMaybeCubit, SomedayMaybeState>(
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
          appBar: AppBar(title: const Text('Someday maybe')),
          body: _SomedayMaybeBody(state: state),
        );
      },
    );
  }
}

class _SomedayMaybeBody extends StatelessWidget {
  const _SomedayMaybeBody({required this.state});

  final SomedayMaybeState state;

  @override
  Widget build(BuildContext context) {
    if (state.isLoading) {
      return const Center(child: CircularProgressIndicator());
    }

    final data = state.data;
    if (data == null || state.isEmpty) {
      return RefreshIndicator(
        onRefresh: () => context.read<SomedayMaybeCubit>().load(),
        child: const GtdEmptyListView(title: 'No someday items'),
      );
    }

    final rows = [
      ...data.actions.map(
        (action) => _SomedayRow(
          icon: Icons.check_circle_outline_rounded,
          title: action.description,
          subtitle: action.notes,
        ),
      ),
      ...data.projects.map(
        (project) => _SomedayRow(
          icon: Icons.folder_outlined,
          title: project.name,
          subtitle: project.desiredOutcome ?? project.description,
        ),
      ),
    ];

    return RefreshIndicator(
      onRefresh: () => context.read<SomedayMaybeCubit>().load(),
      child: ListView.separated(
        padding: const EdgeInsets.fromLTRB(20, 4, 20, 96),
        itemCount: rows.length,
        separatorBuilder: (context, _) => Divider(
          height: 1,
          color: Theme.of(context).colorScheme.outlineVariant,
        ),
        itemBuilder: (context, index) => rows[index],
      ),
    );
  }
}

class _SomedayRow extends StatelessWidget {
  const _SomedayRow({required this.icon, required this.title, this.subtitle});

  final IconData icon;
  final String title;
  final String? subtitle;

  @override
  Widget build(BuildContext context) {
    return ListTile(
      contentPadding: EdgeInsets.zero,
      leading: Icon(icon),
      title: Text(title),
      subtitle: subtitle == null ? null : Text(subtitle!),
    );
  }
}
