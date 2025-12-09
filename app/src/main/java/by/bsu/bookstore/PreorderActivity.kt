package by.bsu.bookstore

import android.os.Bundle
import android.widget.TextView
import by.bsu.bookstore.managers.NotificationsManager
import by.bsu.bookstore.model.Book
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class PreorderActivity : BaseActivity() {

    private lateinit var titleView: TextView
    private lateinit var authorView: TextView
    private lateinit var emailInput: TextInputEditText
    private lateinit var preorderButton: MaterialButton
    private var book: Book? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inflateContent(R.layout.activity_preorder)
        //setupBottomNav(R.id.nav_cart)
        book = intent.getSerializableExtra("book") as? Book

        titleView = findViewById(R.id.bookTitleTextView)
        authorView = findViewById(R.id.bookAuthorTextView)
        emailInput = findViewById(R.id.emailEditText)
        preorderButton = findViewById(R.id.preorderButton)

        val b = book
        if (b != null) {
            titleView.text = b.title
            authorView.text = b.author
        }

        preorderButton.setOnClickListener {
            val email = emailInput.text?.toString()?.trim() ?: ""
            if (!email.contains("@")) {
                emailInput.error = "Введите корректный email"
                return@setOnClickListener
            }
            NotificationsManager.addNotification("Предзаказ", "Вы подписаны на предзаказ книги ${b?.title}")
            finish()
        }
    }
}
