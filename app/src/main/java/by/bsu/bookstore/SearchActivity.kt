package by.bsu.bookstore

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class SearchActivity : AppCompatActivity() {

    private lateinit var inputField: TextInputEditText
    private lateinit var searchButton: MaterialButton
    private lateinit var recyclerView: RecyclerView

    private val allBooks = listOf(
        Book(1, "1984", listOf("Оруэлл"), "A", 300.0),
        Book(2, "Мастер и Маргарита", listOf("Булгаков"), "B", 500.0),
        Book(3, "Гарри Поттер", listOf("Джоан Роулинг"), "Аст", 450.0)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        inputField = findViewById(R.id.searchEditText)
        searchButton = findViewById(R.id.searchButton)
        recyclerView = findViewById(R.id.searchResultsRecyclerView)

        recyclerView.layoutManager = LinearLayoutManager(this)

        searchButton.setOnClickListener {
            val text = inputField.text.toString().trim()

            val result = allBooks.filter { book ->
                book.title.contains(text, ignoreCase = true) ||
                        book.authors.any { it.contains(text, ignoreCase = true) } ||
                        (book.publisher?.contains(text, ignoreCase = true) ?: false)
            }

            recyclerView.adapter = BooksCarouselAdapter(result) { book ->
                startActivity(android.content.Intent(this, BookDetailsActivity::class.java).apply {
                    putExtra("book", book)
                })
            }
        }
    }
}
