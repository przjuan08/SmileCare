package sv.edu.udb.smilecare.utils

import android.content.Context
import android.content.SharedPreferences

class SharedPrefsManager(private val context: Context) {

    companion object {
        private const val PREFS_NAME = "SmileCarePrefs"
        private const val KEY_FIRST_RUN = "first_run"
        private const val KEY_USER_LOGGED_IN = "user_logged_in"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_NOTIFICATIONS_ENABLED = "notifications_enabled"
        private const val KEY_DARK_MODE = "dark_mode"
        private const val KEY_DATABASE_INITIALIZED = "database_initialized"
    }

    private val prefs: SharedPreferences by lazy {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    // First Run
    fun isFirstRun(): Boolean = prefs.getBoolean(KEY_FIRST_RUN, true)
    fun setFirstRunCompleted() = prefs.edit().putBoolean(KEY_FIRST_RUN, false).apply()

    // User Session
    fun setUserLoggedIn(userId: String, userName: String, userEmail: String) {
        prefs.edit().apply {
            putBoolean(KEY_USER_LOGGED_IN, true)
            putString(KEY_USER_ID, userId)
            putString(KEY_USER_NAME, userName)
            putString(KEY_USER_EMAIL, userEmail)
            apply()
        }
    }

    fun isUserLoggedIn(): Boolean = prefs.getBoolean(KEY_USER_LOGGED_IN, false)
    fun getUserId(): String? = prefs.getString(KEY_USER_ID, null)
    fun getUserName(): String? = prefs.getString(KEY_USER_NAME, null)
    fun getUserEmail(): String? = prefs.getString(KEY_USER_EMAIL, null)

    fun clearUserSession() {
        prefs.edit().apply {
            putBoolean(KEY_USER_LOGGED_IN, false)
            remove(KEY_USER_ID)
            remove(KEY_USER_NAME)
            remove(KEY_USER_EMAIL)
            apply()
        }
    }

    // Settings
    fun areNotificationsEnabled(): Boolean = prefs.getBoolean(KEY_NOTIFICATIONS_ENABLED, true)
    fun setNotificationsEnabled(enabled: Boolean) =
        prefs.edit().putBoolean(KEY_NOTIFICATIONS_ENABLED, enabled).apply()

    fun isDarkModeEnabled(): Boolean = prefs.getBoolean(KEY_DARK_MODE, false)
    fun setDarkModeEnabled(enabled: Boolean) =
        prefs.edit().putBoolean(KEY_DARK_MODE, enabled).apply()

    // Database
    fun isDatabaseInitialized(): Boolean = prefs.getBoolean(KEY_DATABASE_INITIALIZED, false)
    fun setDatabaseInitialized() = prefs.edit().putBoolean(KEY_DATABASE_INITIALIZED, true).apply()

    // Clear all preferences
    fun clearAllPreferences() {
        prefs.edit().clear().apply()
    }
}