int intFromJson(Object? value, {int fallback = 0}) {
  return switch (value) {
    int value => value,
    num value => value.toInt(),
    String value => int.tryParse(value) ?? fallback,
    _ => fallback,
  };
}

int? nullableIntFromJson(Object? value) {
  return switch (value) {
    int value => value,
    num value => value.toInt(),
    String value => int.tryParse(value),
    _ => null,
  };
}

DateTime? dateTimeFromJson(Object? value) {
  if (value is! String || value.isEmpty) return null;
  return DateTime.tryParse(value);
}
