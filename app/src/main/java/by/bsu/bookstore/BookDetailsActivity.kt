package by.bsu.bookstore

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class BookDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_details)

        val book = intent.getSerializableExtra("book") as? Book ?: return

        findViewById<ImageView>(R.id.detailsCover).setImageResource(book.coverResId ?: R.drawable.book_cover)
        findViewById<TextView>(R.id.detailsTitle).text = book.title
        findViewById<TextView>(R.id.detailsAuthors).text = book.authors.joinToString(", ")
        findViewById<TextView>(R.id.detailsPublisher).text = book.publisher ?: ""
        findViewById<RatingBar>(R.id.detailsRating).rating = book.rating
        findViewById<TextView>(R.id.detailsPrice).text = "${book.price} ₽"
        findViewById<TextView>(R.id.detailsDescription).text = book.description ?: "Описание отсутствует"

        val btnBuy = findViewById<MaterialButton>(R.id.detailsBuyButton)
        val btnFavorite = findViewById<MaterialButton>(R.id.detailsFavoriteButton)
        btnBuy.setOnClickListener {
            CartManager.addToCart(book, 1)
            android.widget.Toast.makeText(this, "Книга добавлена в корзину", android.widget.Toast.LENGTH_SHORT).show()
        }
        btnFavorite.setOnClickListener {
            FavoritesManager.toggleFavorite(this, book)
            android.widget.Toast.makeText(this, if (FavoritesManager.isFavorite(book)) "Добавлено в избранное" else "Удалено из избранного", android.widget.Toast.LENGTH_SHORT).show()
        }

        findViewById<View>(R.id.preorderSection)?.setOnClickListener {
            if (book.preorder) {
                android.widget.Toast.makeText(this, "Вы подписаны на предзаказ", android.widget.Toast.LENGTH_SHORT).show()
            } else {
                android.widget.Toast.makeText(this, "Книга доступна: ${book.inStock} шт.", android.widget.Toast.LENGTH_SHORT).show()
            }
        }
    }
}
