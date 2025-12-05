package by.bsu.bookstore

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CartActivity : BaseActivity() {

    private lateinit var recycler: RecyclerView
    private lateinit var totalText: TextView
    private lateinit var emptyText: TextView
    private lateinit var checkoutButton: View

    private lateinit var adapter: CartItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inflateContent(R.layout.activity_cart)
        selectNavItem(R.id.nav_cart)

        recycler = findViewById(R.id.cartRecyclerView)
        totalText = findViewById(R.id.cartTotalPrice)
        emptyText = findViewById(R.id.cartEmptyText)
        checkoutButton = findViewById(R.id.cartCheckoutButton)
        recycler.layoutManager = LinearLayoutManager(this)
        setupAdapter()
        updateUI()

        checkoutButton.setOnClickListener {
            startActivity(Intent(this, CheckoutActivity::class.java))
        }
    }

    private fun setupAdapter() {
        // Получаем актуальные данные из CartManager
        val items = CartManager.getItems().map { CartItem(it.book, it.quantity) }.toMutableList()

        adapter = CartItemAdapter(
            items = items,
            onIncrease = { cartItem ->
                // Обновляем количество в CartManager
                CartManager.addToCart(this, cartItem.book, 1)
                refreshData()
            },
            onDecrease = { cartItem ->
                if (cartItem.quantity > 1) {
                    // Уменьшаем количество
                    CartManager.removeFromCart(this, cartItem.book.bookId)
                    CartManager.addToCart(this, cartItem.book, cartItem.quantity - 1)
                } else {
                    // Удаляем товар
                    CartManager.removeFromCart(this, cartItem.book.bookId)
                }
                refreshData()
            },
            onDelete = { cartItem ->
                CartManager.removeFromCart(this, cartItem.book.bookId)
                refreshData()
            }
        )

        recycler.adapter = adapter
    }

    private fun refreshData() {
        // Обновляем список в адаптере актуальными данными из CartManager
        val freshItems = CartManager.getItems().map { CartItem(it.book, it.quantity) }
        adapter.items.clear()
        adapter.items.addAll(freshItems)
        adapter.notifyDataSetChanged()
        updateUI()
    }

    override fun onResume() {
        super.onResume()
        // При возврате на экран обновляем данные
        refreshData()
    }

    private fun updateUI() {
        val total = CartManager.getTotal()
        totalText.text = "Итого: %.2f BYN".format(total)

        val isEmpty = CartManager.getItems().isEmpty()

        if (isEmpty) {
            recycler.visibility = View.GONE
            checkoutButton.visibility = View.GONE
            totalText.visibility = View.GONE
            emptyText.visibility = View.VISIBLE
        } else {
            recycler.visibility = View.VISIBLE
            checkoutButton.visibility = View.VISIBLE
            totalText.visibility = View.VISIBLE
            emptyText.visibility = View.GONE
        }
    }
}