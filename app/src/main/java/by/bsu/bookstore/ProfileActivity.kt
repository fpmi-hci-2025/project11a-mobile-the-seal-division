package by.bsu.bookstore

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ProfileActivity : AppCompatActivity() {

    private lateinit var nameView: TextView
    private lateinit var emailView: TextView
    private lateinit var ordersRecycler: RecyclerView

    private val fakeUser = User(
        userId = 1,
        firstName = "Иван",
        lastName = "Иванов",
        email = "ivan@example.com"
    )

    private val sampleOrders = listOf(
        Order(101, fakeUser, CartManager.getItems(), 950.0, "доставлен", "ул. Ленина, 5"),
        Order(102, fakeUser, CartManager.getItems(), 500.0, "в обработке", "ул. Толстого, 10")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        nameView = findViewById(R.id.profileName)
        emailView = findViewById(R.id.profileEmail)
        ordersRecycler = findViewById(R.id.profileOrdersRecycler)

        nameView.text = "${fakeUser.firstName} ${fakeUser.lastName}"
        emailView.text = fakeUser.email

        ordersRecycler.layoutManager = LinearLayoutManager(this)
        ordersRecycler.adapter = OrderAdapter(sampleOrders) { order, status ->
            android.widget.Toast.makeText(this, "Статус изменён на $status", android.widget.Toast.LENGTH_SHORT).show()
        }
    }
}
