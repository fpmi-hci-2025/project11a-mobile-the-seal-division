// File: app/src/main/java/by/bsu/bookstore/LoginActivity.kt
package by.bsu.bookstore

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import by.bsu.bookstore.auth.AuthManager
import by.bsu.bookstore.repositories.UserRepository
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class LoginActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inflateContent(R.layout.activity_login)
        //setupBottomNav(R.id.nav_profile)
        val inputUser = findViewById<TextInputEditText>(R.id.usernameEditText)
        val inputPass = findViewById<TextInputEditText>(R.id.passwordEditText)
        val btnLogin = findViewById<MaterialButton>(R.id.loginButton)
        val btnRegister = findViewById<TextView>(R.id.registerTextView)

        btnLogin.setOnClickListener {
            val u = inputUser.text.toString()
            val p = inputPass.text.toString()
            if (u.isBlank() || p.isBlank()) {
                android.widget.Toast.makeText(this, "Введите почту и пароль", android.widget.Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (u in UserRepository.getAllEmails()){
                //UserSession.currentUser = UserRepository.getUserByEmail(u)
                AuthManager.login(this, u)
                android.widget.Toast.makeText(this, "Вход выполнен как $u", android.widget.Toast.LENGTH_SHORT).show()
                // TODO: при подключении API сохранить токен
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
            else{
                android.widget.Toast.makeText(this, "Неверный логин: $u", android.widget.Toast.LENGTH_SHORT).show()
            }

        }

        btnRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}
