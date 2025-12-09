package by.bsu.bookstore

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import by.bsu.bookstore.auth.AuthManager
import by.bsu.bookstore.model.User
import by.bsu.bookstore.repositories.UserRepository
import com.google.android.material.snackbar.Snackbar

class EditProfileActivity : BaseActivity() {

    private lateinit var firstNameEdit: EditText
    private lateinit var lastNameEdit: EditText
    private lateinit var emailEdit: EditText
    private lateinit var saveButton: Button
    private lateinit var backButton: ImageButton
    private var userId : Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inflateContent(R.layout.activity_edit_profile)
        //setupBottomNav(R.id.nav_profile)

        firstNameEdit = findViewById(R.id.editFirstName)
        lastNameEdit = findViewById(R.id.editLastName)
        emailEdit = findViewById(R.id.editEmail)
        saveButton = findViewById(R.id.saveProfileButton)
        backButton = findViewById(R.id.backButton)

        val user = AuthManager.currentUserEmail()?.let { UserRepository.getUserByEmail(it) }
        if (user != null) {
            userId = user.id
            firstNameEdit.setText(user.firstName)
            lastNameEdit.setText(user.lastName)
            emailEdit.setText(user.email)
        }

        saveButton.setOnClickListener {
            val fn = firstNameEdit.text?.toString()?.trim() ?: ""
            val ln = lastNameEdit.text?.toString()?.trim() ?: ""
            val em = emailEdit.text?.toString()?.trim() ?: ""

            if (fn.isEmpty() || ln.isEmpty() || em.isEmpty()) {
                Snackbar.make(saveButton, "Заполните все поля", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (userId != null) {
                UserRepository.updateUser(
                    userId!!, User(
                    email = em,
                    firstName = fn,
                    lastName = ln
                )
                )
            }

//            UserSession.currentUser = User(
//                //username = user?.username ?: "",
//                email = em,
//                //password = user?.password ?: "",
//                firstName = fn,
//                lastName = ln
//            )

            Snackbar.make(saveButton, "Профиль сохранён", Snackbar.LENGTH_SHORT).show()
            finish()
        }

        backButton.setOnClickListener { finish() }
    }
}
