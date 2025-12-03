// File: app/src/main/java/by/bsu/bookstore/LoginActivity.kt
package by.bsu.bookstore

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val inputUser = findViewById<TextInputEditText>(R.id.usernameEditText)
        val inputPass = findViewById<TextInputEditText>(R.id.passwordEditText)
        val btnLogin = findViewById<MaterialButton>(R.id.loginButton)
        val btnRegister = findViewById<TextView>(R.id.registerTextView)

        btnLogin.setOnClickListener {
            val u = inputUser.text.toString()
            val p = inputPass.text.toString()
            if (u.isBlank() || p.isBlank()) {
                android.widget.Toast.makeText(this, "Введите имя пользователя и пароль", android.widget.Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            // Локальная заглушка: считаем что вход успешен
            android.widget.Toast.makeText(this, "Вход выполнен как $u", android.widget.Toast.LENGTH_SHORT).show()
            // TODO: при подключении API сохранить токен
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        btnRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}
