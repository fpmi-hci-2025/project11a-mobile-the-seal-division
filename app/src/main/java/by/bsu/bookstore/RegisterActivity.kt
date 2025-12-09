package by.bsu.bookstore

import android.content.Intent
import android.os.Bundle
import by.bsu.bookstore.auth.AuthManager
import by.bsu.bookstore.model.User
import by.bsu.bookstore.repositories.UserRepository
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import java.util.Date

class RegisterActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inflateContent(R.layout.activity_register)
        val inputName = findViewById<TextInputEditText>(R.id.usernameEditText)
        val inputEmail = findViewById<TextInputEditText>(R.id.emailEditText)
        val inputPhone = findViewById<TextInputEditText>(R.id.phoneEditText)
        val inputPass = findViewById<TextInputEditText>(R.id.passwordEditText)
        val inputPass2 = findViewById<TextInputEditText>(R.id.confirmPasswordEditText)
        val btnRegister = findViewById<MaterialButton>(R.id.registerButton)

        btnRegister.setOnClickListener {
            if (inputName.text?.isBlank() == true || inputEmail.text?.isBlank() == true || inputPass.text?.isBlank() == true) {
                android.widget.Toast.makeText(this, "Заполните обязательные поля", android.widget.Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (inputPass.text.toString() != inputPass2.text.toString()) {
                android.widget.Toast.makeText(this, "Пароли не совпадают", android.widget.Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (inputEmail.text.toString() !in UserRepository.getAllEmails()){
                val email = inputEmail.text.toString()
                val password = inputPass.text.toString()
                val phone = inputPhone.text.toString()
                UserRepository.createUser(User(UserRepository.getNewId(), firstName=inputName.text.toString(), lastName="", phone=phone, email=email, password=password, regDate = Date()))
                AuthManager.login(this, email)
                android.widget.Toast.makeText(this, "Регистрация успешна! Вход выполнен как $email", android.widget.Toast.LENGTH_SHORT).show()
                // TODO: при подключении API сохранить токен
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
            else{
                android.widget.Toast.makeText(this, "Пользователь с такой почтой уже существует!", android.widget.Toast.LENGTH_SHORT).show()
            }
        }
    }
}
