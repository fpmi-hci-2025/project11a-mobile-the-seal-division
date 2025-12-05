// File: app/src/main/java/by/bsu/bookstore/CheckoutActivity.kt
package by.bsu.bookstore

import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
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
            // Простая валидация
            if (nameInput.text.isNullOrBlank() || cardInput.text.isNullOrBlank() || addressInput.text.isNullOrBlank()) {
                android.widget.Toast.makeText(this, "Заполните обязательные поля", android.widget.Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            // Симулируем успешную оплату
            val total = CartManager.getTotal()
            val fakeOrder = Order(
                orderId = (0..999999).random(),
                customer = User(0, "Гость", "", "guest@example.com"),
                items = CartManager.getItems(),
                totalAmount = total,
                status = "оплачен",
                address = addressInput.text.toString()
            )
            CartManager.clear(this)
            android.widget.Toast.makeText(this, "Оплата успешна. Сумма: ${"%.2f".format(total)} BYN", android.widget.Toast.LENGTH_LONG).show()
            finish()
        }
    }
}
