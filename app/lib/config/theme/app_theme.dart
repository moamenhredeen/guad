import 'package:flutter/material.dart';
import 'package:guad/config/theme/app_colors.dart';

abstract class AppTheme {
  static const _radius = 8.0;

  static ThemeData get light {
    final base = ColorScheme.fromSeed(
      seedColor: AppColors.primary,
      brightness: Brightness.light,
    );
    final cs = base.copyWith(
      primary: AppColors.primary,
      onPrimary: Colors.white,
      primaryContainer: const Color(0xFFEDEBE7),
      onPrimaryContainer: AppColors.textPrimary,
      secondary: AppColors.secondary,
      tertiary: AppColors.tertiary,
      surface: AppColors.surface,
      onSurface: AppColors.textPrimary,
      onSurfaceVariant: AppColors.textSecondary,
      outline: const Color(0xFFC9C6C0),
      outlineVariant: AppColors.divider,
      surfaceContainerLowest: AppColors.background,
      surfaceContainerLow: AppColors.surface,
      surfaceContainer: const Color(0xFFF4F3F0),
      surfaceContainerHigh: const Color(0xFFEDEBE7),
      surfaceContainerHighest: const Color(0xFFE8E6E1),
    );

    return ThemeData(
      useMaterial3: true,
      colorScheme: cs,
      scaffoldBackgroundColor: AppColors.background,
      visualDensity: VisualDensity.standard,

      textTheme: Typography.material2021().black.apply(
        bodyColor: cs.onSurface,
        displayColor: cs.onSurface,
      ),

      appBarTheme: AppBarTheme(
        backgroundColor: Colors.transparent,
        foregroundColor: cs.onSurface,
        elevation: 0,
        scrolledUnderElevation: 0,
        surfaceTintColor: Colors.transparent,
        centerTitle: false,
        titleTextStyle: TextStyle(
          color: cs.onSurface,
          fontSize: 18,
          fontWeight: FontWeight.w600,
        ),
        iconTheme: IconThemeData(color: cs.onSurfaceVariant, size: 22),
        actionsIconTheme: IconThemeData(color: cs.onSurfaceVariant, size: 22),
      ),

      inputDecorationTheme: InputDecorationTheme(
        filled: true,
        fillColor: cs.surface,
        contentPadding: const EdgeInsets.symmetric(
          horizontal: 14,
          vertical: 14,
        ),
        border: OutlineInputBorder(
          borderRadius: BorderRadius.circular(_radius),
          borderSide: BorderSide(color: cs.outlineVariant),
        ),
        enabledBorder: OutlineInputBorder(
          borderRadius: BorderRadius.circular(_radius),
          borderSide: BorderSide(color: cs.outlineVariant),
        ),
        focusedBorder: OutlineInputBorder(
          borderRadius: BorderRadius.circular(_radius),
          borderSide: BorderSide(color: cs.primary),
        ),
        errorBorder: OutlineInputBorder(
          borderRadius: BorderRadius.circular(_radius),
          borderSide: BorderSide(color: cs.error, width: 1),
        ),
        focusedErrorBorder: OutlineInputBorder(
          borderRadius: BorderRadius.circular(_radius),
          borderSide: BorderSide(color: cs.error),
        ),
        labelStyle: TextStyle(color: cs.onSurfaceVariant),
        floatingLabelStyle: TextStyle(color: cs.primary),
        prefixIconColor: WidgetStateColor.resolveWith(
          (states) => states.contains(WidgetState.focused)
              ? cs.primary
              : cs.onSurfaceVariant,
        ),
      ),

      filledButtonTheme: FilledButtonThemeData(
        style: FilledButton.styleFrom(
          minimumSize: const Size.fromHeight(48),
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(_radius),
          ),
          textStyle: const TextStyle(
            fontSize: 15,
            fontWeight: FontWeight.w600,
            letterSpacing: 0,
          ),
        ),
      ),

      elevatedButtonTheme: ElevatedButtonThemeData(
        style: ElevatedButton.styleFrom(
          elevation: 0,
          minimumSize: const Size.fromHeight(48),
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(_radius),
          ),
        ),
      ),

