import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';

import 'package:guad/features/gtd/presentation/shared/gtd_sheet_widgets.dart';
import 'package:guad/features/gtd/presentation/waiting_for/cubit/waiting_for_cubit.dart';

class WaitingForSheet extends StatefulWidget {
  const WaitingForSheet({super.key});

  @override
  State<WaitingForSheet> createState() => _WaitingForSheetState();
}

class _WaitingForSheetState extends State<WaitingForSheet> {
  final _title = TextEditingController();
  final _delegatedTo = TextEditingController();
  final _notes = TextEditingController();
  final _formKey = GlobalKey<FormState>();

  @override
  void dispose() {
    _title.dispose();
    _delegatedTo.dispose();
    _notes.dispose();
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
              controller: _title,
              hint: 'What are you waiting for?',
              validator: (value) =>
                  value?.trim().isEmpty == true ? 'Title is required' : null,
            ),
            GtdSheetField(controller: _delegatedTo, hint: 'Delegated to'),
            GtdSheetField(controller: _notes, hint: 'Notes'),
            const SizedBox(height: 8),
            Align(
              alignment: Alignment.centerRight,
              child: FilledButton(
                onPressed: _submit,
                child: const Text('Create item'),
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

    await context.read<WaitingForCubit>().create(
      title: _title.text.trim(),
      delegatedTo: emptyToNull(_delegatedTo.text),
      notes: emptyToNull(_notes.text),
    );

    if (mounted) navigator.pop(true);
  }
}
