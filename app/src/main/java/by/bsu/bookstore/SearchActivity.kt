package by.bsu.bookstore

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.bsu.bookstore.adapters.BooksCarouselAdapter
import by.bsu.bookstore.managers.FavoritesManager
import by.bsu.bookstore.managers.FiltersManager
import by.bsu.bookstore.model.Book
import by.bsu.bookstore.model.SearchFilters
import by.bsu.bookstore.repositories.BooksRepository
import by.bsu.bookstore.repositories.PublishersRepository

class SearchActivity : BaseActivity() {

    private lateinit var queryField: EditText
    private lateinit var recycler: RecyclerView
    private lateinit var emptyText: TextView
    private val allBooks = BooksRepository.getAllBooks()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inflateContent(R.layout.activity_search)

        queryField = findViewById(R.id.searchQueryField)
        recycler = findViewById(R.id.searchRecycler)
        emptyText = findViewById(R.id.searchEmptyText)

        recycler.layoutManager = LinearLayoutManager(this)

        findViewById<View>(R.id.searchButton).setOnClickListener {
            performSearch()
        }
    }

    override fun onResume() {
        super.onResume()
        performSearch()
    }

    private fun performSearch() {
        val q = queryField.text?.toString()?.trim()?.lowercase() ?: ""

        var results: List<Book> = allBooks
        val currentFilters = FiltersManager.getFilters()

        if (q.isNotEmpty()) {
            results = results.filter {
                it.title.lowercase().contains(q) || it.author.lowercase().contains(q)
            }
        }

        if (currentFilters.selectedGenres.isNotEmpty()) {
            results = results.filter { book -> currentFilters.selectedGenres.contains(book.category) }
        }
        if (currentFilters.selectedAuthors.isNotEmpty()) {
            results = results.filter { book -> currentFilters.selectedAuthors.contains(book.author) }
        }
        if (currentFilters.selectedPublishers.isNotEmpty()) {
            results = results.filter { book ->
                val publisher = PublishersRepository.findById(book.publisherId)
                publisher != null && currentFilters.selectedPublishers.contains(publisher.name)
            }
        }
        if (currentFilters.minRating > 0) {
            results = results.filter { it.rating >= currentFilters.minRating }
        }
        if (currentFilters.maxPrice > 0) {
            results =
                results.filter { it.price >= currentFilters.minPrice && it.price <= currentFilters.maxPrice }
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
                onFavoriteClick = { book, _ ->
                    FavoritesManager.toggleFavorite(this, book)
                    val position = (recycler.adapter as BooksCarouselAdapter).items.indexOf(book)
                    if (position != -1) {
                        recycler.adapter?.notifyItemChanged(position)
                    }
                }
            )
        }
    }
}
