package by.bsu.bookstore

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.bsu.bookstore.adapters.BooksCarouselAdapter
import by.bsu.bookstore.api.ApiClient
import by.bsu.bookstore.managers.FavoritesManager
import by.bsu.bookstore.managers.FiltersManager
import by.bsu.bookstore.model.Book
import by.bsu.bookstore.model.SearchFilters
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchActivity : BaseActivity() {

    private lateinit var queryField: EditText
    private lateinit var recycler: RecyclerView
    private lateinit var emptyText: TextView
    private var allBooks: List<Book> = emptyList()
    private val apiService = ApiClient.apiService
    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inflateContent(R.layout.activity_search)
        selectNavItem(R.id.nav_home)

        queryField = findViewById(R.id.searchQueryField)
        recycler = findViewById(R.id.searchRecycler)
        emptyText = findViewById(R.id.searchEmptyText)

        recycler.layoutManager = LinearLayoutManager(this)

        findViewById<View>(R.id.searchButton).setOnClickListener {
            performSearch()
        }
        loadAllBooks()
    }

    private fun showData(show: Boolean) {
        recycler.visibility = if (show) View.VISIBLE else View.INVISIBLE
    }

    private fun loadAllBooks() {
        showData(false)
        showLoading(true)
        coroutineScope.launch {
            try {
                val books = withContext(Dispatchers.IO) { apiService.getAllBooks().execute().body() ?: emptyList() }
                allBooks = books
                performSearch()
            } catch (e: Exception) {
                Toast.makeText(this@SearchActivity, "Ошибка загрузки книг", Toast.LENGTH_LONG).show()
            } finally {
                showLoading(false)
                showData(true)
            }
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
            results = results.filter { book -> currentFilters.selectedGenres.contains(book.categoryName) }
        }
        if (currentFilters.selectedAuthors.isNotEmpty()) {
            results = results.filter { book -> currentFilters.selectedAuthors.contains(book.author) }
        }
        if (currentFilters.selectedPublishers.isNotEmpty()) {
            results = results.filter { book ->
                val publisher = book.publisherObj
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
                    startActivity(Intent(this, BookDetailsActivity::class.java).putExtra("book_id", book.id))
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
    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
    }
}
