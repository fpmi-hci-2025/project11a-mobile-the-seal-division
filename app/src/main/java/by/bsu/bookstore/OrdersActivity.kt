// File: app/src/main/java/by/bsu/bookstore/OrdersActivity.kt
package by.bsu.bookstore

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class OrdersActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orders)

        title = "Заказы"

        val rv = findViewById<RecyclerView>(R.id.ordersRecyclerView)
        rv.layoutManager = LinearLayoutManager(this)
        // временные данные
        val sample = listOf(
            Order(1001, User(1, "Иван","Иванов","ivan@example.com"), listOf(OrderItem(Book(1,"1984", listOf("Дж. Оруэлл"), "A", 350.0))), 350.0, "новый", "ул. Ленина, 1"),
            Order(1002, User(2, "Пётр","Петров","petrov@example.com"), listOf(OrderItem(Book(2,"Мастер и Маргарита", listOf("М. Булгаков"), "B", 500.0))), 500.0, "в обработке", "ул. Садовая, 5")
        )
        rv.adapter = OrderAdapter(sample) { order, action ->
            // обработка: change status
        }
    }
}
