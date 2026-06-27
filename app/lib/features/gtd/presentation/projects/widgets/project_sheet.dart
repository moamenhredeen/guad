import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';

import 'package:guad/features/gtd/presentation/projects/cubit/projects_cubit.dart';
import 'package:guad/features/gtd/presentation/shared/gtd_sheet_widgets.dart';

class ProjectSheet extends StatefulWidget {
  const ProjectSheet({super.key});

  @override
  State<ProjectSheet> createState() => _ProjectSheetState();
}

class _ProjectSheetState extends State<ProjectSheet> {
  final _name = TextEditingController();
  final _outcome = TextEditingController();
  final _description = TextEditingController();
  final _formKey = GlobalKey<FormState>();

  @override
  void dispose() {
    _name.dispose();
    _outcome.dispose();
    _description.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return GtdSheetFrame(
      child: Form(
        key: _formKey,
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            GtdSheetField(
              controller: _name,
              hint: 'Project name',
              validator: (value) =>
                  value?.trim().isEmpty == true ? 'Name is required' : null,
            ),
            GtdSheetField(controller: _outcome, hint: 'Desired outcome'),
            GtdSheetField(controller: _description, hint: 'Notes'),
            const SizedBox(height: 8),
            Align(
              alignment: Alignment.centerRight,
              child: FilledButton(
                onPressed: _submit,
                child: const Text('Create project'),
              ),
            ),
          ],
        ),
      ),
    );
  }

  Future<void> _submit() async {
    if (_formKey.currentState?.validate() != true) return;
    final navigator = Navigator.of(context);

    await context.read<ProjectsCubit>().create(
      name: _name.text.trim(),
      desiredOutcome: emptyToNull(_outcome.text),
      description: emptyToNull(_description.text),
    );

    if (mounted) navigator.pop(true);
  }
}
