package by.bsu.bookstore.managers

import android.content.Context
import by.bsu.bookstore.model.Book
import by.bsu.bookstore.model.OrderItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object CartManager {
    private const val PREFS = "cart_prefs"
    private const val KEY = "cart_key"
    private val gson = Gson()
    private val items = mutableListOf<OrderItem>()

    fun init(context: Context) {
        val sp = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val json = sp.getString(KEY, null)
        if (!json.isNullOrEmpty()) {
            val type = object : TypeToken<MutableList<OrderItem>>() {}.type
            items.clear()
            items.addAll(gson.fromJson(json, type))
        }
    }

    fun addToCart(context: Context, book: Book, quantity: Int = 1) {
        val existing = items.find { it.book.id == book.id && it.book.title == book.title }
        if (existing != null) {
            existing.quantity += quantity
        } else {
            items.add(OrderItem(book, quantity))
        }
        save(context)
    }

    fun removeFromCart(context: Context, bookId: Int) {
        items.removeAll { it.book.id == bookId }
        save(context)
    }

    fun clear(context: Context) {
        items.clear()
        save(context)
    }

    fun getItems(): List<OrderItem> = items.toList()

    fun getTotal(): Double = items.sumOf { it.book.price * it.quantity }

    private fun save(context: Context) {
        val sp = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        sp.edit().putString(KEY, gson.toJson(items)).apply()
    }
}
