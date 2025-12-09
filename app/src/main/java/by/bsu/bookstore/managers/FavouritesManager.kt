package by.bsu.bookstore.managers

import android.content.Context
import by.bsu.bookstore.model.Book
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object FavoritesManager {
    private const val PREFS = "favorites_prefs"
    private const val KEY = "favorites_key"
    private val gson = Gson()
    private var favorites: MutableList<Book> = mutableListOf()

    fun init(context: Context) {
        val sp = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val json = sp.getString(KEY, null)
        if (!json.isNullOrEmpty()) {
            val type = object : TypeToken<MutableList<Book>>() {}.type
            favorites = gson.fromJson(json, type)
        }
    }

    fun toggleFavorite(context: Context, book: Book) {
        if (isFavorite(book)) {
            favorites.removeAll { it.id == book.id }
        } else {
            favorites.add(book)
        }
        save(context)
    }

    fun isFavorite(book: Book): Boolean = favorites.any { it.id == book.id }
    fun getFavorites(): List<Book> = favorites.toList()

    private fun save(context: Context) {
        val sp = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        sp.edit().putString(KEY, gson.toJson(favorites)).apply()
    }
}
