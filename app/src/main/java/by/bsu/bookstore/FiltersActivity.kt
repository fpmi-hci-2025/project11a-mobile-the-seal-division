package by.bsu.bookstore

import android.app.Activity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.ChipGroup
import com.google.android.material.slider.RangeSlider
import android.widget.RatingBar

class FiltersActivity : AppCompatActivity() {

    private lateinit var genreGroup: ChipGroup
    private lateinit var authorGroup: ChipGroup
    private lateinit var publisherGroup: ChipGroup
    private lateinit var priceSlider: RangeSlider
    private lateinit var ratingFilter: RatingBar

    private lateinit var applyBtn: MaterialButton
    private lateinit var resetBtn: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filters)

        genreGroup = findViewById(R.id.genreChipGroup)
        authorGroup = findViewById(R.id.authorsChipGroup)
        publisherGroup = findViewById(R.id.publishersChipGroup)
        priceSlider = findViewById(R.id.priceRangeSlider)
        ratingFilter = findViewById(R.id.ratingFilter)

        applyBtn = findViewById(R.id.applyFiltersButton)
        resetBtn = findViewById(R.id.resetFiltersButton)

        applyBtn.setOnClickListener {
            setResult(Activity.RESULT_OK)
            finish()
        }

        resetBtn.setOnClickListener {
            genreGroup.clearCheck()
            authorGroup.clearCheck()
            publisherGroup.clearCheck()
            priceSlider.values = listOf(0f, 5000f)
            ratingFilter.rating = 0f
        }
    }
}
