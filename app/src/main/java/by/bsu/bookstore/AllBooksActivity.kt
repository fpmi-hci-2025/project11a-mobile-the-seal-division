package by.bsu.bookstore

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.bsu.bookstore.adapters.BooksCarouselAdapter
import by.bsu.bookstore.api.ApiClient
import by.bsu.bookstore.managers.FavoritesManager
import by.bsu.bookstore.model.Book
import kotlinx.coroutines.*

class AllBooksActivity : BaseActivity() {

    private lateinit var adapter: BooksCarouselAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyView: TextView

    private var currentSection: String = ""

    private val apiService = ApiClient.apiService
    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inflateContent(R.layout.activity_all_books)
        selectNavItem(R.id.nav_home)

        currentSection = intent.getStringExtra("section") ?: "Все книги"
        title = currentSection

        recyclerView = findViewById(R.id.allBooksRecyclerView)
        emptyView = findViewById(R.id.emptyText)

        recyclerView.layoutManager = LinearLayoutManager(this)

        selectNavItem(R.id.nav_home)
        loadBooks()
        updateNotificationBadge()
    }

    private fun loadBooks() {
        showData(false)
        showLoading(true)

        coroutineScope.launch {
            try {
                val books = withContext(Dispatchers.IO) {
                    if (currentSection != "Все книги" && currentSection.isNotEmpty()) {
                        apiService.getBooksByCategory(currentSection).execute().body()
                    } else {
                        apiService.getAllBooks().execute().body()
                    }
                }
                handleBooksResponse(books ?: emptyList(), null)
            } catch (e: Exception) {
                handleBooksResponse(null, "Ошибка загрузки: ${e.message}")
            } finally {
                showLoading(false)
            }
        }
    }

    private fun handleBooksResponse(books: List<Book>?, error: String?) {
        if (books != null && books.isNotEmpty()) {
            showData(true)
            emptyView.visibility = View.GONE

            adapter = BooksCarouselAdapter(
                items = books,
                onDetailsClick = { book ->
                    startActivity(Intent(this, BookDetailsActivity::class.java).apply {
                        putExtra("book_id", book.id)
                    })
                },
                onFavoriteClick = { book, position ->
                    FavoritesManager.toggleFavorite(this, book)
                    adapter.notifyItemChanged(position)
                }
            )
            recyclerView.adapter = adapter
        } else {
            showData(false)
            emptyView.visibility = View.VISIBLE

            val message = error ?: "Книги в этой секции не найдены"
            emptyView.text = message

            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showData(show: Boolean) {
        recyclerView.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
    }
}