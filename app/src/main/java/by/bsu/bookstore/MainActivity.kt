package by.bsu.bookstore

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class MainActivity : AppCompatActivity() {

    private lateinit var promotionsViewPager: ViewPager2
    private lateinit var sectionsRecyclerView: RecyclerView
    private lateinit var bottomNavigation: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        setupPromotions()
        setupSections()
        setupBottomNavigation()
        setupToolbar()
    }

    private fun initViews() {
        promotionsViewPager = findViewById(R.id.promotionsViewPager)
        sectionsRecyclerView = findViewById(R.id.sectionsRecyclerView)
        bottomNavigation = findViewById(R.id.bottomNavigation)
    }

    private fun setupPromotions() {
        val promotions = listOf(
            Promotion("Скидка 20% на классику", "Только до конца месяца!"),
            Promotion("Новинки со скидкой", "Лучшие книги 2025 года"),
            Promotion("Бесплатная доставка", "При покупке от 2000 рублей")
        )

        val adapter = PromotionsAdapter(promotions) { promotion ->
            showPromotionDialog(promotion)
        }
        promotionsViewPager.adapter = adapter
    }

    private fun setupSections() {
        val sections = listOf(
            BookSection("Новинки", getSampleBooks()),
            BookSection("Классика", getSampleBooks()),
            BookSection("Фантастика", getSampleBooks()),
            BookSection("Детективы", getSampleBooks())
        )

        val adapter = BookSectionsAdapter(sections) { section ->
            startActivity(Intent(this, AllBooksActivity::class.java).apply {
                putExtra("section", section.title)
            })
        }

        sectionsRecyclerView.layoutManager = LinearLayoutManager(this)
        sectionsRecyclerView.adapter = adapter
    }

    private fun setupBottomNavigation() {
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true
                R.id.nav_favorites -> {
                    startActivity(Intent(this, FavoritesActivity::class.java))
                    true
                }
                R.id.nav_cart -> {
                    startActivity(Intent(this, CartActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    private fun setupToolbar() {
        findViewById<View>(R.id.searchIcon).setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }

        findViewById<View>(R.id.filterIcon).setOnClickListener {
            startActivity(Intent(this, FiltersActivity::class.java))
        }

        findViewById<View>(R.id.favoritesButton).setOnClickListener {
            startActivity(Intent(this, FavoritesActivity::class.java))
        }

        findViewById<View>(R.id.cartButton).setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }

        findViewById<View>(R.id.allPromotionsButton).setOnClickListener {
            startActivity(Intent(this, AllPromotionsActivity::class.java))
        }
    }

    private fun showPromotionDialog(promotion: Promotion) {
        MaterialAlertDialogBuilder(this)
            .setTitle(promotion.title)
            .setMessage(promotion.description)
            .setPositiveButton("Закрыть", null)
            .show()
    }

    private fun getSampleBooks(): List<Book> {
        return listOf(
            Book("1984", "Джордж Оруэлл", 4.5f),
            Book("Мастер и Маргарита", "Михаил Булгаков", 4.8f),
            Book("Преступление и наказание", "Федор Достоевский", 4.6f),
            Book("Война и мир", "Лев Толстой", 4.7f),
            Book("Гарри Поттер", "Дж. К. Роулинг", 4.9f)
        )
    }
}