package by.bsu.bookstore

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import by.bsu.bookstore.adapters.BookSectionsAdapter
import by.bsu.bookstore.adapters.PromotionsAdapter
import by.bsu.bookstore.managers.FavoritesManager
import by.bsu.bookstore.model.BookSection
import by.bsu.bookstore.repositories.BooksRepository
import by.bsu.bookstore.repositories.PromotionsRepository

class MainActivity : BaseActivity() {

    private lateinit var promotionsViewPager: ViewPager2
    private lateinit var sectionsRecyclerView: androidx.recyclerview.widget.RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inflateContent(R.layout.activity_main)
        //setupBottomNav(R.id.nav_home)

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
        setupPromotions()
        setupSections()
    }

    override fun onResume() {
        super.onResume()
        if (sectionsRecyclerView.adapter != null) {
            sectionsRecyclerView.adapter!!.notifyDataSetChanged()
        }
    }

    private fun setupPromotions() {

        promotionsViewPager.adapter = PromotionsAdapter(PromotionsRepository.findAll()) { promo ->
            AlertDialog.Builder(this)
                .setTitle(promo.title)
                .setMessage(promo.description)
                .setPositiveButton("OK", null)
                .show()
        }
    }

    private fun setupSections() {
        val sections = listOf(
            BookSection("Новинки", BooksRepository.getBooksByCategory("Новинки")),
            BookSection("Классика", BooksRepository.getBooksByCategory("Классика"))
        )

        sectionsRecyclerView.layoutManager = LinearLayoutManager(this)
        sectionsRecyclerView.adapter = BookSectionsAdapter(
            sections = sections,
            onAllBooksClick = { section ->
                startActivity(Intent(this, AllBooksActivity::class.java).apply {
                    putExtra("section", section.title)
                })
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            },
            onDetailsClick = { book ->
                startActivity(Intent(this, BookDetailsActivity::class.java).apply {
                    putExtra("book", book)
                })
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            },
            onFavoriteClick = { book, sectionPosition, bookPosition ->
                FavoritesManager.toggleFavorite(this, book)
                sectionsRecyclerView.adapter?.notifyItemChanged(sectionPosition)
            }
        )
    }
}
