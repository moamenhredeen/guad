import 'package:flutter/material.dart';

import 'package:guad/features/gtd/presentation/shared/gtd_empty_list.dart';

class GtdDeleteBackground extends StatelessWidget {
  const GtdDeleteBackground({super.key});

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

class GtdOptionalSubtitle extends StatelessWidget {
  const GtdOptionalSubtitle({this.primary, this.secondary, super.key});

  final String? primary;
  final String? secondary;

  @override
  Widget build(BuildContext context) {
    final parts = [
      if (primary?.trim().isNotEmpty == true) primary!.trim(),
      if (secondary?.trim().isNotEmpty == true) secondary!.trim(),
    ];
    if (parts.isEmpty) return const SizedBox.shrink();
    return Text(
      parts.join(' • '),
      maxLines: 2,
      overflow: TextOverflow.ellipsis,
    );
  }
}

class GtdCountPill extends StatelessWidget {
  const GtdCountPill({required this.label, super.key});

  final String label;

  @override
  Widget build(BuildContext context) {
    final cs = Theme.of(context).colorScheme;
    return DecoratedBox(
      decoration: BoxDecoration(
        color: cs.surfaceContainerHighest,
        borderRadius: BorderRadius.circular(8),
      ),
      child: Padding(
        padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
        child: Text(label),
      ),
    );
  }
}

class GtdSimpleList extends StatelessWidget {
  const GtdSimpleList({required this.rows, required this.onDelete, super.key});

  final List<GtdSimpleRow> rows;
  final Future<void> Function(int id) onDelete;

  @override
  Widget build(BuildContext context) {
    if (rows.isEmpty) {
      return const GtdEmptyListView(title: 'Nothing here yet');
    }

    return ListView.separated(
      padding: const EdgeInsets.fromLTRB(20, 4, 20, 96),
      itemCount: rows.length,
      separatorBuilder: (context, _) => Divider(
        height: 1,
        color: Theme.of(context).colorScheme.outlineVariant,
      ),
      itemBuilder: (context, index) {
        final row = rows[index];
        return Dismissible(
          key: ValueKey(row.id),
          direction: DismissDirection.endToStart,
          background: const GtdDeleteBackground(),
          onDismissed: (_) => onDelete(row.id),
          child: ListTile(
            contentPadding: EdgeInsets.zero,
            title: Text(row.title),
            subtitle: row.subtitle == null ? null : Text(row.subtitle!),
          ),
        );
      },
    );
  }
}

class GtdSimpleRow {
  const GtdSimpleRow({required this.id, required this.title, this.subtitle});

  final int id;
  final String title;
  final String? subtitle;
}
