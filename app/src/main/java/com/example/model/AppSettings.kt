package com.example.model

enum class AppLanguage(val code: String, val displayName: String, val nativeName: String) {
    ENGLISH("en", "English", "English"),
    BENGALI("bn", "Bengali", "বাংলা")
}

enum class AccentColorTheme(val displayName: String, val primaryHex: Long, val darkPrimaryHex: Long) {
    BLUE("Blue", 0xFF0284C7, 0xFF38BDF8),
    PURPLE("Purple", 0xFF9333EA, 0xFFC084FC),
    GREEN("Green", 0xFF16A34A, 0xFF4ADE80),
    TEAL("Teal", 0xFF006A6A, 0xFF80D5D4),
    ORANGE("Orange", 0xFFEA580C, 0xFFFB923C),
    PINK("Pink", 0xFFE11D48, 0xFFFB7185),
    RED("Red", 0xFFDC2626, 0xFFF87171),
    INDIGO("Indigo", 0xFF4F46E5, 0xFF818CF8)
}

enum class ThemeMode {
    LIGHT, DARK, SYSTEM
}

enum class SortOption {
    NEWEST, OLDEST, NAME_ASC, NAME_DESC, PRICE_LOW_HIGH, PRICE_HIGH_LOW
}

data class AppSettings(
    val language: AppLanguage = AppLanguage.ENGLISH,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val accentColor: AccentColorTheme = AccentColorTheme.TEAL,
    val sortOption: SortOption = SortOption.NEWEST,
    val currencySymbol: String = "₹"
)
