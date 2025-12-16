package by.bsu.bookstore

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import by.bsu.bookstore.api.ApiClient
import by.bsu.bookstore.auth.AuthManager
import by.bsu.bookstore.model.LoginRequest
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.*

class LoginActivity : BaseActivity() {

    private val apiService = ApiClient.apiService
    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inflateContent(R.layout.activity_login)
        selectNavItem(R.id.nav_profile)

        val inputEmail = findViewById<TextInputEditText>(R.id.usernameEditText)
        val inputPass = findViewById<TextInputEditText>(R.id.passwordEditText)
        val btnLogin = findViewById<MaterialButton>(R.id.loginButton)
        val btnRegister = findViewById<TextView>(R.id.registerTextView)

        btnLogin.setOnClickListener {
            val email = inputEmail.text.toString().trim()
            val password = inputPass.text.toString()
            if (email.isBlank() || password.isBlank()) {
                Toast.makeText(this, "Введите почту и пароль", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            performLogin(email, password)
        }

        btnRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun performLogin(email: String, password: String) {
        showLoading(true)
        coroutineScope.launch {
            try {
                val loginRequest = LoginRequest(email, password)
                val response = withContext(Dispatchers.IO) {
                    apiService.login(loginRequest).execute()
                }

                if (response.isSuccessful && response.body() != null) {
                    val user = response.body()!!
                    AuthManager.login(this@LoginActivity, user)
                    Toast.makeText(this@LoginActivity, "Вход выполнен как ${user.email}", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finishAffinity()
                } else {
                    Toast.makeText(this@LoginActivity, "Неверный логин или пароль", Toast.LENGTH_SHORT).show()
                    //$2a$12$ztLBYiBLdGcFRcy31QMTo.efIFptrGt7CiBeULs6XHD1ZM1TihPHe - 123123
                    //$2a$12$aQ2SjVzgQWSw.I/ukJ48k.jmySPhv5l8BLRUa5OmkMqqEYMk4Ltfe - 123456
                    //$2a$12$g7anWMgGq75lH0P/RsVQJutFHs8dVmYYRDiPlEJvhcsSmT/Akw1aW - 111111
                }
            } catch (e: Exception) {
                Toast.makeText(this@LoginActivity, "Ошибка сети: ${e.message}", Toast.LENGTH_LONG).show()
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