package by.bsu.bookstore

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class AllPromotionsActivity : BaseActivity() {

    private val promotions = listOf(
        Promotion("Скидка 20% на классику", "Только до конца месяца!"),
        Promotion("Новинки со скидкой 15%", "2026 год начинается с отличных книг!"),
        Promotion("Бесплатная доставка", "При заказе от 50 BYN")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inflateContent(R.layout.activity_all_promotions)
        //setupBottomNav(R.id.nav_home)

        val rv = findViewById<RecyclerView>(R.id.allPromotionsRecyclerView)
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = AllPromotionsAdapter(promotions) {}
    }
}
