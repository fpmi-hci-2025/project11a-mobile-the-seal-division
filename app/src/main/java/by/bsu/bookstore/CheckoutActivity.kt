package by.bsu.bookstore

import android.os.Bundle
import android.widget.Toast
import by.bsu.bookstore.api.ApiClient
import by.bsu.bookstore.auth.AuthManager
import by.bsu.bookstore.managers.CartManager
import by.bsu.bookstore.model.Order
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.*

class CheckoutActivity : BaseActivity() {

    private val apiService = ApiClient.apiService
    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inflateContent(R.layout.activity_checkout)
        selectNavItem(R.id.nav_cart)
        updateNotificationBadge()

        val nameInput = findViewById<TextInputEditText>(R.id.cardNameEditText)
        val cardInput = findViewById<TextInputEditText>(R.id.cardNumberEditText)
        val dateInput = findViewById<TextInputEditText>(R.id.cardExpiryEditText)
        val cvvInput = findViewById<TextInputEditText>(R.id.cardCvvEditText)
        val addressInput = findViewById<TextInputEditText>(R.id.deliveryAddressEditText)
        val payButton = findViewById<MaterialButton>(R.id.payButton)

        payButton.setOnClickListener {
            if (nameInput.text.isNullOrBlank() || cardInput.text.isNullOrBlank() || addressInput.text.isNullOrBlank()) {
                Toast.makeText(this, "Заполните обязательные поля", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            createOrder(addressInput.text.toString())
        }
    }

    private fun createOrder(address: String) {
        val userId = AuthManager.getCurrentUserId()
        if (userId == null || userId == -1) {
            Toast.makeText(this, "Ошибка аутентификации", Toast.LENGTH_SHORT).show()
            return
        }

        val order = Order(
            userId = userId,
            itemsString = CartManager.getItemsToString(),
            totalAmount = CartManager.getTotal(),
            status = "новый",
            address = address
        )

        showLoading(true)
        coroutineScope.launch {
            try {
                val createdOrder = withContext(Dispatchers.IO) {
                    apiService.createOrder(order).execute().body()
                }
                if (createdOrder != null) {
                    CartManager.clear(this@CheckoutActivity)
                    Toast.makeText(this@CheckoutActivity, "Заказ №${createdOrder.id} успешно создан!", Toast.LENGTH_LONG).show()
                    finish()
                } else {
                    Toast.makeText(this@CheckoutActivity, "Не удалось создать заказ", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@CheckoutActivity, "Ошибка сети: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                showLoading(false)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
    }
}