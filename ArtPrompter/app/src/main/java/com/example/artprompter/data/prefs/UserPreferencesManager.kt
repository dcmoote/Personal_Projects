package com.dcmoote.inkwell.data.prefs

import android.content.Context
import android.content.SharedPreferences

class UserPreferencesManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    var onboardingComplete: Boolean
        get() = prefs.getBoolean(KEY_ONBOARDING_COMPLETE, false)
        set(value) = prefs.edit().putBoolean(KEY_ONBOARDING_COMPLETE, value).apply()

    var creativeType: String
        get() = prefs.getString(KEY_CREATIVE_TYPE, CreativeType.BOTH) ?: CreativeType.BOTH
        set(value) = prefs.edit().putString(KEY_CREATIVE_TYPE, value).apply()

    var writingGenres: Set<String>
        get() = prefs.getStringSet(KEY_WRITING_GENRES, emptySet()) ?: emptySet()
        set(value) = prefs.edit().putStringSet(KEY_WRITING_GENRES, value).apply()

    var artMediums: Set<String>
        get() = prefs.getStringSet(KEY_ART_MEDIUMS, emptySet()) ?: emptySet()
        set(value) = prefs.edit().putStringSet(KEY_ART_MEDIUMS, value).apply()

    var artSubjects: Set<String>
        get() = prefs.getStringSet(KEY_ART_SUBJECTS, emptySet()) ?: emptySet()
        set(value) = prefs.edit().putStringSet(KEY_ART_SUBJECTS, value).apply()

    var artThemes: Set<String>
        get() = prefs.getStringSet(KEY_ART_THEMES, emptySet()) ?: emptySet()
        set(value) = prefs.edit().putStringSet(KEY_ART_THEMES, value).apply()

    var directionLevel: Int
        get() = prefs.getInt(KEY_DIRECTION_LEVEL, DirectionLevel.GUIDED)
        set(value) = prefs.edit().putInt(KEY_DIRECTION_LEVEL, value).apply()

    var reminderEnabled: Boolean
        get() = prefs.getBoolean(KEY_REMINDER_ENABLED, false)
        set(value) = prefs.edit().putBoolean(KEY_REMINDER_ENABLED, value).apply()

    var reminderTimeHour: Int
        get() = prefs.getInt(KEY_REMINDER_HOUR, 9)
        set(value) = prefs.edit().putInt(KEY_REMINDER_HOUR, value).apply()

    var reminderTimeMinute: Int
        get() = prefs.getInt(KEY_REMINDER_MINUTE, 0)
        set(value) = prefs.edit().putInt(KEY_REMINDER_MINUTE, value).apply()

    var useAiWhenAvailable: Boolean
        get() = prefs.getBoolean(KEY_USE_AI, true)
        set(value) = prefs.edit().putBoolean(KEY_USE_AI, value).apply()

    var themeMode: String
        get() = prefs.getString(KEY_THEME_MODE, ThemeMode.SYSTEM) ?: ThemeMode.SYSTEM
        set(value) = prefs.edit().putString(KEY_THEME_MODE, value).apply()

    object CreativeType {
        const val WRITING = "WRITING"
        const val ART = "ART"
        const val BOTH = "BOTH"
    }

    object ArtSubject {
        const val PEOPLE = "People"
        const val LANDSCAPES = "Landscapes"
        const val ANIMALS = "Animals"
        const val ABSTRACT = "Abstract"
    }

    object DirectionLevel {
        const val MINIMAL = 1  // Single word or short phrase — just a seed
        const val GUIDED = 2   // A sentence or two — concept and mood
        const val DETAILED = 3 // Full specifics — just pick up the brush/pen
    }

    object ArtTheme {
        const val FANTASY = "Fantasy"
        const val SCI_FI = "Sci-Fi"
        const val DARK_GOTHIC = "Dark / Gothic"
        const val NATURE = "Nature"
        const val URBAN = "Urban"
        const val MYTHOLOGY = "Mythology"
        const val SURREAL = "Surreal"
        const val HORROR = "Horror"
        const val VINTAGE = "Vintage"
        const val KAWAII = "Kawaii"
    }

    object ThemeMode {
        const val LIGHT = "LIGHT"
        const val DARK = "DARK"
        const val SYSTEM = "SYSTEM"
    }

    companion object {
        private const val PREFS_NAME = "user_preferences"
        private const val KEY_ONBOARDING_COMPLETE = "onboarding_complete"
        private const val KEY_CREATIVE_TYPE = "creative_type"
        private const val KEY_WRITING_GENRES = "writing_genres"
        private const val KEY_ART_MEDIUMS = "art_mediums"
        private const val KEY_ART_SUBJECTS = "art_subjects"
        private const val KEY_ART_THEMES = "art_themes"
        private const val KEY_DIRECTION_LEVEL = "direction_level"
        private const val KEY_REMINDER_ENABLED = "reminder_enabled"
        private const val KEY_REMINDER_HOUR = "reminder_hour"
        private const val KEY_REMINDER_MINUTE = "reminder_minute"
        private const val KEY_USE_AI = "use_ai"
        private const val KEY_THEME_MODE = "theme_mode"
    }
}
