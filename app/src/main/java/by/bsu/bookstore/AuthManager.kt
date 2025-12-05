// File: app/src/main/java/by/bsu/bookstore/AuthManager.kt
package by.bsu.bookstore

import android.content.Context
import android.content.SharedPreferences

object AuthManager {
    private const val PREF = "auth_prefs"
    private const val KEY_LOGGED = "is_logged"
    private const val KEY_USER = "user_email"

    private lateinit var sp: SharedPreferences

    fun init(context: Context) {
        sp = context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
    }

    fun isLogged(): Boolean = this::sp.isInitialized && sp.getBoolean(KEY_LOGGED, false)

    fun login(context: Context, email: String) {
        if (!this::sp.isInitialized) init(context)
        sp.edit().putBoolean(KEY_LOGGED, true).putString(KEY_USER, email).apply()
    }

    fun logout() {
        if (!this::sp.isInitialized) return
        sp.edit().putBoolean(KEY_LOGGED, false).remove(KEY_USER).apply()
    }

    fun currentUserEmail(): String? = if (this::sp.isInitialized) sp.getString(KEY_USER, null) else null
}
