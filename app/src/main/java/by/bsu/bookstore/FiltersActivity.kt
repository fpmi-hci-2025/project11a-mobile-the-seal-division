package by.bsu.bookstore

import android.os.Bundle
import android.view.View
import android.widget.RatingBar
import androidx.appcompat.app.AppCompatActivity
import by.bsu.bookstore.repositories.BooksRepository
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
        //setupBottomNav(R.id.nav_home)

        genreChips = findViewById(R.id.genreChipGroup)
        authorChips = findViewById(R.id.authorsChipGroup)
        publisherChips = findViewById(R.id.publishersChipGroup)
        slider = findViewById(R.id.priceRangeSlider)
        rating = findViewById(R.id.ratingFilter)

        fillAuthors()
        fillPublishers()

        findViewById<View>(R.id.applyFiltersButton).setOnClickListener {
            finish()
        }
    }

    private fun fillAuthors() {
        val authors = sampleBooks.flatMap { it.authors }.distinct()
        authors.forEach {
            val chip = Chip(this)
            chip.text = it
            chip.isCheckable = true
            authorChips.addView(chip)
        }
    }

    private fun fillPublishers() {
        val publishers = sampleBooks.map { it.publisher }.distinct()
        publishers.forEach {
            val chip = Chip(this)
            chip.text = it
            chip.isCheckable = true
            publisherChips.addView(chip)
        }
    }
}
