// File: app/src/main/java/by/bsu/bookstore/CheckoutActivity.kt
package by.bsu.bookstore

import android.os.Bundle
import by.bsu.bookstore.auth.AuthManager
import by.bsu.bookstore.managers.CartManager
import by.bsu.bookstore.model.Order
import by.bsu.bookstore.model.User
import by.bsu.bookstore.repositories.OrdersRepository
import by.bsu.bookstore.repositories.UserRepository
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class CheckoutActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inflateContent(R.layout.activity_checkout)
        //setupBottomNav(R.id.nav_profile)

        val nameInput = findViewById<TextInputEditText>(R.id.cardNameEditText)
        val cardInput = findViewById<TextInputEditText>(R.id.cardNumberEditText)
        val dateInput = findViewById<TextInputEditText>(R.id.cardExpiryEditText)
        val cvvInput = findViewById<TextInputEditText>(R.id.cardCvvEditText)
        val addressInput = findViewById<TextInputEditText>(R.id.deliveryAddressEditText)
        val payButton = findViewById<MaterialButton>(R.id.payButton)

        payButton.setOnClickListener {
            if (nameInput.text.isNullOrBlank() || cardInput.text.isNullOrBlank() || addressInput.text.isNullOrBlank()) {
                android.widget.Toast.makeText(this, "Заполните обязательные поля", android.widget.Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val total = CartManager.getTotal()
            val order = Order(
                orderId = OrdersRepository.getNewId(),
                customerId = UserRepository.getIdByEmail(AuthManager.currentUserEmail()!!),
                items = CartManager.getItems(),
                totalAmount = total,
                status = "оплачен",
                address = addressInput.text.toString()
            )
            OrdersRepository.createOrder(order)
            CartManager.clear(this)
            android.widget.Toast.makeText(this, "Оплата успешна. Сумма: ${"%.2f".format(total)} BYN", android.widget.Toast.LENGTH_LONG).show()
            finish()
        }
    }
}
