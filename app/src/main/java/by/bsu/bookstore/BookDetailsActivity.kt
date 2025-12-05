package by.bsu.bookstore

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class BookDetailsActivity : BaseActivity() {

    private var book: Book? = null
    private lateinit var cover: ImageView
    private lateinit var titleView: TextView
    private lateinit var authorsView: TextView
    private lateinit var publisherView: TextView
    private lateinit var priceView: TextView
    private lateinit var descriptionView: TextView
    private lateinit var ratingView: RatingBar
    private lateinit var toCartButton: MaterialButton
    private lateinit var favoriteButton: MaterialButton
    private lateinit var subscribeButton: MaterialButton
    private lateinit var reviewsContainer: LinearLayout
    private lateinit var addReviewButton: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inflateContent(R.layout.activity_book_details)
        //setupBottomNav(R.id.nav_home)

        book = intent.getSerializableExtra("book") as? Book

        cover = findViewById(R.id.detailsCover)
        titleView = findViewById(R.id.detailsTitle)
        authorsView = findViewById(R.id.detailsAuthors)
        publisherView = findViewById(R.id.detailsPublisher)
        priceView = findViewById(R.id.detailsPrice)
        descriptionView = findViewById(R.id.detailsDescription)
        ratingView = findViewById(R.id.detailsRating)
        toCartButton = findViewById(R.id.detailsBuyButton)
        favoriteButton = findViewById(R.id.detailsFavoriteButton)
        subscribeButton = findViewById(R.id.subscribeButton)
        reviewsContainer = findViewById(R.id.reviewsContainer)
        addReviewButton = findViewById(R.id.addReviewButton)

        val b = book
        if (b != null) {
            cover.setImageResource(b.coverResId ?: R.drawable.book_cover)
            titleView.text = b.title
            authorsView.text = b.authors.joinToString(", ")
            publisherView.text = b.publisher ?: ""
            priceView.text = String.format("%.2f BYN", b.price)
            descriptionView.text = b.description ?: ""
            ratingView.rating = b.rating
        }

        toCartButton.setOnClickListener {
            handleCartAction()
        }

        favoriteButton.setOnClickListener {
            handleFavoriteAction()
        }

        subscribeButton.setOnClickListener {
            handleSubscribeAction()
        }

        addReviewButton.setOnClickListener {
            handleAddReview()
        }

        refreshCartButton()
        refreshFavoriteButton()
        refreshSubscribeButton()
        showReviews()
    }

    override fun onResume() {
        super.onResume()
        refreshCartButton()
        refreshFavoriteButton()
    }

    private fun handleCartAction() {
        val b = book ?: return
        val inCart = CartManager.getItems().any { it.book.bookId == b.bookId }
        if (!inCart) {
            CartManager.addToCart(this, b, 1)
            toCartButton.text = "В корзине"
            toCartButton.setBackgroundColor(resources.getColor(R.color.secondary_color))
            //updateNotificationBadge()
        } else {
            startActivity(Intent(this, CartActivity::class.java))
        }
    }

    private fun handleFavoriteAction() {
        val b = book ?: return
        FavoritesManager.toggleFavorite(this, b)
        refreshFavoriteButton()
    }

    private fun handleSubscribeAction() {
        val b = book ?: return
        val publisher = b.publisher ?: return
        if (!AuthManager.isLogged()) {
            startActivity(Intent(this, LoginActivity::class.java))
            return
        }
        if (SubscriptionManager.isSubscribed(publisher)) {
            SubscriptionManager.unsubscribe(publisher)
            subscribeButton.text = "Подписаться на издательство"
            Toast.makeText(this, "Отписаны от $publisher", Toast.LENGTH_SHORT).show()
        } else {
            SubscriptionManager.subscribe(publisher)
            subscribeButton.text = "Отписаны"
            Toast.makeText(this, "Подписаны на $publisher", Toast.LENGTH_SHORT).show()
            NotificationsManager.addNotification("Подписка", "Вы подписались на новости издательства $publisher")
            //updateNotificationBadge()
        }
    }

    private fun handleAddReview() {
        if (!AuthManager.isLogged()) {
            startActivity(Intent(this, LoginActivity::class.java))
            return
        }
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_review, null)
        val inputText = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.inputReviewText)
        val ratingBar = dialogView.findViewById<android.widget.RatingBar>(R.id.inputReviewRating)

        val dlg = androidx.appcompat.app.AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle("Оставить отзыв")
            .setPositiveButton("Добавить") { d, _ ->
                val text = inputText.text?.toString()?.trim() ?: ""
                val rating = ratingBar.rating.toInt()
                if (text.isNotEmpty()) {
                    ReviewManager.addReview(book!!.bookId, text, rating, AuthManager.currentUserEmail())
                    // обновление UI
                }
                d.dismiss()
            }
            .setNegativeButton("Отмена", null)
        dlg.show()
    }

    private fun showReviews() {
        reviewsContainer.removeAllViews()
        val b = book ?: return
        val list = ReviewManager.getForBook(b.bookId)
        if (list.isEmpty()) {
            val tv = TextView(this)
            tv.text = "Пока нет отзывов"
            tv.setPadding(8, 8, 8, 8)
            reviewsContainer.addView(tv)
            return
        }
        for (r in list) {
            val block = LayoutInflater.from(this).inflate(R.layout.item_review, reviewsContainer, false)
            val author = block.findViewById<TextView>(R.id.reviewAuthor)
            val rating = block.findViewById<RatingBar>(R.id.reviewRating)
            val text = block.findViewById<TextView>(R.id.reviewText)
            val del = block.findViewById<Button>(R.id.reviewDeleteButton)
            author.text = r.authorEmail ?: "Аноним"
            rating.rating = r.rating.toFloat()
            text.text = r.text
            val curr = AuthManager.currentUserEmail()
            if (curr != null && curr == r.authorEmail) {
                del.visibility = Button.VISIBLE
                del.setOnClickListener {
                    val removed = ReviewManager.removeReview(r.id, curr)
                    if (removed) showReviews()
                }
            } else {
                del.visibility = Button.GONE
            }
            reviewsContainer.addView(block)
        }
    }

    private fun refreshCartButton() {
        val b = book ?: return
        val inCart = CartManager.getItems().any { it.book.bookId == b.bookId }
        if (inCart) {
            toCartButton.text = "В корзине"
            toCartButton.setBackgroundColor(resources.getColor(R.color.secondary_color))
        } else {
            toCartButton.text = "В корзину"
            toCartButton.setBackgroundColor(resources.getColor(R.color.primary_color))
        }
    }

    private fun refreshFavoriteButton() {
        val b = book ?: return
        if (FavoritesManager.isFavorite(b)) {
            favoriteButton.setBackgroundColor(resources.getColor(R.color.secondary_color))
        } else {
            favoriteButton.setBackgroundColor(resources.getColor(R.color.primary_color))
        }
    }

    private fun refreshSubscribeButton() {
        val b = book ?: return
        val publisher = b.publisher ?: return
        if (SubscriptionManager.isSubscribed(publisher)) {
            subscribeButton.text = "Отписаться"
        } else {
            subscribeButton.text = "Подписаться на издательство"
        }
    }
}
