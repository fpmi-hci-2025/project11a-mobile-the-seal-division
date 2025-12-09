package by.bsu.bookstore

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import by.bsu.bookstore.auth.AuthManager
import by.bsu.bookstore.managers.CartManager
import by.bsu.bookstore.managers.FavoritesManager
import by.bsu.bookstore.managers.NotificationsManager
import by.bsu.bookstore.managers.ReviewManager
import by.bsu.bookstore.managers.SubscriptionManager
import by.bsu.bookstore.model.Book
import by.bsu.bookstore.repositories.PublishersRepository
import by.bsu.bookstore.repositories.UserRepository
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
    private lateinit var availableDateText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inflateContent(R.layout.activity_book_details)

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
        availableDateText = findViewById(R.id.detailsAvailableDate)

        toCartButton.setOnClickListener { handleCartAction() }
        favoriteButton.setOnClickListener { handleFavoriteAction() }
        subscribeButton.setOnClickListener { handleSubscribeAction() }
        addReviewButton.setOnClickListener { handleAddReview() }

        fillBookData()
        refreshUI()
    }

    override fun onResume() {
        super.onResume()
        refreshUI()
    }

    private fun fillBookData() {
        val b = book ?: return

        if (!b.coverUrl.isNullOrEmpty()) {
            Glide.with(cover.context)
                .load(b.coverUrl)
                .placeholder(R.drawable.loading)
                .error(R.drawable.error_loading)
                .into(cover)
        } else {
            cover.setImageResource(b.defaultCover ?: R.drawable.book_cover)
        }
        titleView.text = b.title
        authorsView.text = b.author
        publisherView.text = PublishersRepository.findById(b.publisherId)?.name ?: ""
        priceView.text = String.format("%.2f BYN", b.price)
        descriptionView.text = b.description
        ratingView.rating = b.rating
    }

    private fun handleCartAction() {
        val b = book ?: return
        val inCart = CartManager.getItems().any { it.book.id == b.id }

        if (inCart) {
            startActivity(Intent(this, CartActivity::class.java))
        } else {
            CartManager.addToCart(this, b)

            val isPreorder = b.preorder && b.availabilityDate != null && b.availabilityDate.after(Date())
            val message = if (isPreorder) {
                "Книга добавлена в предзаказы"
            } else {
                "Книга добавлена в корзину"
            }
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

            refreshCartButton()
        }
    }

    private fun refreshCartButton() {
        val b = book ?: return
        val inCart = CartManager.getItems().any { it.book.id == b.id }
        val isPreorder = b.preorder && b.availabilityDate != null && b.availabilityDate.after(Date())

        if (inCart) {
            toCartButton.text = "В корзине"
            toCartButton.setBackgroundColor(resources.getColor(R.color.secondary_color))
        } else {
            if (isPreorder) {
                toCartButton.text = "Предзаказать"
                val formattedDate = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(b.availabilityDate!!)
                availableDateText.text = "Будет доступна: $formattedDate"
                availableDateText.visibility = View.VISIBLE
            } else {
                toCartButton.text = "В корзину"
                availableDateText.visibility = View.GONE
            }
            toCartButton.setBackgroundColor(resources.getColor(R.color.primary_color))
        }
    }


    private fun refreshUI() {
        refreshCartButton()
        refreshFavoriteButton()
        refreshSubscribeButton()
        showReviews()
    }

    private fun handleFavoriteAction() {
        val b = book ?: return
        FavoritesManager.toggleFavorite(this, b)
        if (FavoritesManager.isFavorite(b)){
            favoriteButton.setIconResource(R.drawable.ic_bookmark_filled)
        }
        else{
            favoriteButton.setIconResource(R.drawable.ic_bookmark)

        }
        Toast.makeText(this, if (FavoritesManager.isFavorite(b)) "Добавлено в избранное" else "Удалено из избранного", Toast.LENGTH_SHORT).show()
        refreshFavoriteButton()
    }

    private fun handleSubscribeAction() {
        val b = book ?: return
        val publisher = b.publisherId
        val publisherName = PublishersRepository.findById(publisher)?.name ?: ""
        if (!AuthManager.isLogged()) {
            startActivity(Intent(this, LoginActivity::class.java))
            return
        }
        if (SubscriptionManager.isSubscribed(publisher)) {
            SubscriptionManager.unsubscribe(PublishersRepository.findById(publisher)!!)
            subscribeButton.text = "Подписаться на издательство"
            Toast.makeText(this, "Отписаны от $publisherName", Toast.LENGTH_SHORT).show()
        } else {
            SubscriptionManager.subscribe(PublishersRepository.findById(publisher)!!)
            subscribeButton.text = "Отписаны"
            Toast.makeText(this, "Подписаны на $publisherName", Toast.LENGTH_SHORT).show()
            NotificationsManager.addNotification("Подписка", "Вы подписались на новости издательства $publisherName")
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
                    AuthManager.currentUserEmail()?.let { email ->
                        UserRepository.getUserByEmail(email)?.id?.let { userId ->
                            ReviewManager.addReview(this, book!!.id, text, rating, userId)
                            showReviews()
                        }
                    }
                }
                d.dismiss()
            }
            .setNegativeButton("Отмена", null)
        dlg.show()
    }

    private fun showReviews() {
        reviewsContainer.removeAllViews()
        val b = book ?: return
        val list = ReviewManager.getForBook(b.id)
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
            author.text = UserRepository.getUserById(r.userId)?.email ?: "Аноним"
            rating.rating = r.rating.toFloat()
            text.text = r.comment
            val curr = AuthManager.currentUserEmail()
            if (curr != null && curr == UserRepository.getUserById(r.userId)?.email) {
                del.visibility = Button.VISIBLE
                del.setOnClickListener {
                    val removed = ReviewManager.removeReview(this, r.id, r.userId)
                    if (removed) showReviews()
                }
            } else {
                del.visibility = Button.GONE
            }
            reviewsContainer.addView(block)
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
        val publisher = b.publisherId
        if (SubscriptionManager.isSubscribed(publisher)) {
            subscribeButton.text = "Отписаться"
        } else {
            subscribeButton.text = "Подписаться на издательство"
        }
    }
}