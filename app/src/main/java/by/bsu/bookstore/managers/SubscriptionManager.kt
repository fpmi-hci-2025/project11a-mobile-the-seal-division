package by.bsu.bookstore.managers

import android.content.Context
import android.content.SharedPreferences
import by.bsu.bookstore.model.Publisher
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object SubscriptionManager {
    private const val PREF = "sub_prefs"
    private const val KEY = "publishers"
    private lateinit var sp: SharedPreferences
    private val gson = Gson()
    private var publishers: MutableSet<Int> = mutableSetOf()

    fun init(context: Context) {
        if (!this::sp.isInitialized) sp = context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
        val json = sp.getString(KEY, null)
        if (!json.isNullOrEmpty()) {
            val type = object : TypeToken<MutableSet<Int>>() {}.type
            publishers = gson.fromJson(json, type)
        } else {
            publishers = mutableSetOf()
        }
    }

    fun subscribe(publisher: Publisher) {
        publishers.add(publisher.id)
        save()
    }

    fun unsubscribe(publisher: Publisher) {
        publishers.remove(publisher.id)
        save()
    }

    fun isSubscribed(publisher_id: Int): Boolean {
        return publishers.contains(publisher_id)
    }

    private fun save() {
        if (!this::sp.isInitialized) return
        sp.edit().putString(KEY, gson.toJson(publishers)).apply()
    }
}
