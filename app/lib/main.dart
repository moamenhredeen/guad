import 'package:flutter/widgets.dart';
import 'package:shared_preferences/shared_preferences.dart';

import 'package:guad/app.dart';
import 'package:guad/config/env_config.dart';
import 'package:guad/features/auth/data/services/local_biometric_authenticator.dart';
import 'package:guad/infrastructure/services/connectivity_service.dart';
import 'package:guad/infrastructure/services/key_value_storage_service.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();

  const env = String.fromEnvironment('ENV', defaultValue: 'dev');
  final config = env == 'prod' ? EnvConfig.prod : EnvConfig.dev;

  final prefs = await SharedPreferences.getInstance();
  final keyValueStorage = KeyValueStorageService(prefs);

  runApp(
    App(
      config: config,
      keyValueStorage: keyValueStorage,
      connectivityService: ConnectivityService(),
      biometricService: LocalBiometricAuthenticator(),
    ),
  );
}
