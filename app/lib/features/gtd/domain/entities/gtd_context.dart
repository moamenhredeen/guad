class GtdContext {
  const GtdContext({
    required this.id,
    required this.name,
    this.description,
    this.color,
    this.iconKey,
  });

  final int id;
  final String name;
  final String? description;
  final String? color;
  final String? iconKey;
}
