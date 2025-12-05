// File: app/src/main/java/by/bsu/bookstore/NotificationsManager.kt
package by.bsu.bookstore

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

data class NotificationItem(val id: Long, val title: String, val text: String, var read: Boolean = false)

object NotificationsManager {
    private const val PREF = "notif_prefs"
    private const val KEY_LIST = "notif_list"
    private lateinit var sp: SharedPreferences
    private val gson = Gson()
    private var list: MutableList<NotificationItem> = mutableListOf()

    fun init(context: Context) {
        sp = context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
        val json = sp.getString(KEY_LIST, null)
        if (!json.isNullOrEmpty()) {
            val type = object : TypeToken<MutableList<NotificationItem>>() {}.type
            list = gson.fromJson(json, type)
        } else list = mutableListOf()
    }

    fun addNotification(title: String, text: String) {
        val item = NotificationItem(System.currentTimeMillis(), title, text, false)
        list.add(0, item)
        save()
    }

    fun markAllRead() {
        list.forEach { it.read = true }
        save()
    }

    fun hasUnread(): Boolean = list.any { !it.read }

    fun all(): List<NotificationItem> = list.toList()

    private fun save() {
        if (!this::sp.isInitialized) return
        sp.edit().putString(KEY_LIST, gson.toJson(list)).apply()
    }

    fun clear() {
        list.clear()
        save()
    }
}
