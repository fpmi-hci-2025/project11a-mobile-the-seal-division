package by.bsu.bookstore

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.bsu.bookstore.adapters.OrderAdapter
import by.bsu.bookstore.api.ApiClient
import by.bsu.bookstore.auth.AuthManager
import by.bsu.bookstore.managers.NotificationsManager
import by.bsu.bookstore.model.Order
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileActivity : BaseActivity() {

    private lateinit var userName: TextView
    private lateinit var userEmail: TextView
    private lateinit var editProfile: LinearLayout
    private lateinit var notificationsButton: LinearLayout
    private lateinit var notificationsButtonText: TextView

    private lateinit var ordersRecycler: RecyclerView
    private lateinit var emptyOrders: TextView
    private lateinit var logoutButton: View
    private lateinit var loginButton: LinearLayout
    private lateinit var registerButton: LinearLayout
    private val orders: List<Order> = emptyList()
    private val apiService = ApiClient.apiService
    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inflateContent(R.layout.activity_profile)

        userName = findViewById(R.id.profileUserName)
        userEmail = findViewById(R.id.profileUserEmail)
        editProfile = findViewById(R.id.profileEditButton)
        notificationsButton = findViewById(R.id.profileNotificationsButton)
        notificationsButtonText = findViewById(R.id.profileNotificationsButtonText)
        ordersRecycler = findViewById(R.id.profileOrdersRecycler)
        emptyOrders = findViewById(R.id.profileEmptyOrders)
        logoutButton = findViewById(R.id.profileLogoutButton)
        loginButton = findViewById(R.id.profileLoginButton)
        registerButton = findViewById(R.id.profileRegisterButton)
        selectNavItem(R.id.nav_profile)
        ordersRecycler.layoutManager = LinearLayoutManager(this)

        updateUserUI()
        updateNotificationBadge()

        editProfile.setOnClickListener {
            startActivity(Intent(this, EditProfileActivity::class.java))
        }

        notificationsButton.setOnClickListener {
            startActivity(Intent(this, NotificationsActivity::class.java))
        }

        logoutButton.setOnClickListener {
            //UserSession.currentUser = null
            AuthManager.logout()
            updateUserUI()
            finish()
        }

        loginButton.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        registerButton.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        updateUserUI()
    }

    private fun updateUserUI() {
        val user = AuthManager.getCurrentUserId()
        if (user != null && AuthManager.isLogged()) {
            userName.text = "${AuthManager.getCurrentUserName()} ${AuthManager.getCurrentUserLastName()}"
            userEmail.text = AuthManager.currentUserEmail()
            logoutButton.visibility = View.VISIBLE
            loginButton.visibility = View.GONE
            registerButton.visibility = View.GONE
            editProfile.visibility = View.VISIBLE
            if(NotificationsManager.all().isNotEmpty()){
                notificationsButton.visibility = View.VISIBLE
            }
            else{
                notificationsButton.visibility = View.GONE
            }
            notificationsButtonText.text = "Уведомления (${NotificationsManager.getUnreadCount()})"
            AuthManager.getCurrentUserId()?.let { loadUserOrders(it) }
        } else {
            userName.text = "Гость"
            userEmail.text = "Авторизуйтесь"
            logoutButton.visibility = View.GONE
            loginButton.visibility = View.VISIBLE
            registerButton.visibility = View.VISIBLE
            editProfile.visibility = View.GONE
            notificationsButton.visibility = View.GONE
            showOrders(emptyList())
        }
    }

    private fun loadUserOrders(userId: Int) {
        ordersRecycler.visibility = View.INVISIBLE
        showLoading(true)
        coroutineScope.launch {
            try {
                val orders = withContext(Dispatchers.IO) {
                    apiService.getOrdersByUserId(userId).execute().body() ?: emptyList()
                }
                showOrders(orders)
            } catch (e: Exception) {
                Toast.makeText(this@ProfileActivity, "Не удалось загрузить заказы", Toast.LENGTH_SHORT).show()
                showOrders(emptyList())
            } finally {
                showLoading(false)
            }
        }
    }

    private fun showOrders(orders: List<Order>) {
        if (orders.isEmpty()) {
            emptyOrders.visibility = View.VISIBLE
            ordersRecycler.visibility = View.GONE
        } else {
            emptyOrders.visibility = View.GONE
            ordersRecycler.visibility = View.VISIBLE
            ordersRecycler.adapter = OrderAdapter(orders) { order, newStatus ->
                // Здесь можно будет добавить вызов API для обновления статуса заказа
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
    }
}
