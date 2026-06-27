import 'dart:async';

import 'package:flutter/foundation.dart';
import 'package:flutter/widgets.dart';
import 'package:flutter_localizations/flutter_localizations.dart';
import 'package:intl/intl.dart' as intl;

import 'app_localizations_ar.dart';
import 'app_localizations_en.dart';

// ignore_for_file: type=lint

/// Callers can lookup localized strings with an instance of AppLocalizations
/// returned by `AppLocalizations.of(context)`.
///
/// Applications need to include `AppLocalizations.delegate()` in their app's
/// `localizationDelegates` list, and the locales they support in the app's
/// `supportedLocales` list. For example:
///
/// ```dart
/// import 'l10n/app_localizations.dart';
///
/// return MaterialApp(
///   localizationsDelegates: AppLocalizations.localizationsDelegates,
///   supportedLocales: AppLocalizations.supportedLocales,
///   home: MyApplicationHome(),
/// );
/// ```
///
/// ## Update pubspec.yaml
///
/// Please make sure to update your pubspec.yaml to include the following
/// packages:
///
/// ```yaml
/// dependencies:
///   # Internationalization support.
///   flutter_localizations:
///     sdk: flutter
///   intl: any # Use the pinned version from flutter_localizations
///
///   # Rest of dependencies
/// ```
///
/// ## iOS Applications
///
/// iOS applications define key application metadata, including supported
/// locales, in an Info.plist file that is built into the application bundle.
/// To configure the locales supported by your app, you’ll need to edit this
/// file.
///
/// First, open your project’s ios/Runner.xcworkspace Xcode workspace file.
/// Then, in the Project Navigator, open the Info.plist file under the Runner
/// project’s Runner folder.
///
/// Next, select the Information Property List item, select Add Item from the
/// Editor menu, then select Localizations from the pop-up menu.
///
/// Select and expand the newly-created Localizations item then, for each
/// locale your application supports, add a new item and select the locale
/// you wish to add from the pop-up menu in the Value field. This list should
/// be consistent with the languages listed in the AppLocalizations.supportedLocales
/// property.
abstract class AppLocalizations {
  AppLocalizations(String locale)
    : localeName = intl.Intl.canonicalizedLocale(locale.toString());

  final String localeName;

  static AppLocalizations of(BuildContext context) {
    return Localizations.of<AppLocalizations>(context, AppLocalizations)!;
  }

  static const LocalizationsDelegate<AppLocalizations> delegate =
      _AppLocalizationsDelegate();

  /// A list of this localizations delegate along with the default localizations
  /// delegates.
  ///
  /// Returns a list of localizations delegates containing this delegate along with
  /// GlobalMaterialLocalizations.delegate, GlobalCupertinoLocalizations.delegate,
  /// and GlobalWidgetsLocalizations.delegate.
  ///
  /// Additional delegates can be added by appending to this list in
  /// MaterialApp. This list does not have to be used at all if a custom list
  /// of delegates is preferred or required.
  static const List<LocalizationsDelegate<dynamic>> localizationsDelegates =
      <LocalizationsDelegate<dynamic>>[
        delegate,
        GlobalMaterialLocalizations.delegate,
        GlobalCupertinoLocalizations.delegate,
        GlobalWidgetsLocalizations.delegate,
      ];

  /// A list of this localizations delegate's supported locales.
  static const List<Locale> supportedLocales = <Locale>[
    Locale('ar'),
    Locale('en'),
  ];

  /// No description provided for @appName.
  ///
  /// In en, this message translates to:
  /// **'Guad'**
  String get appName;

  /// No description provided for @navInbox.
  ///
  /// In en, this message translates to:
  /// **'Inbox'**
  String get navInbox;

  /// No description provided for @navActions.
  ///
  /// In en, this message translates to:
  /// **'Next'**
  String get navActions;

  /// No description provided for @navProfile.
  ///
  /// In en, this message translates to:
  /// **'Profile'**
  String get navProfile;

  /// No description provided for @loginHint.
  ///
  /// In en, this message translates to:
  /// **'Keep your tasks and account organized'**
  String get loginHint;

  /// No description provided for @loginSignIn.
  ///
  /// In en, this message translates to:
  /// **'Sign in'**
  String get loginSignIn;

  /// No description provided for @loginEmail.
  ///
  /// In en, this message translates to:
  /// **'Email'**
  String get loginEmail;

  /// No description provided for @loginPassword.
  ///
  /// In en, this message translates to:
  /// **'Password'**
  String get loginPassword;

  /// No description provided for @loginEmailRequired.
  ///
  /// In en, this message translates to:
  /// **'Email is required'**
  String get loginEmailRequired;

  /// No description provided for @loginEmailInvalid.
  ///
  /// In en, this message translates to:
  /// **'Enter a valid email'**
  String get loginEmailInvalid;

  /// No description provided for @loginPasswordRequired.
  ///
  /// In en, this message translates to:
  /// **'Password is required'**
  String get loginPasswordRequired;

  /// No description provided for @inboxWelcome.
  ///
  /// In en, this message translates to:
  /// **'Welcome back, {name}'**
  String inboxWelcome(String name);

