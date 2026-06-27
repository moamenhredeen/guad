class AuthToken {
  const AuthToken({
    required this.accessToken,
    required this.refreshToken,
    this.idToken,
    required this.expiresAt,
    required this.refreshTokenExpiresAt,
  });

  final String accessToken;
  final String refreshToken;
  final String? idToken;
  final DateTime expiresAt;
  final DateTime refreshTokenExpiresAt;

  bool get isExpired => DateTime.now().isAfter(expiresAt);
  bool get isRefreshExpired => DateTime.now().isAfter(refreshTokenExpiresAt);

  Map<String, dynamic> toJson() => {
    'accessToken': accessToken,
    'refreshToken': refreshToken,
    'idToken': idToken,
    'expiresAt': expiresAt.toIso8601String(),
    'refreshTokenExpiresAt': refreshTokenExpiresAt.toIso8601String(),
  };

  factory AuthToken.fromJson(Map<String, dynamic> json) => AuthToken(
    accessToken: json['accessToken'] as String,
    refreshToken: json['refreshToken'] as String,
    idToken: json['idToken'] as String?,
    expiresAt: DateTime.parse(json['expiresAt'] as String),
    refreshTokenExpiresAt: DateTime.parse(
      json['refreshTokenExpiresAt'] as String,
    ),
  );
}
