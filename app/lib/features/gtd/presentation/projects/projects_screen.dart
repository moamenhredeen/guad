import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';

import 'package:guad/features/gtd/domain/entities/gtd_project.dart';
import 'package:guad/features/gtd/presentation/projects/cubit/projects_cubit.dart';
import 'package:guad/features/gtd/presentation/projects/widgets/project_sheet.dart';
import 'package:guad/features/gtd/presentation/shared/gtd_empty_list.dart';
import 'package:guad/features/gtd/presentation/shared/gtd_list_widgets.dart';

class ProjectsScreen extends StatelessWidget {
  const ProjectsScreen({super.key});

  @override
  Widget build(BuildContext context) {
    final cs = Theme.of(context).colorScheme;

    return BlocConsumer<ProjectsCubit, ProjectsState>(
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
          appBar: AppBar(title: const Text('Projects')),
          body: _ProjectsBody(state: state),
          floatingActionButton: FloatingActionButton.small(
            tooltip: 'Add',
            onPressed: state.isMutating
                ? null
                : () => _showProjectSheet(context),
            child: const Icon(Icons.add_rounded),
          ),
        );
      },
    );
  }

  Future<void> _showProjectSheet(BuildContext context) {
    return showModalBottomSheet<void>(
      context: context,
      isScrollControlled: true,
      builder: (_) => BlocProvider.value(
        value: context.read<ProjectsCubit>(),
        child: const ProjectSheet(),
      ),
    );
  }
}

class _ProjectsBody extends StatelessWidget {
  const _ProjectsBody({required this.state});

  final ProjectsState state;

  @override
  Widget build(BuildContext context) {
    if (state.isLoading) {
      return const Center(child: CircularProgressIndicator());
    }

    if (state.isEmpty) {
      return RefreshIndicator(
        onRefresh: () => context.read<ProjectsCubit>().load(),
        child: const GtdEmptyListView(title: 'No active projects'),
      );
    }

    return RefreshIndicator(
      onRefresh: () => context.read<ProjectsCubit>().load(),
      child: ListView.separated(
        padding: const EdgeInsets.fromLTRB(20, 4, 20, 96),
        itemCount: state.projects.length,
        separatorBuilder: (context, _) => Divider(
          height: 1,
          color: Theme.of(context).colorScheme.outlineVariant,
        ),
        itemBuilder: (context, index) {
          final project = state.projects[index];
          return Dismissible(
            key: ValueKey(project.id),
            direction: DismissDirection.endToStart,
            background: const GtdDeleteBackground(),
            onDismissed: (_) =>
                context.read<ProjectsCubit>().delete(project.id),
            child: _ProjectTile(project: project),
          );
        },
      ),
    );
  }
}

class _ProjectTile extends StatelessWidget {
  const _ProjectTile({required this.project});

  final GtdProject project;

  @override
  Widget build(BuildContext context) {
    return ListTile(
      contentPadding: EdgeInsets.zero,
      title: Text(project.name),
      subtitle: GtdOptionalSubtitle(
        primary: project.desiredOutcome,
        secondary: project.areaName,
      ),
      trailing: GtdCountPill(label: '${project.nextActionCount} next'),
    );
  }
}
