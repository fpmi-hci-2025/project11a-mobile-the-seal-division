package by.bsu.bookstore

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton

class CartActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var totalPriceText: TextView
    private lateinit var checkoutButton: MaterialButton

    private lateinit var adapter: CartAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        recyclerView = findViewById(R.id.cartRecyclerView)
        totalPriceText = findViewById(R.id.cartTotalPrice)
        checkoutButton = findViewById(R.id.cartCheckoutButton)

        adapter = CartAdapter(
            items = CartManager.getItems().toMutableList(),
            onQuantityChanged = { updateTotal() },
            onItemRemoved = {
                updateTotal()
                refreshList()
            }
        )

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        updateTotal()

        checkoutButton.setOnClickListener {
            if (CartManager.getItems().isEmpty()) {
                android.widget.Toast.makeText(this, "Корзина пуста", android.widget.Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            startActivity(Intent(this, CheckoutActivity::class.java))
        }
    }

    private fun refreshList() {
        adapter.items = CartManager.getItems().toMutableList()
        adapter.notifyDataSetChanged()
    }

    private fun updateTotal() {
        val total = CartManager.getTotal()
        totalPriceText.text = "Итого: %.2f BYN".format(total)
    }
}
