package by.bsu.bookstore

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class PreorderActivity : AppCompatActivity() {

    private lateinit var titleView: TextView
    private lateinit var authorView: TextView
    private lateinit var emailInput: TextInputEditText
    private lateinit var preorderButton: MaterialButton

    private var currentBook: Book? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preorder)

        currentBook = intent.getSerializableExtra("book") as? Book

        initViews()
        fillBookInfo()

        preorderButton.setOnClickListener {
            val email = emailInput.text?.toString()?.trim()

            if (email.isNullOrEmpty() || !email.contains("@")) {
                emailInput.error = "Введите корректный email"
                return@setOnClickListener
            }

            android.widget.Toast.makeText(
                this,
                "Вы подписаны на уведомление о выходе книги!",
                android.widget.Toast.LENGTH_LONG
            ).show()

            finish()
        }
    }

    private fun initViews() {
        titleView = findViewById(R.id.bookTitleTextView)
        authorView = findViewById(R.id.bookAuthorTextView)
        emailInput = findViewById(R.id.emailEditText)
        preorderButton = findViewById(R.id.preorderButton)
    }

    private fun fillBookInfo() {
        currentBook?.let { book ->
            titleView.text = book.title
            authorView.text = book.authors.joinToString(", ")
        }
    }
}
