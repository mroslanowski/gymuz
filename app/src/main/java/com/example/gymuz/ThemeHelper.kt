package com.example.gymuz

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate

object ThemeHelper {
    private const val APP_SETTINGS = "AppSettings"
    private const val DARK_MODE = "dark_mode"

    fun applyTheme(context: Context) {
        val prefs = context.getSharedPreferences(APP_SETTINGS, Context.MODE_PRIVATE)
        val isDarkMode = prefs.getBoolean(DARK_MODE, false)
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }
}