package by.bsu.bookstore

import android.icu.util.Calendar
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import by.bsu.bookstore.api.ApiClient
import by.bsu.bookstore.auth.AuthManager
import by.bsu.bookstore.managers.CartManager
import by.bsu.bookstore.model.Order
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.*
import kotlin.text.get
import kotlin.text.isNullOrBlank
import kotlin.text.replace
import kotlin.text.substring

class CheckoutActivity : BaseActivity() {

    private val apiService = ApiClient.apiService
    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private lateinit var nameInput: TextInputEditText
    private lateinit var cardInput: TextInputEditText
    private lateinit var dateInput: TextInputEditText
    private lateinit var cvvInput: TextInputEditText
    private lateinit var addressInput: TextInputEditText
    private lateinit var payButton: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inflateContent(R.layout.activity_checkout)
        selectNavItem(R.id.nav_cart)
        updateNotificationBadge()

        nameInput = findViewById(R.id.cardNameEditText)
        cardInput = findViewById(R.id.cardNumberEditText)
        dateInput = findViewById(R.id.cardExpiryEditText)
        cvvInput = findViewById(R.id.cardCvvEditText)
        addressInput = findViewById(R.id.deliveryAddressEditText)
        payButton = findViewById(R.id.payButton)

        setupCardNumberFormatting()
        setupCardExpiryFormatting()

        fillAddressFromCurrentUser()

        payButton.setOnClickListener {
            if (validateInputs()) {
                createOrder(addressInput.text.toString())
            }
        }
    }

    private fun fillAddressFromCurrentUser() {
        val currentUser = AuthManager.getCurrentUser()
        if (currentUser?.address?.isNotBlank() == true) {
            addressInput.setText(currentUser.address)
        }
    }

    private fun setupCardNumberFormatting() {
        cardInput.addTextChangedListener(object : TextWatcher {
            private var isUpdating = false
            private val separator = ' '

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (isUpdating) return
                isUpdating = true

                val originalString = s.toString().replace(separator.toString(), "")
                val formattedString = StringBuilder()
                for (i in originalString.indices) {
                    formattedString.append(originalString[i])
                    if ((i + 1) % 4 == 0 && (i + 1) < originalString.length) {
                        formattedString.append(separator)
                    }
                }

                s?.replace(0, s.length, formattedString.toString())
                isUpdating = false
            }
        })
    }

    private fun setupCardExpiryFormatting() {
        dateInput.addTextChangedListener(object : TextWatcher {
            private var isUpdating = false

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (isUpdating) return
                isUpdating = true

                var str = s.toString().replace("/", "")
                if (str.length >= 2) {
                    str = str.substring(0, 2) + "/" + str.substring(2)
                }

                s?.replace(0, s.length, str)
                s?.let { dateInput.setSelection(it.length) }

                isUpdating = false
            }
        })
    }

    private fun validateInputs(): Boolean {
        if (nameInput.text.isNullOrBlank() || cardInput.text.isNullOrBlank() ||
            addressInput.text.isNullOrBlank() || dateInput.text.isNullOrBlank() ||
            cvvInput.text.isNullOrBlank()) {
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show()
            return false
        }

        if (cardInput.text.toString().replace(" ", "").length != 16) {
            Toast.makeText(this, "Номер карты должен содержать 16 цифр", Toast.LENGTH_SHORT).show()
            return false
        }

        val expiryText = dateInput.text.toString()
        if (expiryText.length != 5) {
            Toast.makeText(this, "Неверный формат срока действия (MM/YY)", Toast.LENGTH_SHORT).show()
            return false
        }

        try {
            val month = expiryText.substring(0, 2).toInt()
            val year = expiryText.substring(3, 5).toInt()
            val currentYear = Calendar.getInstance().get(Calendar.YEAR) % 100
            val currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1

            if (month < 1 || month > 12) {
                Toast.makeText(this, "Неверный месяц срока действия", Toast.LENGTH_SHORT).show()
                return false
            }
            if (year < currentYear || (year == currentYear && month < currentMonth)) {
                Toast.makeText(this, "Срок действия карты истек", Toast.LENGTH_SHORT).show()
                return false
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Неверный формат срока действия", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
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