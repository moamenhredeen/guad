import 'package:guad/features/gtd/domain/entities/inbox_item.dart';

class InboxItemModel {
  const InboxItemModel({
    required this.id,
    required this.title,
    this.description,
    required this.status,
    this.createdAt,
  });

  final int id;
  final String title;
  final String? description;
  final InboxItemStatus status;
  final DateTime? createdAt;

  factory InboxItemModel.fromJson(Map<String, dynamic> json) {
    return InboxItemModel(
      id: json['id'] as int,
      title: json['title'] as String,
      description: json['description'] as String?,
      status: _statusFromApi(json['status'] as String?),
      createdAt: _dateTimeFromApi(json['createdAt']),
    );
  }

  InboxItem toDomain() {
    return InboxItem(
      id: id,
      title: title,
      description: description,
      status: status,
      createdAt: createdAt,
    );
  }

  static InboxItemStatus _statusFromApi(String? value) {
    return switch (value) {
      'PROCESSING' => InboxItemStatus.processing,
      'PROCESSED' => InboxItemStatus.processed,
      _ => InboxItemStatus.unprocessed,
    };
  }

  static DateTime? _dateTimeFromApi(Object? value) {
    if (value is! String || value.isEmpty) return null;
    return DateTime.tryParse(value);
  }
}
