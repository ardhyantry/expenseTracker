package com.bebas.expensetracker.util

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREF_NAME = "expense_session"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USERNAME = "username"
        private const val KEY_FIRST_NAME = "first_name"
        private const val KEY_LAST_NAME = "last_name"
        private const val KEY_IS_LOGIN = "is_login"
    }

    fun saveSession(
        id: Int,
        username: String,
        firstName: String,
        lastName: String
    ) {
        prefs.edit()
            .putInt(KEY_USER_ID, id)
            .putString(KEY_USERNAME, username)
            .putString(KEY_FIRST_NAME, firstName)
            .putString(KEY_LAST_NAME, lastName)
            .putBoolean(KEY_IS_LOGIN, true)
            .apply()
    }

    fun getUserId(): Int = prefs.getInt(KEY_USER_ID, -1)

    fun getUsername(): String? = prefs.getString(KEY_USERNAME, null)

    fun getFirstName(): String? = prefs.getString(KEY_FIRST_NAME, null)

    fun getLastName(): String? = prefs.getString(KEY_LAST_NAME, null)

    fun getFullName(): String = "${getFirstName() ?: ""} ${getLastName() ?: ""}".trim()

    fun isLoggedIn(): Boolean = prefs.getBoolean(KEY_IS_LOGIN, false)

    fun clearSession() {
        prefs.edit().clear().apply()
    }
}
