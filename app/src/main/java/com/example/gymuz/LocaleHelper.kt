package com.example.gymuz

import android.content.Context
import java.util.Locale

object LocaleHelper {
    private const val APP_SETTINGS = "AppSettings"
    private const val APP_LANGUAGE = "app_language"

    fun onAttach(context: Context): Context {
        val lang = getPersistedLanguage(context)
        return setLocale(context, lang)
    }


    fun getLanguage(context: Context): String {
        return getPersistedLanguage(context)
    }
    // ------------------------------------

    fun setLocale(context: Context, language: String): Context {
        persistLanguage(context, language)
        val locale = Locale(language)
        Locale.setDefault(locale)

        val config = context.resources.configuration
        config.setLocale(locale)
        config.setLayoutDirection(locale)

        return context.createConfigurationContext(config)
    }

    private fun getPersistedLanguage(context: Context): String {
        val prefs = context.getSharedPreferences(APP_SETTINGS, Context.MODE_PRIVATE)
        return prefs.getString(APP_LANGUAGE, "en") ?: "en"
    }

    private fun persistLanguage(context: Context, language: String) {
        val prefs = context.getSharedPreferences(APP_SETTINGS, Context.MODE_PRIVATE)
        prefs.edit().putString(APP_LANGUAGE, language).apply()
    }
}