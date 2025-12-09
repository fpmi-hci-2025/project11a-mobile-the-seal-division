package by.bsu.bookstore

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.RatingBar
import android.widget.Toast
import by.bsu.bookstore.managers.FiltersManager
import by.bsu.bookstore.model.SearchFilters
import by.bsu.bookstore.repositories.BooksRepository
import by.bsu.bookstore.repositories.PublishersRepository
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.slider.RangeSlider

class FiltersActivity : BaseActivity() {

    private lateinit var genreChips: ChipGroup
    private lateinit var authorChips: ChipGroup
    private lateinit var publisherChips: ChipGroup
    private lateinit var slider: RangeSlider
    private lateinit var rating: RatingBar
    private val sampleBooks = BooksRepository.getAllBooks()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inflateContent(R.layout.activity_filters)

        genreChips = findViewById(R.id.genreChipGroup)
        authorChips = findViewById(R.id.authorsChipGroup)
        publisherChips = findViewById(R.id.publishersChipGroup)
        slider = findViewById(R.id.priceRangeSlider)
        rating = findViewById(R.id.ratingFilter)
        findViewById<View>(R.id.clearFiltersButton).visibility = View.VISIBLE
        findViewById<View>(R.id.clearFiltersButton).setOnClickListener {
            FiltersManager.clearFilters(this)
            Toast.makeText(this, "Фильтры сброшены", Toast.LENGTH_SHORT).show()
            finish()
        }
        fillGenres()
        fillAuthors()
        fillPublishers()

        findViewById<View>(R.id.applyFiltersButton).setOnClickListener {
            applyFiltersAndFinish()
        }
    }

    private fun applyFiltersAndFinish() {
        val selectedGenres = genreChips.checkedChipIds.map { findViewById<Chip>(it).text.toString() }
        val selectedAuthors = authorChips.checkedChipIds.map { findViewById<Chip>(it).text.toString() }
        val selectedPublishers = publisherChips.checkedChipIds.map { findViewById<Chip>(it).text.toString() }

        //val minPrice = slider.values[0]
        val maxPrice = slider.values[0]
        val minRating = rating.rating

        val filters = SearchFilters(
            selectedGenres = selectedGenres,
            selectedAuthors = selectedAuthors,
            selectedPublishers = selectedPublishers,
            minPrice = 0.0f,
            maxPrice = maxPrice,
            minRating = minRating
        )

        FiltersManager.saveFilters(this, filters)
        Toast.makeText(this, "Фильтры применены", Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun fillGenres() {
        val genres = sampleBooks.map { it.category }.distinct()
        genres.forEach { genreName ->
            val chip = Chip(this)
            chip.text = genreName
            chip.isCheckable = true
            genreChips.addView(chip)
        }
    }

    private fun fillAuthors() {
        val authors = sampleBooks.map { it.author }.distinct()
        authors.forEach { authorName ->
            val chip = Chip(this)
            chip.text = authorName
            chip.isCheckable = true
            authorChips.addView(chip)
        }
    }

    private fun fillPublishers() {
        val publisherIds = sampleBooks.map { it.publisherId }.distinct()
        val publishers = publisherIds.mapNotNull { id ->
            PublishersRepository.findById(id)
        }

        publishers.forEach { publisher ->
            val chip = Chip(this)
            chip.text = publisher.name
            chip.isCheckable = true
            publisherChips.addView(chip)
        }
    }
}
