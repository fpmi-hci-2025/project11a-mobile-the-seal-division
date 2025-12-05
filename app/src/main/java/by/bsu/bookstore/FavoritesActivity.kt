package by.bsu.bookstore

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class FavoritesActivity : BaseActivity() {

    private lateinit var recycler: RecyclerView
    private lateinit var emptyText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        inflateContent(R.layout.activity_favorites)
        //setupBottomNav(R.id.nav_favorites)
        selectNavItem(R.id.nav_favorites)

        recycler = findViewById(R.id.favoritesRecycler)
        emptyText = findViewById(R.id.favoritesEmptyText)
        recycler.layoutManager = LinearLayoutManager(this)
        updateUI()
    }

    override fun onResume() {
        super.onResume()
        updateUI()
    }

    private fun updateUI() {
        val favorites = FavoritesManager.getFavorites()
        if (favorites.isEmpty()) {
            emptyText.visibility = View.VISIBLE
            recycler.visibility = View.GONE
        } else {
            emptyText.visibility = View.GONE
            recycler.visibility = View.VISIBLE
            recycler.adapter = BooksCarouselAdapter(
                favorites,
                onDetailsClick = { book ->
                    startActivity(Intent(this, BookDetailsActivity::class.java).putExtra("book", book))
                },
                onFavoriteClick = { book ->
                    FavoritesManager.toggleFavorite(this, book)
                    updateUI()
                }
            )
        }
    }
}
