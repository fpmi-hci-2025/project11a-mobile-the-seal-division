package by.bsu.bookstore

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.bsu.bookstore.adapters.AllPromotionsAdapter
import by.bsu.bookstore.repositories.PromotionsRepository

class AllPromotionsActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inflateContent(R.layout.activity_all_promotions)
        //setupBottomNav(R.id.nav_home)

        val rv = findViewById<RecyclerView>(R.id.allPromotionsRecyclerView)
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = AllPromotionsAdapter(PromotionsRepository.findAll()) {}
    }
}
