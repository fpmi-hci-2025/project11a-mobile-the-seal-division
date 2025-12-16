package by.bsu.bookstore.managers

import android.content.Context
import android.graphics.Paint
import android.view.View
import android.widget.TextView
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

    fun getItemsToString(): String {
        var strings = ""
        for(item in items){
            strings = strings.plus(item.book.title)
            strings = strings.plus("\n")
        }
        return strings
    }

    fun getTotal(): Double = items.sumOf { getPriceWithDiscount(it.book) * it.quantity }

    private fun save(context: Context) {
        val sp = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        sp.edit().putString(KEY, gson.toJson(items)).apply()
    }

    private fun getPriceWithDiscount(book: Book): Double {
        val discount = book.discountObj
        if (discount != null && discount.percentage > 0) {
            val oldPrice = book.price
            val newPrice = oldPrice * (1 - discount.percentage / 100.0)
            return newPrice
        } else {
            return book.price
        }
    }
}
