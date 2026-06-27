// ignore: unused_import
import 'package:intl/intl.dart' as intl;
import 'app_localizations.dart';

// ignore_for_file: type=lint

/// The translations for Arabic (`ar`).
class AppLocalizationsAr extends AppLocalizations {
  AppLocalizationsAr([String locale = 'ar']) : super(locale);

  @override
  String get appName => 'Guad';

  @override
  String get navInbox => 'الوارد';

  @override
  String get navProfile => 'الملف الشخصي';

  @override
  String get loginHint => 'نظّم مهامك وحسابك بسهولة';

  @override
  String get loginSignIn => 'تسجيل الدخول';

  @override
  String get loginEmail => 'البريد الإلكتروني';

  @override
  String get loginPassword => 'كلمة المرور';

  @override
  String get loginEmailRequired => 'البريد الإلكتروني مطلوب';

  @override
  String get loginEmailInvalid => 'أدخل بريداً إلكترونياً صحيحاً';

  @override
  String get loginPasswordRequired => 'كلمة المرور مطلوبة';

  @override
  String inboxWelcome(String name) {
    return 'مرحباً، $name';
  }

  @override
  String get inboxCapture => 'التقاط';

  @override
  String get inboxEmptyTitle => 'صندوق الوارد فارغ';

  @override
  String get inboxEmptySubtitle => 'التقط أي شيء يشغل بالك، ثم وضّحه لاحقاً.';

  @override
  String get inboxProcessTooltip => 'معالجة';

  @override
  String get inboxProcessNextAction => 'إجراء تالي';

  @override
  String get inboxProcessProject => 'مشروع';

  @override
  String get inboxProcessWaitingFor => 'بانتظار';

  @override
  String get inboxProcessSomedayMaybe => 'ربما يوماً ما';

  @override
  String get inboxProcessReference => 'مرجع';

  @override
  String get inboxProcessTrash => 'حذف';

  @override
  String get inboxCapturePrompt => 'ما هو؟';

  @override
  String get inboxNotes => 'ملاحظات';

  @override
  String get inboxTitleRequired => 'العنوان مطلوب';

  @override
  String get inboxAddToInbox => 'إضافة إلى الوارد';

  @override
  String get profileTitle => 'الملف الشخصي';

  @override
  String get profileAccount => 'الحساب';

  @override
  String get profileApp => 'التطبيق';

  @override
  String get profilePersonalInfo => 'المعلومات الشخصية';

  @override
  String get profileNotifications => 'الإشعارات';

  @override
  String get profileSettings => 'اللغة';

  @override
  String get profileHelp => 'المساعدة والدعم';

  @override
  String get profileSignOut => 'تسجيل الخروج';

  @override
  String get profileAccountSettings => 'الحساب والأمان';

  @override
  String get personalInfoTitle => 'المعلومات الشخصية';

  @override
  String get personalInfoFirstName => 'الاسم الأول';

  @override
  String get personalInfoLastName => 'اسم العائلة';

  @override
  String get personalInfoEmail => 'البريد الإلكتروني';

  @override
  String get personalInfoPhone => 'الهاتف';

  @override
  String get notificationsTitle => 'الإشعارات';

  @override
  String get notificationsEmpty => 'لا توجد إشعارات';

  @override
  String get notificationsCaughtUp => 'أنت مطلع على كل شيء';

  @override
  String get save => 'حفظ';

  @override
  String get accountTitle => 'الحساب';

  @override
  String get accountContact => 'معلومات التواصل';

  @override
  String get accountSecurity => 'الأمان';

  @override
  String get accountEmail => 'البريد الإلكتروني';

  @override
  String get accountPhone => 'رقم الهاتف';

  @override
  String get accountChangePassword => 'تغيير كلمة المرور';

  @override
  String get accountNotSet => 'غير محدد';

  @override
  String get accountEmailVerifyHint =>
      'تغيير بريدك الإلكتروني يتطلب التحقق للحفاظ على أمان حسابك.';

  @override
  String get accountPhoneVerifyHint =>
      'تغيير رقم هاتفك يتطلب التحقق للحفاظ على أمان حسابك.';

  @override
  String get accountPasswordVerifyHint =>
      'ستتلقى رمز تحقق لتأكيد هويتك قبل تغيير كلمة المرور.';

  @override
  String get accountSecurityNote =>
      'التغييرات على معلومات التواصل وكلمة المرور تتطلب التحقق من الهوية لأمانك.';

  @override
  String get biometricTitle => 'التحقق من الهوية';

  @override
  String get biometricSubtitle => 'استخدم بصمتك للمتابعة';

  @override
  String get biometricTryAgain => 'حاول مرة أخرى';

  @override
  String get biometricUsePassword => 'استخدم كلمة المرور';

  @override
  String get biometricEnable => 'تسجيل الدخول بالبصمة';

  @override
  String get biometricEnableHint =>
      'سجّل الدخول بسرعة باستخدام البصمة أو التعرف على الوجه';
}
