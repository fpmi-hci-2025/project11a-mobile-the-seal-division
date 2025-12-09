package by.bsu.bookstore

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.bsu.bookstore.adapters.NotificationsAdapter
import by.bsu.bookstore.managers.NotificationsManager

class NotificationsActivity : BaseActivity() {

    private lateinit var recycler: RecyclerView
    private val notifications = NotificationsManager.all()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inflateContent(R.layout.activity_notifications)
        //setupBottomNav(R.id.nav_profile)

        recycler = findViewById(R.id.notificationsRecycler)
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = NotificationsAdapter(notifications) { notif ->
            notif.read = true
        }

        NotificationsManager.clear()
    }
}
