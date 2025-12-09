// File: app/src/main/java/by/bsu/bookstore/OrdersActivity.kt
package by.bsu.bookstore

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.bsu.bookstore.adapters.OrderAdapter
import by.bsu.bookstore.repositories.OrdersRepository

class OrdersActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inflateContent(R.layout.activity_orders)
        //setupBottomNav(R.id.nav_profile)
        title = "Заказы"

        val rv = findViewById<RecyclerView>(R.id.ordersRecyclerView)
        rv.layoutManager = LinearLayoutManager(this)

        val sample = OrdersRepository.getAllOrders()
        rv.adapter = OrderAdapter(sample) { order, action ->
            // обработка: change status
        }
    }
}
