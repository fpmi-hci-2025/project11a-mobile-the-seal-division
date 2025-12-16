package by.bsu.bookstore

import android.os.Bundle
import android.view.View
import android.widget.RatingBar
import android.widget.Toast
import androidx.core.view.children
import by.bsu.bookstore.api.ApiClient
import by.bsu.bookstore.managers.FiltersManager
import by.bsu.bookstore.model.Book
import by.bsu.bookstore.model.SearchFilters
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.slider.RangeSlider
import kotlinx.coroutines.*

class FiltersActivity : BaseActivity() {

    private lateinit var genreChips: ChipGroup
    private lateinit var authorChips: ChipGroup
    private lateinit var publisherChips: ChipGroup
    private lateinit var slider: RangeSlider
    private lateinit var rating: RatingBar
    private lateinit var clearBtn: MaterialButton

    private val apiService = ApiClient.apiService
    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inflateContent(R.layout.activity_filters)
        selectNavItem(R.id.nav_home)

        genreChips = findViewById(R.id.genreChipGroup)
        authorChips = findViewById(R.id.authorsChipGroup)
        publisherChips = findViewById(R.id.publishersChipGroup)
        slider = findViewById(R.id.priceRangeSlider)
        rating = findViewById(R.id.ratingFilter)
        clearBtn = findViewById(R.id.clearFiltersButton)

        loadFilterOptions()

        findViewById<View>(R.id.applyFiltersButton).setOnClickListener { applyFiltersAndFinish() }
        clearBtn.setOnClickListener {
            FiltersManager.clearFilters(this)
            Toast.makeText(this, "Фильтры сброшены", Toast.LENGTH_SHORT).show()
            restoreFilterState(SearchFilters.empty())
            updateClearButtonVisibility()
        }
    }

    private fun loadFilterOptions() {
        showLoading(true)
        coroutineScope.launch {
            try {
                val allBooks = withContext(Dispatchers.IO) {
                    apiService.getAllBooks().execute().body() ?: emptyList()
                }
                fillChipGroups(allBooks)
                restoreFilterState(FiltersManager.getFilters())
                updateClearButtonVisibility()
            } catch (e: Exception) {
                Toast.makeText(this@FiltersActivity, "Не удалось загрузить опции фильтров", Toast.LENGTH_LONG).show()
            } finally {
                showLoading(false)
            }
        }
    }

    private fun fillChipGroups(books: List<Book>) {
        val genres = books.mapNotNull { it.categoryName }.distinct()
        genres.forEach { genreName ->
            val chip = Chip(this).apply { text = genreName; isCheckable = true }
            genreChips.addView(chip)
        }

        val authors = books.map { it.author }.distinct()
        authors.forEach { authorName ->
            val chip = Chip(this).apply { text = authorName; isCheckable = true }
            authorChips.addView(chip)
        }

        val publishers = books.mapNotNull { it.publisherName }.distinct()
        publishers.forEach { publisherName ->
            val chip = Chip(this).apply { text = publisherName; isCheckable = true }
            publisherChips.addView(chip)
        }
    }

    /**
     * Восстанавливает состояние элементов UI на основе сохраненных фильтров.
     */
    private fun restoreFilterState(filters: SearchFilters) {
        genreChips.children.forEach { view ->
            if (view is Chip) {
                view.isChecked = filters.selectedGenres.contains(view.text.toString())
            }
        }
        authorChips.children.forEach { view ->
            if (view is Chip) {
                view.isChecked = filters.selectedAuthors.contains(view.text.toString())
            }
        }
        publisherChips.children.forEach { view ->
            if (view is Chip) {
                view.isChecked = filters.selectedPublishers.contains(view.text.toString())
            }
        }

        if (filters.maxPrice > 0) {
            slider.setValues(filters.maxPrice)
        }
        rating.rating = filters.minRating
    }

    /**
     * Проверяет, есть ли активные фильтры, и обновляет видимость кнопки "Очистить".
     */
    private fun updateClearButtonVisibility() {
        val hasActiveFilters = FiltersManager.hasActiveFilters()
        clearBtn.visibility = if (hasActiveFilters) View.VISIBLE else View.GONE
    }

    private fun applyFiltersAndFinish() {
        val selectedGenres = genreChips.checkedChipIds.map { findViewById<Chip>(it).text.toString() }
        val selectedAuthors = authorChips.checkedChipIds.map { findViewById<Chip>(it).text.toString() }
        val selectedPublishers = publisherChips.checkedChipIds.map { findViewById<Chip>(it).text.toString() }
        val maxPrice = slider.values.firstOrNull() ?: 0f
        val minRating = rating.rating

        val filters = SearchFilters(
            selectedGenres, selectedAuthors, selectedPublishers, 0f, maxPrice, minRating
        )

        FiltersManager.saveFilters(this, filters)
        Toast.makeText(this, "Фильтры применены", Toast.LENGTH_SHORT).show()
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
    }
}