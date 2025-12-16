package by.bsu.bookstore

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.bsu.bookstore.adapters.NotificationsAdapter
import by.bsu.bookstore.managers.NotificationsManager

class NotificationsActivity : BaseActivity() {

    private lateinit var recycler: RecyclerView
    private lateinit var clearButton: Button
    private lateinit var emptyText: TextView
    private lateinit var adapter: NotificationsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inflateContent(R.layout.activity_notifications)
        selectNavItem(R.id.nav_profile)

        recycler = findViewById(R.id.notificationsRecycler)
        clearButton = findViewById(R.id.clearNotificationsButton)
        emptyText = findViewById(R.id.emptyNotificationsText)

        setupRecyclerView()
        updateUI()

        clearButton.setOnClickListener {
            NotificationsManager.clearAll()
            updateUI()
        }
    }

    override fun onStop() {
        super.onStop()
        NotificationsManager.markAllRead()
    }

    private fun setupRecyclerView() {
        val notifications = NotificationsManager.all()
        adapter = NotificationsAdapter(notifications.toMutableList()) { notif ->
        }
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter
    }

    private fun updateUI() {
        val currentNotifications = NotificationsManager.all()
        adapter.updateItems(currentNotifications)

        if (currentNotifications.isEmpty()) {
            recycler.visibility = View.GONE
            clearButton.visibility = View.GONE
            emptyText.visibility = View.VISIBLE
        } else {
            recycler.visibility = View.VISIBLE
            clearButton.visibility = View.VISIBLE
            emptyText.visibility = View.GONE
        }
    }
}