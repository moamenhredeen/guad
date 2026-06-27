import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';

import 'package:guad/features/gtd/presentation/areas/cubit/areas_cubit.dart';
import 'package:guad/features/gtd/presentation/shared/gtd_sheet_widgets.dart';

class AreaSheet extends StatefulWidget {
  const AreaSheet({super.key});

  @override
  State<AreaSheet> createState() => _AreaSheetState();
}

class _AreaSheetState extends State<AreaSheet> {
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
            Text('New area', style: Theme.of(context).textTheme.titleMedium),
            GtdSheetField(
              controller: _name,
              hint: 'Area name',
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

    await context.read<AreasCubit>().create(
      name: _name.text.trim(),
      description: emptyToNull(_description.text),
    );

    if (mounted) navigator.pop();
  }
}
