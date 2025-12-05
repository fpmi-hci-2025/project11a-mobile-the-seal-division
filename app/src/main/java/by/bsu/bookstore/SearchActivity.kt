package by.bsu.bookstore

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import by.bsu.bookstore.repositories.BooksRepository

class SearchActivity : BaseActivity() {

    private lateinit var queryField: EditText
    private lateinit var recycler: RecyclerView
    private lateinit var emptyText: TextView
    private val allBooks = BooksRepository.getAllBooks()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inflateContent(R.layout.activity_search)
        //setupBottomNav(R.id.nav_home)

        queryField = findViewById(R.id.searchQueryField)
        recycler = findViewById(R.id.searchRecycler)
        emptyText = findViewById(R.id.searchEmptyText)

        recycler.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)

        findViewById<View>(R.id.searchButton).setOnClickListener {
            performSearch()
        }
    }

    private fun performSearch() {
        val q = queryField.text?.toString()?.trim()?.lowercase() ?: ""

        val results = allBooks.filter {
            it.title.lowercase().contains(q) || it.authors.any { a -> a.lowercase().contains(q) }
        }

        if (results.isEmpty()) {
            emptyText.visibility = View.VISIBLE
            recycler.visibility = View.GONE
        } else {
            emptyText.visibility = View.GONE
            recycler.visibility = View.VISIBLE
            recycler.adapter = BooksCarouselAdapter(
                results,
                onDetailsClick = { book ->
                    startActivity(Intent(this, BookDetailsActivity::class.java).putExtra("book", book))
                },
                onFavoriteClick = { book ->
                    FavoritesManager.toggleFavorite(this, book)
                    (recycler.adapter as? BooksCarouselAdapter)?.notifyDataSetChanged()
                }
            )
        }
    }
}