      outlinedButtonTheme: OutlinedButtonThemeData(
        style: OutlinedButton.styleFrom(
          minimumSize: const Size.fromHeight(48),
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(_radius),
          ),
          side: BorderSide(color: cs.outlineVariant),
          foregroundColor: cs.onSurface,
        ),
      ),

      cardTheme: CardThemeData(
        elevation: 0,
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(_radius),
          side: BorderSide(color: cs.outlineVariant),
        ),
        color: cs.surface,
        margin: EdgeInsets.zero,
      ),

      chipTheme: ChipThemeData(
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(6)),
        side: BorderSide(color: cs.outlineVariant),
        backgroundColor: cs.surface,
        selectedColor: cs.surfaceContainerHigh,
        showCheckmark: false,
        labelStyle: TextStyle(
          color: cs.onSurfaceVariant,
          fontWeight: FontWeight.w500,
          fontSize: 13,
        ),
        secondaryLabelStyle: TextStyle(
          color: cs.onPrimaryContainer,
          fontWeight: FontWeight.w600,
          fontSize: 13,
        ),
        padding: const EdgeInsets.symmetric(horizontal: 4),
      ),

      navigationBarTheme: NavigationBarThemeData(
        backgroundColor: cs.surface,
        surfaceTintColor: Colors.transparent,
        indicatorColor: cs.surfaceContainerHigh,
        height: 64,
        elevation: 0,
        labelTextStyle: WidgetStateTextStyle.resolveWith(
          (states) => TextStyle(
            fontSize: 12,
            fontWeight: states.contains(WidgetState.selected)
                ? FontWeight.w600
                : FontWeight.normal,
            color: states.contains(WidgetState.selected)
                ? cs.onSurface
                : cs.onSurfaceVariant,
          ),
        ),
      ),

      dividerTheme: DividerThemeData(color: cs.outlineVariant, space: 0),

      bottomSheetTheme: BottomSheetThemeData(
        backgroundColor: cs.surface,
        surfaceTintColor: Colors.transparent,
        shape: const RoundedRectangleBorder(
          borderRadius: BorderRadius.vertical(top: Radius.circular(12)),
        ),
      ),

      floatingActionButtonTheme: FloatingActionButtonThemeData(
        elevation: 0,
        focusElevation: 0,
        hoverElevation: 0,
        highlightElevation: 0,
        backgroundColor: cs.onSurface,
        foregroundColor: cs.surface,
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
      ),

      listTileTheme: ListTileThemeData(
        iconColor: cs.onSurfaceVariant,
        contentPadding: const EdgeInsets.symmetric(horizontal: 16),
        minLeadingWidth: 24,
      ),

      popupMenuTheme: PopupMenuThemeData(
        color: cs.surface,
        surfaceTintColor: Colors.transparent,
        elevation: 1,
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(8)),
      ),

      snackBarTheme: SnackBarThemeData(
        behavior: SnackBarBehavior.floating,
        backgroundColor: cs.onSurface,
        contentTextStyle: TextStyle(color: cs.surface),
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(8)),
      ),
    );
  }

  static ThemeData get dark {
    final base = ColorScheme.fromSeed(
      seedColor: AppColors.primary,
      brightness: Brightness.dark,
    );
    final cs = base.copyWith(
      primary: AppColors.textPrimaryDark,
      onPrimary: AppColors.backgroundDark,
      primaryContainer: const Color(0xFF303030),
      onPrimaryContainer: AppColors.textPrimaryDark,
      surface: AppColors.surfaceDark,
      onSurface: AppColors.textPrimaryDark,
      onSurfaceVariant: AppColors.textSecondaryDark,
      outlineVariant: const Color(0xFF303030),
      surfaceContainerLowest: AppColors.backgroundDark,
      surfaceContainer: const Color(0xFF202020),
    );
    return ThemeData(
      useMaterial3: true,
      colorScheme: cs,
      scaffoldBackgroundColor: AppColors.backgroundDark,
      appBarTheme: AppBarTheme(
        backgroundColor: Colors.transparent,
        foregroundColor: cs.onSurface,
        elevation: 0,
        scrolledUnderElevation: 0,
        surfaceTintColor: Colors.transparent,
        titleTextStyle: TextStyle(
          color: cs.onSurface,
          fontSize: 18,
          fontWeight: FontWeight.w600,
        ),
      ),
      cardTheme: CardThemeData(
        elevation: 0,
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(_radius),
          side: BorderSide(color: cs.outlineVariant),
        ),
        color: cs.surface,
        margin: EdgeInsets.zero,
      ),
    );
  }
}
