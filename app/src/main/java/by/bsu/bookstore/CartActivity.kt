package by.bsu.bookstore

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.bsu.bookstore.adapters.CartItemAdapter
import by.bsu.bookstore.auth.AuthManager
import by.bsu.bookstore.managers.CartManager
import by.bsu.bookstore.model.CartItem

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
            if (!AuthManager.isLogged()) {
                startActivity(Intent(this, LoginActivity::class.java))
            }
            else startActivity(Intent(this, CheckoutActivity::class.java))
        }
    }

    private fun setupAdapter() {
        val items = CartManager.getItems().map { CartItem(it.book, it.quantity) }.toMutableList()

        adapter = CartItemAdapter(
            items = items,
            onIncrease = { cartItem : CartItem ->
                CartManager.addToCart(this, cartItem.book, 1)
                refreshData()
            },
            onDecrease = { cartItem : CartItem ->
                if (cartItem.quantity > 1) {
                    CartManager.removeFromCart(this, cartItem.book.id)
                    CartManager.addToCart(this, cartItem.book, cartItem.quantity - 1)
                } else {
                    CartManager.removeFromCart(this, cartItem.book.id)
                }
                refreshData()
            },
            onDelete = { cartItem : CartItem ->
                CartManager.removeFromCart(this, cartItem.book.id)
                refreshData()
            }
        )

        recycler.adapter = adapter
    }

    private fun refreshData() {
        val freshItems = CartManager.getItems().map { CartItem(it.book, it.quantity) }
        adapter.items.clear()
        adapter.items.addAll(freshItems)
        adapter.notifyDataSetChanged()
        updateUI()
    }

    override fun onResume() {
        super.onResume()
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