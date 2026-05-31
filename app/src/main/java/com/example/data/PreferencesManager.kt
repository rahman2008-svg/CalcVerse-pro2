package com.example.data

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("calcverse_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_THEME = "theme_mode" // values: "light", "amoled", "glass", "dynamic"
        private const val KEY_KEEP_HISTORY = "keep_history"
        private const val KEY_VIBRATION = "vibration_feedback"
    }

    var themeMode: String
        get() = prefs.getString(KEY_THEME, "amoled") ?: "amoled"
        set(value) = prefs.edit().putString(KEY_THEME, value).apply()

    var keepHistory: Boolean
        get() = prefs.getBoolean(KEY_KEEP_HISTORY, true)
        set(value) = prefs.edit().putBoolean(KEY_KEEP_HISTORY, value).apply()

    var vibrationFeedback: Boolean
        get() = prefs.getBoolean(KEY_VIBRATION, true)
        set(value) = prefs.edit().putBoolean(KEY_VIBRATION, value).apply()
}
