package by.bsu.bookstore

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class FavoritesActivity : AppCompatActivity() {

    private lateinit var recycler: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorites)

        recycler = findViewById(R.id.favoritesRecycler)
        recycler.layoutManager = LinearLayoutManager(this)

        val favBooks = FavoritesManager.getFavorites()

        recycler.adapter = BooksCarouselAdapter(favBooks) { book ->
            startActivity(Intent(this, BookDetailsActivity::class.java).apply {
                putExtra("book", book)
            })
        }
    }
}
