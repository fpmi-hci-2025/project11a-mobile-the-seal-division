package by.bsu.bookstore

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.bsu.bookstore.adapters.OrderAdapter
import by.bsu.bookstore.api.ApiClient
import by.bsu.bookstore.model.Order
import kotlinx.coroutines.*

class OrdersActivity : BaseActivity() {

    private lateinit var rv: RecyclerView
    private val apiService = ApiClient.apiService
    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inflateContent(R.layout.activity_orders)
        selectNavItem(R.id.nav_home)
        title = "Все заказы"

        rv = findViewById(R.id.ordersRecyclerView)
        rv.layoutManager = LinearLayoutManager(this)

        loadAllOrders()
    }

    private fun loadAllOrders() {
        showData(false)
        showLoading(true)
        coroutineScope.launch {
            try {
                val orders = withContext(Dispatchers.IO) {
                    apiService.getAllOrders().execute().body() ?: emptyList()
                }
                rv.adapter = OrderAdapter(orders) { order, action ->
                    // обработка: change status
                }
            } catch (e: Exception) {
                Toast.makeText(this@OrdersActivity, "Ошибка загрузки заказов", Toast.LENGTH_LONG).show()
            } finally {
                showLoading(false)
                showData(true)
            }
        }
    }

    private fun showData(show: Boolean) {
        rv.visibility = if (show) View.VISIBLE else View.INVISIBLE
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
    }
}