import 'package:flutter/widgets.dart';
import 'package:flutter_bloc/flutter_bloc.dart';

import 'package:guad/infrastructure/services/key_value_storage_service.dart';

class LocaleCubit extends Cubit<Locale> {
  LocaleCubit({required KeyValueStorageService keyValueStorage})
    : _keyValueStorage = keyValueStorage,
      super(Locale(keyValueStorage.getString(_localeKey) ?? 'en'));

  static const _localeKey = 'locale';

  final KeyValueStorageService _keyValueStorage;

  Future<void> setLocale(Locale locale) async {
    await _keyValueStorage.setString(_localeKey, locale.languageCode);
    emit(locale);
  }
}
