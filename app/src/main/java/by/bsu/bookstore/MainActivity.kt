package by.bsu.bookstore

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import by.bsu.bookstore.adapters.BookSectionsAdapter
import by.bsu.bookstore.adapters.PromotionsAdapter
import by.bsu.bookstore.api.ApiClient
import by.bsu.bookstore.managers.FavoritesManager
import by.bsu.bookstore.model.Book
import by.bsu.bookstore.model.BookSection
import com.airbnb.lottie.LottieAnimationView
import kotlinx.coroutines.*
import retrofit2.HttpException
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : BaseActivity() {

    private lateinit var promotionsViewPager: ViewPager2
    private lateinit var sectionsRecyclerView: androidx.recyclerview.widget.RecyclerView
    private lateinit var progressBar: LottieAnimationView
    private lateinit var loadingOverlay: View

    private var sectionsAdapter: BookSectionsAdapter? = null
    private val apiService = ApiClient.apiService
    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private val loadedSections = mutableListOf<BookSection>()

    companion object {
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inflateContent(R.layout.activity_main)

        progressBar = findViewById(R.id.loadingAnimationBase)
        loadingOverlay = findViewById(R.id.loadingOverlayBase)

        val searchIcon = findViewById<ImageView>(R.id.searchIcon)
        val filterIcon = findViewById<ImageView>(R.id.filterIcon)
        val favoritesButton = findViewById<View>(R.id.favoritesButton)
        val cartButton = findViewById<View>(R.id.cartButton)

        promotionsViewPager = findViewById(R.id.promotionsViewPager)
        sectionsRecyclerView = findViewById(R.id.sectionsRecyclerView)

        searchIcon.setOnClickListener { startActivity(Intent(this, SearchActivity::class.java)) }
        filterIcon.setOnClickListener { startActivity(Intent(this, FiltersActivity::class.java)) }
        favoritesButton.setOnClickListener { startActivity(Intent(this, FavoritesActivity::class.java)) }
        cartButton.setOnClickListener { startActivity(Intent(this, CartActivity::class.java)) }

        findViewById<View>(R.id.allPromotionsButton).setOnClickListener {
            startActivity(Intent(this, AllPromotionsActivity::class.java))
        }

        selectNavItem(R.id.nav_home)
        updateNotificationBadge()

        loadDataFromApi()
    }

    private fun loadDataFromApi() {
        showLoading(true)
        loadedSections.clear()

        coroutineScope.launch {
            try {
                Log.d(TAG, "Начинаем загрузку данных...")

                val deferredBooks = async(Dispatchers.IO) {
                    Log.d(TAG, "Загрузка книг...")
                    val response = apiService.getAllBooks().execute()
                    if (response.isSuccessful) {
                        response.body()?.also {
                            Log.d(TAG, "Загружено ${it.size} книг")
                        } ?: emptyList()
                    } else {
                        Log.e(TAG, "Ошибка загрузки книг: ${response.code()} - ${response.message()}")
                        emptyList()
                    }
                }

                val deferredCategories = async(Dispatchers.IO) {
                    Log.d(TAG, "Загрузка категорий...")
                    val response = apiService.getAllCategories().execute()
                    if (response.isSuccessful) {
                        response.body()?.also {
                            Log.d(TAG, "Загружено ${it.size} категорий")
                        } ?: emptyList()
                    } else {
                        Log.e(TAG, "Ошибка загрузки категорий: ${response.code()} - ${response.message()}")
                        emptyList()
                    }
                }

                val deferredDiscounts = async(Dispatchers.IO) {
                    Log.d(TAG, "Загрузка акций...")
                    val response = apiService.getAllDiscounts().execute()
                    if (response.isSuccessful) {
                        response.body()?.also {
                            Log.d(TAG, "Загружено ${it.size} акций")
                        } ?: emptyList()
                    } else {
                        Log.e(TAG, "Ошибка загрузки акций: ${response.code()} - ${response.message()}")
                        emptyList()
                    }
                }

                val allBooks = deferredBooks.await()
                val allCategories = deferredCategories.await()
                val allDiscounts = deferredDiscounts.await()

                Log.d(TAG, "Обработка данных: ${allBooks.size} книг, ${allCategories.size} категорий")

                if (allBooks.isEmpty()) {
                    withContext(Dispatchers.Main) {
                        progressBar.visibility = View.GONE
                        Toast.makeText(this@MainActivity, "Нет доступных книг", Toast.LENGTH_SHORT).show()
                        setupEmptySections()
                    }
                    return@launch
                }

                // Создаем секции по категориям
                for (category in allCategories) {
                    val booksInCategory = allBooks.filter {
                        it.categoryObj!!.id == category.id || it.categoryName == category.name
                    }
                    if (booksInCategory.isNotEmpty()) {
                        loadedSections.add(BookSection(category.name, booksInCategory))
                    }
                }

                if (loadedSections.size < 2) {
                    val popularBooks = allBooks.sortedByDescending { it.rating }.take(5)
                    if (popularBooks.isNotEmpty()) {
                        loadedSections.add(BookSection("Популярные", popularBooks))
                    }
                }

                withContext(Dispatchers.Main) {
                    Log.d(TAG, "Настройка UI с ${loadedSections.size} секциями")
                    setupSections()
                    promotionsViewPager.adapter = PromotionsAdapter(allDiscounts) { promo ->
                        AlertDialog.Builder(this@MainActivity)
                            .setTitle(promo.title)
                            .setMessage(promo.description)
                            .setPositiveButton("OK", null)
                            .show()
                    }
                    showLoading(false)
                    sectionsAdapter?.notifyDataSetChanged()
                }

            } catch (e: Exception) {
                Log.e(TAG, "Ошибка при загрузке данных", e)
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    handleError(e)
                }
            }
        }
    }
    
    private fun setupEmptySections() {
        val emptySection = BookSection("Нет данных", emptyList())
        loadedSections.add(emptySection)

        sectionsRecyclerView.layoutManager = LinearLayoutManager(this)
        sectionsAdapter = BookSectionsAdapter(
            sections = loadedSections,
            onAllBooksClick = { section ->
                // Можно перезагрузить данные
                loadDataFromApi()
            },
            onDetailsClick = { book ->
                // Ничего не делаем
            },
            onFavoriteClick = { book, sectionPosition, bookPosition ->
                // Ничего не делаем
            }
        )
        sectionsRecyclerView.adapter = sectionsAdapter
    }

    private fun setupSections() {
        sectionsRecyclerView.layoutManager = LinearLayoutManager(this)
        sectionsAdapter = BookSectionsAdapter(
            sections = loadedSections,
            onAllBooksClick = { section ->
                startActivity(Intent(this, AllBooksActivity::class.java).apply {
                    putExtra("section", section.title)
                    // Можно передать фильтр для категории
                    putExtra("categoryFilter", section.title)
                })
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            },
            onDetailsClick = { book ->
                startActivity(Intent(this, BookDetailsActivity::class.java).apply {
                    putExtra("book_id", book.id)
                })
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            },
            onFavoriteClick = { book, sectionPosition, bookPosition ->
                FavoritesManager.toggleFavorite(this, book)
                sectionsAdapter?.notifyItemChanged(sectionPosition, bookPosition)
            }
        )
        sectionsRecyclerView.adapter = sectionsAdapter
    }

    private fun handleError(e: Exception) {
        val errorMessage = when (e) {
            is HttpException -> when (e.code()) {
                404 -> "Сервер не найден"
                500 -> "Ошибка сервера"
                else -> "Ошибка HTTP: ${e.code()}"
            }
            is IOException -> "Проверьте подключение к интернету"
            is TimeoutCancellationException -> "Превышено время ожидания"
            else -> "Ошибка: ${e.localizedMessage ?: "Неизвестная ошибка"}"
        }

        Log.e(TAG, "Ошибка: $errorMessage", e)
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()

        AlertDialog.Builder(this)
            .setTitle("Ошибка загрузки")
            .setMessage("Не удалось загрузить данные. $errorMessage")
            .setPositiveButton("Повторить") { _, _ ->
                loadDataFromApi()
            }
            .setNegativeButton("Продолжить офлайн") { _, _ ->
                setupEmptySections()
            }
            .setNeutralButton("Настройки", null)
            .show()
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
    }

    override fun onResume() {
        super.onResume()
        if (sectionsAdapter?.itemCount == 0) {
            loadDataFromApi()
        } else {
            sectionsAdapter?.notifyDataSetChanged()
        }
    }
}