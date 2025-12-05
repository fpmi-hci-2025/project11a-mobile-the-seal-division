package by.bsu.bookstore

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.bsu.bookstore.repositories.OrdersRepository
import by.bsu.bookstore.repositories.UserRepository
import com.google.android.material.bottomnavigation.BottomNavigationView

class ProfileActivity : BaseActivity() {

    private lateinit var userName: TextView
    private lateinit var userEmail: TextView
    private lateinit var editProfile: LinearLayout
    private lateinit var notificationsButton: LinearLayout
    private lateinit var ordersRecycler: RecyclerView
    private lateinit var emptyOrders: TextView
    private lateinit var logoutButton: View
    private lateinit var loginButton: LinearLayout
    private lateinit var registerButton: LinearLayout

    private val orders = AuthManager.currentUserEmail()
        ?.let { OrdersRepository.getOrdersByEmail(it) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inflateContent(R.layout.activity_profile)
        //setupBottomNav(R.id.nav_profile)

        userName = findViewById(R.id.profileUserName)
        userEmail = findViewById(R.id.profileUserEmail)
        editProfile = findViewById(R.id.profileEditButton)
        notificationsButton = findViewById(R.id.profileNotificationsButton)
        ordersRecycler = findViewById(R.id.profileOrdersRecycler)
        emptyOrders = findViewById(R.id.profileEmptyOrders)
        logoutButton = findViewById(R.id.profileLogoutButton)
        loginButton = findViewById(R.id.profileLoginButton)
        registerButton = findViewById(R.id.profileRegisterButton)
        selectNavItem(R.id.nav_profile)
        ordersRecycler.layoutManager = LinearLayoutManager(this)
        updateUserUI()
        setupOrders()

        editProfile.setOnClickListener {
            startActivity(Intent(this, EditProfileActivity::class.java))
        }

        notificationsButton.setOnClickListener {
            startActivity(Intent(this, NotificationsActivity::class.java))
        }

        logoutButton.setOnClickListener {
            //UserSession.currentUser = null
            AuthManager.logout()
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
        setupOrders()
    }

    private fun updateUserUI() {
        val user = AuthManager.currentUserEmail()?.let { UserRepository.getUserByEmail(it) }
        if (user != null) {
            userName.text = "${user.firstName} ${user.lastName}"
            userEmail.text = user.email
            logoutButton.visibility = View.VISIBLE
            loginButton.visibility = View.GONE
            registerButton.visibility = View.GONE
            editProfile.visibility = View.VISIBLE
            if(NotificationStorage.notifications().isNotEmpty()){
                notificationsButton.visibility = View.VISIBLE
            }
            else{
                notificationsButton.visibility = View.GONE
            }
        } else {
            userName.text = "Гость"
            userEmail.text = "Авторизуйтесь"
            logoutButton.visibility = View.GONE
            loginButton.visibility = View.VISIBLE
            registerButton.visibility = View.VISIBLE
            editProfile.visibility = View.GONE
            notificationsButton.visibility = View.GONE
        }
    }

    private fun setupOrders() {
        if(orders == null){
            emptyOrders.visibility = View.VISIBLE
            ordersRecycler.visibility = View.GONE
        }
        else if (orders!!.isEmpty()) {
            emptyOrders.visibility = View.VISIBLE
            ordersRecycler.visibility = View.GONE
        } else {
            emptyOrders.visibility = View.GONE
            ordersRecycler.visibility = View.VISIBLE
            ordersRecycler.adapter = OrderAdapter(orders) { order, newStatus ->
                order.status = newStatus
            }
        }
    }

//    private fun setupBottomNav() {
//        bottomNav.selectedItemId = R.id.nav_profile
//
//        bottomNav.setOnItemSelectedListener { item ->
//            when (item.itemId) {
//                R.id.nav_home -> {
//                    startActivity(Intent(this, MainActivity::class.java)); true
//                }
//                R.id.nav_favorites -> {
//                    startActivity(Intent(this, FavoritesActivity::class.java)); true
//                }
//                R.id.nav_cart -> {
//                    startActivity(Intent(this, CartActivity::class.java)); true
//                }
//                R.id.nav_profile -> true
//                else -> false
//            }
//        }
//    }
}
