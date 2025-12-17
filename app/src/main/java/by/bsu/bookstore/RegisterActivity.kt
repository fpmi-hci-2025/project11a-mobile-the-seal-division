package by.bsu.bookstore

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import at.favre.lib.crypto.bcrypt.BCrypt
import by.bsu.bookstore.api.ApiClient
import by.bsu.bookstore.auth.AuthManager
import by.bsu.bookstore.model.User
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.*
import java.util.*

class RegisterActivity : BaseActivity() {

    private val apiService = ApiClient.apiService
    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inflateContent(R.layout.activity_register)
        selectNavItem(R.id.nav_profile)
        val inputName = findViewById<TextInputEditText>(R.id.usernameEditText)
        val inputEmail = findViewById<TextInputEditText>(R.id.emailEditText)
        val inputPhone = findViewById<TextInputEditText>(R.id.phoneEditText)
        val inputPass = findViewById<TextInputEditText>(R.id.passwordEditText)
        val inputPass2 = findViewById<TextInputEditText>(R.id.confirmPasswordEditText)
        val btnRegister = findViewById<MaterialButton>(R.id.registerButton)

        btnRegister.setOnClickListener {
            if (inputName.text?.isBlank() == true || inputEmail.text?.isBlank() == true || inputPass.text?.isBlank() == true) {
                android.widget.Toast.makeText(this, "Заполните обязательные поля", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (inputPass.text.toString() != inputPass2.text.toString()) {
                android.widget.Toast.makeText(this, "Пароли не совпадают", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val hashedPassword = BCrypt.withDefaults().hashToString(12, inputPass.text.toString().toCharArray())

            val newUser = User(
                firstName = inputName.text.toString().trim(),
                lastName = "",
                email = inputEmail.text.toString().trim(),
                phone = inputPhone.text.toString().trim(),
                password = hashedPassword,
            )
            performRegistration(newUser)
        }
    }

    private fun performRegistration(user: User) {
        showLoading(true)
        coroutineScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    apiService.register(user).execute()
                }
                if (response.isSuccessful && response.body() != null) {
                    val registeredUser = response.body()!!
                    AuthManager.login(this@RegisterActivity, registeredUser)
                    Toast.makeText(this@RegisterActivity, "Регистрация успешна!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@RegisterActivity, MainActivity::class.java))
                    finishAffinity()
                } else {
                    Log.d("REGISTER_ACTIVITY", "Ошибка регистрации: ${response.message()}")
                    Toast.makeText(this@RegisterActivity, "Ошибка регистрации: ${response.message()}", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@RegisterActivity, "Ошибка сети: ${e.message}", Toast.LENGTH_LONG).show()
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
