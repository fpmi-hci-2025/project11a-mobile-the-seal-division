package by.bsu.bookstore

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class AllBooksActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_books)

        val section = intent.getStringExtra("section") ?: "Все книги"
        title = section

        val booksRv = findViewById<RecyclerView>(R.id.allBooksRecyclerView)
        val books = getSampleBooksLarge()
        val adapter = BooksCarouselAdapter(books) { book ->
            startActivity(android.content.Intent(this, BookDetailsActivity::class.java).apply {
                putExtra("book", book)
            })
        }
        booksRv.layoutManager = LinearLayoutManager(this)
        booksRv.adapter = adapter
    }

    private fun getSampleBooksLarge(): List<Book> {
        return listOf(
            Book(1, "1984", listOf("Джордж Оруэлл"), "Издательство А", 350.0, "Антиутопия", 4.5f, 10),
            Book(2, "Мастер и Маргарита", listOf("Михаил Булгаков"), "Издательство B", 500.0, "Роман", 4.8f, 5),
            Book(3, "Преступление и наказание", listOf("Фёдор Достоевский"), "Издательство C", 450.0, "Классика", 4.6f, 2),
            Book(4, "Война и мир", listOf("Лев Толстой"), "Издательство D", 900.0, "Эпос", 4.7f, 1)
        )
    }
}
