// ignore: unused_import
import 'package:intl/intl.dart' as intl;
import 'app_localizations.dart';

// ignore_for_file: type=lint

/// The translations for English (`en`).
class AppLocalizationsEn extends AppLocalizations {
  AppLocalizationsEn([String locale = 'en']) : super(locale);

  @override
  String get appName => 'Guad';

  @override
  String get navInbox => 'Inbox';

  @override
  String get navActions => 'Next';

  @override
  String get navProfile => 'Profile';

  @override
  String get loginHint => 'Keep your tasks and account organized';

  @override
  String get loginSignIn => 'Sign in';

  @override
  String get loginEmail => 'Email';

  @override
  String get loginPassword => 'Password';

  @override
  String get loginEmailRequired => 'Email is required';

  @override
  String get loginEmailInvalid => 'Enter a valid email';

  @override
  String get loginPasswordRequired => 'Password is required';

  @override
  String inboxWelcome(String name) {
    return 'Welcome back, $name';
  }

  @override
  String get inboxCapture => 'Capture';

  @override
  String get inboxEmptyTitle => 'Inbox is clear';

  @override
  String get inboxEmptySubtitle =>
      'Capture anything on your mind, then clarify it later.';

  @override
  String get inboxProcessTooltip => 'Process';

  @override
  String get inboxProcessNextAction => 'Next action';

  @override
  String get inboxProcessProject => 'Project';

  @override
  String get inboxProcessWaitingFor => 'Waiting for';

  @override
  String get inboxProcessSomedayMaybe => 'Someday maybe';

  @override
  String get inboxProcessReference => 'Reference';

  @override
  String get inboxProcessTrash => 'Trash';

  @override
  String get inboxCapturePrompt => 'What is it?';

  @override
  String get inboxNotes => 'Notes';

  @override
  String get inboxTitleRequired => 'Title is required';

  @override
  String get inboxAddToInbox => 'Add to inbox';

  @override
  String get actionsAdd => 'Add action';

  @override
  String get actionsComplete => 'Complete';

  @override
  String get actionsEmptyTitle => 'No next actions';

  @override
  String get actionsEmptySubtitle =>
      'Clarified actions from your inbox will show up here.';

  @override
  String get actionsPrompt => 'What\'s the next action?';

  @override
  String get actionsNotes => 'Notes';

  @override
  String get actionsDescriptionRequired => 'Description is required';

  @override
  String get actionsAddAction => 'Add action';

  @override
  String actionsMinutes(int minutes) {
    return '$minutes min';
  }

  @override
  String get profileTitle => 'Profile';

  @override
  String get profileAccount => 'Account';

  @override
  String get profileApp => 'App';

  @override
  String get profilePersonalInfo => 'Personal Information';

  @override
  String get profileNotifications => 'Notifications';

  @override
  String get profileSettings => 'Language';

  @override
  String get profileHelp => 'Help & Support';

  @override
  String get profileSignOut => 'Sign out';

  @override
  String get profileAccountSettings => 'Account & Security';

  @override
  String get personalInfoTitle => 'Personal Information';

  @override
  String get personalInfoFirstName => 'First Name';

  @override
  String get personalInfoLastName => 'Last Name';

  @override
  String get personalInfoEmail => 'Email';

  @override
  String get personalInfoPhone => 'Phone';

  @override
  String get notificationsTitle => 'Notifications';

  @override
  String get notificationsEmpty => 'No notifications';

  @override
  String get notificationsCaughtUp => 'You\'re all caught up';

  @override
  String get save => 'Save';

  @override
  String get accountTitle => 'Account';

  @override
  String get accountContact => 'Contact';

  @override
  String get accountSecurity => 'Security';

  @override
  String get accountEmail => 'Email Address';

  @override
  String get accountPhone => 'Phone Number';

  @override
  String get accountChangePassword => 'Change Password';

  @override
  String get accountNotSet => 'Not set';

  @override
  String get accountEmailVerifyHint =>
      'Changing your email address requires verification to keep your account secure.';

  @override
  String get accountPhoneVerifyHint =>
      'Changing your phone number requires verification to keep your account secure.';

  @override
  String get accountPasswordVerifyHint =>
      'You will receive a verification code to confirm your identity before changing your password.';

  @override
  String get accountSecurityNote =>
      'Changes to contact info and password require identity verification for your security.';

  @override
  String get biometricTitle => 'Verify Identity';

  @override
  String get biometricSubtitle => 'Use your biometrics to continue';

  @override
  String get biometricTryAgain => 'Try Again';

  @override
  String get biometricUsePassword => 'Use password instead';

  @override
  String get biometricEnable => 'Biometric Login';

  @override
  String get biometricEnableHint =>
      'Sign in faster using fingerprint or face recognition';
}
