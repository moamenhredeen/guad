import 'package:guad/features/gtd/domain/entities/capture.dart';

class CaptureModel {
  const CaptureModel({
    required this.id,
    required this.title,
    this.description,
    required this.status,
    this.createdAt,
  });

  final int id;
  final String title;
  final String? description;
  final CaptureStatus status;
  final DateTime? createdAt;

  factory CaptureModel.fromJson(Map<String, dynamic> json) {
    return CaptureModel(
      id: json['id'] as int,
      title: json['title'] as String,
      description: json['description'] as String?,
      status: _statusFromApi(json['status'] as String?),
      createdAt: _dateTimeFromApi(json['createdAt']),
    );
  }

  Capture toDomain() {
    return Capture(
      id: id,
      title: title,
      description: description,
      status: status,
      createdAt: createdAt,
    );
  }

  static CaptureStatus _statusFromApi(String? value) {
    return switch (value) {
      'PROCESSING' => CaptureStatus.processing,
      'PROCESSED' => CaptureStatus.processed,
      _ => CaptureStatus.unprocessed,
    };
  }

  static DateTime? _dateTimeFromApi(Object? value) {
    if (value is! String || value.isEmpty) return null;
    return DateTime.tryParse(value);
  }
}
