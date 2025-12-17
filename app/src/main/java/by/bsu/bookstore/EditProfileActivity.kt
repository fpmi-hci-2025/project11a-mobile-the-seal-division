package by.bsu.bookstore

import android.os.Bundle
import android.view.View
import android.widget.*
import by.bsu.bookstore.api.ApiClient
import by.bsu.bookstore.auth.AuthManager
import by.bsu.bookstore.model.User
import by.bsu.bookstore.model.UserProfileUpdateDTO
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.*
import kotlin.text.trim

class EditProfileActivity : BaseActivity() {

    private lateinit var firstNameEdit: EditText
    private lateinit var lastNameEdit: EditText
    private lateinit var phoneEdit: EditText
    private lateinit var addressEdit: EditText

    private lateinit var emailText: TextView
    private lateinit var saveButton: Button
    private lateinit var backButton: ImageButton

    private val apiService = ApiClient.apiService
    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inflateContent(R.layout.activity_edit_profile)
        selectNavItem(R.id.nav_profile)

        initViews()

        loadUserData()

        saveButton.setOnClickListener { saveProfile() }
        backButton.setOnClickListener { finish() }
    }

    private fun initViews() {
        firstNameEdit = findViewById(R.id.editFirstName)
        lastNameEdit = findViewById(R.id.editLastName)
        phoneEdit = findViewById(R.id.editPhone)
        addressEdit = findViewById(R.id.editAddress)
        emailText = findViewById(R.id.editEmail)
        saveButton = findViewById(R.id.saveProfileButton)
        backButton = findViewById(R.id.backButton)
    }

    private fun loadUserData() {
        val currentUserName = AuthManager.getCurrentUserName()
        val currentUserLastName = AuthManager.getCurrentUserLastName()
        val currentUserEmail = AuthManager.currentUserEmail()
        val currentUserPhone = AuthManager.getCurrentUserPhone()
        val currentUserAddress = AuthManager.getCurrentUserAddress()


        if (currentUserEmail != null) {
            firstNameEdit.setText(currentUserName)
            lastNameEdit.setText(currentUserLastName)
            phoneEdit.setText(currentUserPhone?.trim() ?: "")
            addressEdit.setText(currentUserAddress?.trim() ?: "")
            emailText.text = currentUserEmail
        } else {
            Toast.makeText(this, "Ошибка: пользователь не найден", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun saveProfile() {
        val fn = firstNameEdit.text?.toString()?.trim() ?: ""
        val ln = lastNameEdit.text?.toString()?.trim() ?: ""
        val phone = phoneEdit.text?.toString()?.trim() ?: ""
        val address = addressEdit.text?.toString()?.trim() ?: ""

        if (fn.isEmpty()) {
            Snackbar.make(saveButton, "Имя не может быть пустым", Snackbar.LENGTH_SHORT).show()
            return
        }

        val userId = AuthManager.getCurrentUserId()
        if (userId == null) {
            Snackbar.make(saveButton, "Ошибка аутентификации", Snackbar.LENGTH_SHORT).show()
            return
        }

        val profileUpdateDto = UserProfileUpdateDTO(
            firstName = fn,
            lastName = ln,
            phone = phone,
            address = address
        )

        showLoading(true)
        coroutineScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    apiService.updateUserProfile(userId, profileUpdateDto).execute()
                }

                if (response.isSuccessful && response.body() != null) {
                    val updatedUser = response.body()!!
                    AuthManager.login(this@EditProfileActivity, updatedUser)
                    Snackbar.make(saveButton, "Профиль сохранён", Snackbar.LENGTH_SHORT).show()
                    finish()
                } else {
                    Snackbar.make(saveButton, "Ошибка сохранения: ${response.message()}", Snackbar.LENGTH_LONG).show()
                }

            } catch (e: Exception) {
                Snackbar.make(saveButton, "Ошибка сети: ${e.message}", Snackbar.LENGTH_LONG).show()
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