  /// No description provided for @inboxCapture.
  ///
  /// In en, this message translates to:
  /// **'Capture'**
  String get inboxCapture;

  /// No description provided for @inboxEmptyTitle.
  ///
  /// In en, this message translates to:
  /// **'Inbox is clear'**
  String get inboxEmptyTitle;

  /// No description provided for @inboxEmptySubtitle.
  ///
  /// In en, this message translates to:
  /// **'Capture anything on your mind, then clarify it later.'**
  String get inboxEmptySubtitle;

  /// No description provided for @inboxProcessTooltip.
  ///
  /// In en, this message translates to:
  /// **'Process'**
  String get inboxProcessTooltip;

  /// No description provided for @inboxProcessNextAction.
  ///
  /// In en, this message translates to:
  /// **'Next action'**
  String get inboxProcessNextAction;

  /// No description provided for @inboxProcessProject.
  ///
  /// In en, this message translates to:
  /// **'Project'**
  String get inboxProcessProject;

  /// No description provided for @inboxProcessWaitingFor.
  ///
  /// In en, this message translates to:
  /// **'Waiting for'**
  String get inboxProcessWaitingFor;

  /// No description provided for @inboxProcessSomedayMaybe.
  ///
  /// In en, this message translates to:
  /// **'Someday maybe'**
  String get inboxProcessSomedayMaybe;

  /// No description provided for @inboxProcessReference.
  ///
  /// In en, this message translates to:
  /// **'Reference'**
  String get inboxProcessReference;

  /// No description provided for @inboxProcessTrash.
  ///
  /// In en, this message translates to:
  /// **'Trash'**
  String get inboxProcessTrash;

  /// No description provided for @inboxCapturePrompt.
  ///
  /// In en, this message translates to:
  /// **'What is it?'**
  String get inboxCapturePrompt;

  /// No description provided for @inboxNotes.
  ///
  /// In en, this message translates to:
  /// **'Notes'**
  String get inboxNotes;

  /// No description provided for @inboxTitleRequired.
  ///
  /// In en, this message translates to:
  /// **'Title is required'**
  String get inboxTitleRequired;

  /// No description provided for @inboxAddToInbox.
  ///
  /// In en, this message translates to:
  /// **'Add to inbox'**
  String get inboxAddToInbox;

  /// No description provided for @actionsAdd.
  ///
  /// In en, this message translates to:
  /// **'Add action'**
  String get actionsAdd;

  /// No description provided for @actionsComplete.
  ///
  /// In en, this message translates to:
  /// **'Complete'**
  String get actionsComplete;

  /// No description provided for @actionsEmptyTitle.
  ///
  /// In en, this message translates to:
  /// **'No next actions'**
  String get actionsEmptyTitle;

  /// No description provided for @actionsEmptySubtitle.
  ///
  /// In en, this message translates to:
  /// **'Clarified actions from your inbox will show up here.'**
  String get actionsEmptySubtitle;

  /// No description provided for @actionsPrompt.
  ///
  /// In en, this message translates to:
  /// **'What\'s the next action?'**
  String get actionsPrompt;

  /// No description provided for @actionsNotes.
  ///
  /// In en, this message translates to:
  /// **'Notes'**
  String get actionsNotes;

  /// No description provided for @actionsDescriptionRequired.
  ///
  /// In en, this message translates to:
  /// **'Description is required'**
  String get actionsDescriptionRequired;

  /// No description provided for @actionsAddAction.
  ///
  /// In en, this message translates to:
  /// **'Add action'**
  String get actionsAddAction;

  /// No description provided for @actionsMinutes.
  ///
  /// In en, this message translates to:
  /// **'{minutes} min'**
  String actionsMinutes(int minutes);

  /// No description provided for @profileTitle.
  ///
  /// In en, this message translates to:
  /// **'Profile'**
  String get profileTitle;

  /// No description provided for @profileAccount.
  ///
  /// In en, this message translates to:
  /// **'Account'**
  String get profileAccount;

  /// No description provided for @profileApp.
  ///
  /// In en, this message translates to:
  /// **'App'**
  String get profileApp;

  /// No description provided for @profilePersonalInfo.
  ///
  /// In en, this message translates to:
  /// **'Personal Information'**
  String get profilePersonalInfo;

  /// No description provided for @profileNotifications.
  ///
  /// In en, this message translates to:
  /// **'Notifications'**
  String get profileNotifications;

  /// No description provided for @profileSettings.
  ///
  /// In en, this message translates to:
  /// **'Language'**
  String get profileSettings;

  /// No description provided for @profileHelp.
  ///
  /// In en, this message translates to:
  /// **'Help & Support'**
  String get profileHelp;

  /// No description provided for @profileSignOut.
  ///
  /// In en, this message translates to:
  /// **'Sign out'**
  String get profileSignOut;

  /// No description provided for @profileAccountSettings.
  ///
  /// In en, this message translates to:
  /// **'Account & Security'**
  String get profileAccountSettings;

