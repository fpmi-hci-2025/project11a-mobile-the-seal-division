package by.bsu.bookstore

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class AllPromotionsActivity : AppCompatActivity() {

    private val promotions = listOf(
        Promotion("Скидка 20% на классику", "Только до конца месяца!"),
        Promotion("Новинки со скидкой 15%", "2025 год начинается с отличных книг!"),
        Promotion("Бесплатная доставка", "При заказе от 2000 ₽")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_promotions)

        val rv = findViewById<RecyclerView>(R.id.allPromotionsRecyclerView)
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = PromotionsAdapter(promotions) {}
    }
}
