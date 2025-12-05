package by.bsu.bookstore

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object SubscriptionManager {
    private const val PREF = "sub_prefs"
    private const val KEY = "publishers"
    private lateinit var sp: SharedPreferences
    private val gson = Gson()
    private var publishers: MutableSet<String> = mutableSetOf()

    fun init(context: Context) {
        if (!this::sp.isInitialized) sp = context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
        val json = sp.getString(KEY, null)
        if (!json.isNullOrEmpty()) {
            val type = object : TypeToken<MutableSet<String>>() {}.type
            publishers = gson.fromJson(json, type)
        } else {
            publishers = mutableSetOf()
        }
    }

    fun subscribe(publisher: String) {
        publishers.add(publisher)
        save()
    }

    fun unsubscribe(publisher: String) {
        publishers.remove(publisher)
        save()
    }

    fun isSubscribed(publisher: String): Boolean {
        return publishers.contains(publisher)
    }

    private fun save() {
        if (!this::sp.isInitialized) return
        sp.edit().putString(KEY, gson.toJson(publishers)).apply()
    }
}
