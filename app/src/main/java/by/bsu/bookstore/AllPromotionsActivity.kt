package by.bsu.bookstore

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.bsu.bookstore.adapters.AllPromotionsAdapter
import by.bsu.bookstore.api.ApiClient
import by.bsu.bookstore.model.Discount
import kotlinx.coroutines.*

class AllPromotionsActivity : BaseActivity() {

    private lateinit var rv: RecyclerView
    private val apiService = ApiClient.apiService
    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inflateContent(R.layout.activity_all_promotions)

        rv = findViewById(R.id.allPromotionsRecyclerView)
        rv.layoutManager = LinearLayoutManager(this)
        selectNavItem(R.id.nav_home)
        loadPromotions()
    }

    private fun loadPromotions() {
        showLoading(true)
        rv.visibility = View.GONE

        coroutineScope.launch {
            try {
                val discounts = withContext(Dispatchers.IO) {
                    apiService.getAllDiscounts().execute().body() ?: emptyList()
                }
                setupAdapter(discounts)
            } catch (e: Exception) {
                Toast.makeText(this@AllPromotionsActivity, "Ошибка загрузки: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                rv.visibility = View.VISIBLE
                showLoading(false)
            }
        }
    }

    private fun setupAdapter(discounts: List<Discount>) {
        if (discounts.isEmpty()) {
            Toast.makeText(this, "Нет доступных акций", Toast.LENGTH_SHORT).show()
        }
        rv.adapter = AllPromotionsAdapter(discounts) { promo ->
            AlertDialog.Builder(this@AllPromotionsActivity)
                .setTitle(promo.title)
                .setMessage(promo.description)
                .setPositiveButton("OK", null)
                .show()}
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
    }
}