  /// No description provided for @personalInfoTitle.
  ///
  /// In en, this message translates to:
  /// **'Personal Information'**
  String get personalInfoTitle;

  /// No description provided for @personalInfoFirstName.
  ///
  /// In en, this message translates to:
  /// **'First Name'**
  String get personalInfoFirstName;

  /// No description provided for @personalInfoLastName.
  ///
  /// In en, this message translates to:
  /// **'Last Name'**
  String get personalInfoLastName;

  /// No description provided for @personalInfoEmail.
  ///
  /// In en, this message translates to:
  /// **'Email'**
  String get personalInfoEmail;

  /// No description provided for @personalInfoPhone.
  ///
  /// In en, this message translates to:
  /// **'Phone'**
  String get personalInfoPhone;

  /// No description provided for @notificationsTitle.
  ///
  /// In en, this message translates to:
  /// **'Notifications'**
  String get notificationsTitle;

  /// No description provided for @notificationsEmpty.
  ///
  /// In en, this message translates to:
  /// **'No notifications'**
  String get notificationsEmpty;

  /// No description provided for @notificationsCaughtUp.
  ///
  /// In en, this message translates to:
  /// **'You\'re all caught up'**
  String get notificationsCaughtUp;

  /// No description provided for @save.
  ///
  /// In en, this message translates to:
  /// **'Save'**
  String get save;

  /// No description provided for @accountTitle.
  ///
  /// In en, this message translates to:
  /// **'Account'**
  String get accountTitle;

  /// No description provided for @accountContact.
  ///
  /// In en, this message translates to:
  /// **'Contact'**
  String get accountContact;

  /// No description provided for @accountSecurity.
  ///
  /// In en, this message translates to:
  /// **'Security'**
  String get accountSecurity;

  /// No description provided for @accountEmail.
  ///
  /// In en, this message translates to:
  /// **'Email Address'**
  String get accountEmail;

  /// No description provided for @accountPhone.
  ///
  /// In en, this message translates to:
  /// **'Phone Number'**
  String get accountPhone;

  /// No description provided for @accountChangePassword.
  ///
  /// In en, this message translates to:
  /// **'Change Password'**
  String get accountChangePassword;

  /// No description provided for @accountNotSet.
  ///
  /// In en, this message translates to:
  /// **'Not set'**
  String get accountNotSet;

  /// No description provided for @accountEmailVerifyHint.
  ///
  /// In en, this message translates to:
  /// **'Changing your email address requires verification to keep your account secure.'**
  String get accountEmailVerifyHint;

  /// No description provided for @accountPhoneVerifyHint.
  ///
  /// In en, this message translates to:
  /// **'Changing your phone number requires verification to keep your account secure.'**
  String get accountPhoneVerifyHint;

  /// No description provided for @accountPasswordVerifyHint.
  ///
  /// In en, this message translates to:
  /// **'You will receive a verification code to confirm your identity before changing your password.'**
  String get accountPasswordVerifyHint;

  /// No description provided for @accountSecurityNote.
  ///
  /// In en, this message translates to:
  /// **'Changes to contact info and password require identity verification for your security.'**
  String get accountSecurityNote;

  /// No description provided for @biometricTitle.
  ///
  /// In en, this message translates to:
  /// **'Verify Identity'**
  String get biometricTitle;

  /// No description provided for @biometricSubtitle.
  ///
  /// In en, this message translates to:
  /// **'Use your biometrics to continue'**
  String get biometricSubtitle;

  /// No description provided for @biometricTryAgain.
  ///
  /// In en, this message translates to:
  /// **'Try Again'**
  String get biometricTryAgain;

  /// No description provided for @biometricUsePassword.
  ///
  /// In en, this message translates to:
  /// **'Use password instead'**
  String get biometricUsePassword;

  /// No description provided for @biometricEnable.
  ///
  /// In en, this message translates to:
  /// **'Biometric Login'**
  String get biometricEnable;

  /// No description provided for @biometricEnableHint.
  ///
  /// In en, this message translates to:
  /// **'Sign in faster using fingerprint or face recognition'**
  String get biometricEnableHint;
}

class _AppLocalizationsDelegate
    extends LocalizationsDelegate<AppLocalizations> {
  const _AppLocalizationsDelegate();

  @override
  Future<AppLocalizations> load(Locale locale) {
    return SynchronousFuture<AppLocalizations>(lookupAppLocalizations(locale));
  }

  @override
  bool isSupported(Locale locale) =>
      <String>['ar', 'en'].contains(locale.languageCode);

  @override
  bool shouldReload(_AppLocalizationsDelegate old) => false;
}

AppLocalizations lookupAppLocalizations(Locale locale) {
  // Lookup logic when only language code is specified.
  switch (locale.languageCode) {
    case 'ar':
      return AppLocalizationsAr();
    case 'en':
      return AppLocalizationsEn();
  }

  throw FlutterError(
    'AppLocalizations.delegate failed to load unsupported locale "$locale". This is likely '
    'an issue with the localizations generation tool. Please file an issue '
    'on GitHub with a reproducible sample app and the gen-l10n configuration '
    'that was used.',
  );
}
