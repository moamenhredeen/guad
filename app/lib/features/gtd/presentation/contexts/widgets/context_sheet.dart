import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';

import 'package:guad/features/gtd/presentation/contexts/cubit/contexts_cubit.dart';
import 'package:guad/features/gtd/presentation/shared/gtd_sheet_widgets.dart';

class ContextSheet extends StatefulWidget {
  const ContextSheet({super.key});

  @override
  State<ContextSheet> createState() => _ContextSheetState();
}

class _ContextSheetState extends State<ContextSheet> {
  final _name = TextEditingController();
  final _description = TextEditingController();
  final _formKey = GlobalKey<FormState>();

  @override
  void dispose() {
    _name.dispose();
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
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            Text('New context', style: Theme.of(context).textTheme.titleMedium),
            GtdSheetField(
              controller: _name,
              hint: 'Context name',
              validator: (value) =>
                  value?.trim().isEmpty == true ? 'Name is required' : null,
            ),
            GtdSheetField(controller: _description, hint: 'Description'),
            const SizedBox(height: 8),
            Align(
              alignment: Alignment.centerRight,
              child: FilledButton(
                onPressed: _submit,
                child: const Text('Create'),
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

    await context.read<ContextsCubit>().create(
      name: _name.text.trim(),
      description: emptyToNull(_description.text),
    );

    if (mounted) navigator.pop();
  }
}
