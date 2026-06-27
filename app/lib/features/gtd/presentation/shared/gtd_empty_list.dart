import 'package:flutter/material.dart';

class GtdEmptyList extends StatelessWidget {
  const GtdEmptyList({required this.title, super.key});

  final String title;

  @override
  Widget build(BuildContext context) {
    return Center(
      child: Padding(
        padding: const EdgeInsets.symmetric(horizontal: 32, vertical: 120),
        child: Text(
          title,
          textAlign: TextAlign.center,
          style: Theme.of(context).textTheme.bodyLarge?.copyWith(
            color: Theme.of(context).colorScheme.onSurfaceVariant,
          ),
        ),
      ),
    );
  }
}

class GtdEmptyListView extends StatelessWidget {
  const GtdEmptyListView({required this.title, super.key});

  final String title;

  @override
  Widget build(BuildContext context) {
    return ListView(
      physics: const AlwaysScrollableScrollPhysics(),
      children: [GtdEmptyList(title: title)],
    );
  }
}
