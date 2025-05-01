package com.example.gymuz

import android.content.Context
import android.content.SharedPreferences

class UserPreferences(context: Context) {
    private val preferences: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun saveUserId(userId: Int) {
        preferences.edit().putInt(KEY_USER_ID, userId).apply()
    }

    fun getUserId(): Int {
        return preferences.getInt(KEY_USER_ID, -1)
    }

    fun saveUserEmail(email: String) {
        preferences.edit().putString(KEY_USER_EMAIL, email).apply()
    }

    fun getUserEmail(): String? {
        return preferences.getString(KEY_USER_EMAIL, null)
    }

    fun setLoggedIn(isLoggedIn: Boolean) {
        preferences.edit().putBoolean(KEY_IS_LOGGED_IN, isLoggedIn).apply()
    }

    fun isLoggedIn(): Boolean {
        return preferences.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun clearUserData() {
        preferences.edit()
            .remove(KEY_USER_ID)
            .remove(KEY_USER_EMAIL)
            .putBoolean(KEY_IS_LOGGED_IN, false)
            .apply()
    }

    companion object {
        private const val PREFS_NAME = "GymUzUserPrefs"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
    }
}