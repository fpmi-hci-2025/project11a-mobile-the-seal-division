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
import androidx.activity.result.launch
import by.bsu.bookstore.api.ApiClient.apiService
import by.bsu.bookstore.auth.AuthManager
import by.bsu.bookstore.managers.CartManager
import by.bsu.bookstore.managers.FavoritesManager
import by.bsu.bookstore.managers.NotificationsManager
import by.bsu.bookstore.managers.SubscriptionManager
import by.bsu.bookstore.model.Book
import by.bsu.bookstore.model.Review
import by.bsu.bookstore.repositories.PublishersRepository
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.UUID

class BookDetailsActivity : BaseActivity() {

    private var book: Book? = null
    private var bookId: Int = -1

    private lateinit var contentContainer: LinearLayout
    private lateinit var cover: ImageView
    private lateinit var titleView: TextView
    private lateinit var authorsView: TextView
    private lateinit var publisherView: TextView
    private lateinit var priceView: TextView
    private lateinit var oldPriceView: TextView
    private lateinit var descriptionView: TextView
    private lateinit var ratingView: RatingBar
    private lateinit var toCartButton: MaterialButton
    private lateinit var favoriteButton: MaterialButton
    private lateinit var subscribeButton: MaterialButton
    private lateinit var reviewsContainer: LinearLayout
    private lateinit var addReviewButton: MaterialButton
    private lateinit var availableDateText: TextView
    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inflateContent(R.layout.activity_book_details)

        bookId = intent.getIntExtra("book_id", -1)
        if (bookId == -1) {
            Toast.makeText(this, "Ошибка: ID книги не найден", Toast.LENGTH_LONG).show()
            finish()
            return
        }
        contentContainer = findViewById(R.id.detailsContentContainer)
        cover = findViewById(R.id.detailsCover)
        titleView = findViewById(R.id.detailsTitle)
        authorsView = findViewById(R.id.detailsAuthors)
        publisherView = findViewById(R.id.detailsPublisher)
        priceView = findViewById(R.id.detailsPrice)
        oldPriceView = findViewById(R.id.detailsOldPrice)
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

        selectNavItem(R.id.nav_home)
        loadBookDetails()
        updateNotificationBadge()

        //fillBookData()
        //refreshUI()
    }

    private fun loadBookDetails() {
        showData(false) // Скрываем контент
        showLoading(true) // Показываем загрузку

        coroutineScope.launch {
            try {
                // Параллельно загружаем книгу и ее отзывы
                val bookDeferred = async(Dispatchers.IO) { apiService.getBook(bookId).execute().body() }
                val reviewsDeferred = async(Dispatchers.IO) { apiService.getReviewsByBookId(bookId).execute().body() }

                val loadedBook = bookDeferred.await()
                val loadedReviews = reviewsDeferred.await()

                if (loadedBook != null) {
                    book = loadedBook
                    fillBookData()
                    showReviews(loadedReviews ?: emptyList())
                    refreshUI()
                    showData(true)
                } else {
                    Toast.makeText(this@BookDetailsActivity, "Не удалось загрузить информацию о книге", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@BookDetailsActivity, "Ошибка сети: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                showLoading(false)
            }
        }
    }

    private fun showData(show: Boolean) {
        contentContainer.visibility = if (show) View.VISIBLE else View.INVISIBLE
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
            cover.setImageResource(R.drawable.book_cover)
        }
        titleView.text = b.title
        authorsView.text = b.author
        publisherView.text = b.publisherName
        setPriceWithDiscount(b, priceView, oldPriceView)
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
            val message = "Книга добавлена в корзину"

            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

            refreshCartButton()
        }
    }
    private fun parseDate(dateString: String): Date {
        val formats = listOf(
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            "yyyy-MM-dd'T'HH:mm:ss'Z'",
            "yyyy-MM-dd HH:mm:ss",
            "yyyy-MM-dd",
            "dd.MM.yyyy"
        )

        for (format in formats) {
            try {
                val sdf = SimpleDateFormat(format, Locale.getDefault())
                sdf.timeZone = TimeZone.getTimeZone("UTC")
                return sdf.parse(dateString) ?: Date()
            } catch (e: Exception) {
            }
        }

        return Date()
    }

    private fun refreshCartButton() {
        val b = book ?: return
        val inCart = CartManager.getItems().any { it.book.id == b.id }
        val isPreorder = b.preorder && b.availableDate != null && parseDate(b.availableDate).after(Date())

        if (inCart) {
            toCartButton.text = "В корзине"
            toCartButton.setBackgroundColor(resources.getColor(R.color.secondary_color))
        } else {
            if (isPreorder) {
                toCartButton.text = "Предзаказать"
                val formattedDate = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(parseDate(b.availableDate!!))
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
        val publisher = b.publisherObj
        val publisherId = b.publisherId
        val publisherName = b.publisherName
        if (!AuthManager.isLogged()) {
            startActivity(Intent(this, LoginActivity::class.java))
            return
        }
        if (SubscriptionManager.isSubscribed(publisherId)) {
            SubscriptionManager.unsubscribe(publisher!!)
            subscribeButton.text = "Подписаться на издательство"
            Toast.makeText(this, "Отписаны от $publisherName", Toast.LENGTH_SHORT).show()
        } else {
            SubscriptionManager.subscribe(publisher!!)
            subscribeButton.text = "Отписаться"
            Toast.makeText(this, "Подписаны на $publisherName", Toast.LENGTH_SHORT).show()
            NotificationsManager.addNotification("Подписка", "Вы подписались на новости издательства $publisherName")
            updateNotificationBadge()
        }
    }

    private fun handleAddReview() {
        if (!AuthManager.isLogged()) {
            startActivity(Intent(this, LoginActivity::class.java))
            return
        }
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_review, null)
        val inputText = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.inputReviewText)
        val ratingBar = dialogView.findViewById<RatingBar>(R.id.inputReviewRating)

        val dlg = androidx.appcompat.app.AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle("Оставить отзыв")
            .setPositiveButton("Добавить") { d, _ ->
                val text = inputText.text?.toString()?.trim() ?: ""
                val rating = ratingBar.rating.toInt().toString()
                if (text.isNotEmpty()) {
                    AuthManager.getCurrentUserId()?.let { userId ->
                            coroutineScope.launch {
                                showLoading(true)
                                try {
                                    val newReview = Review(bookId = book!!.id, comment = text, rating = rating, userId = userId)
                                    val addedReview = withContext(Dispatchers.IO) {
                                        apiService.addReview(book!!.id, newReview).execute().body()
                                    }
                                    if (addedReview != null) {
                                        val updatedReviews = withContext(Dispatchers.IO) { apiService.getReviewsByBookId(bookId).execute().body() }
                                        showReviews(updatedReviews ?: emptyList())
                                    }
                                    else{
                                        Toast.makeText(this@BookDetailsActivity, "Не удалось добавить отзыв", Toast.LENGTH_SHORT).show()
                                    }
                                } catch (e: Exception) {
                                    Toast.makeText(this@BookDetailsActivity, "Ошибка. Не удалось добавить отзыв", Toast.LENGTH_SHORT).show()
                                }
                                finally {
                                    showLoading(false)
                                }
                        }
                    }
                }
                d.dismiss()
            }
            .setNegativeButton("Отмена", null)
        dlg.show()
    }

    private fun showReviews(list: List<Review>) {
        reviewsContainer.removeAllViews()
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
            author.text = r.userObj?.email ?: "Аноним"
            rating.rating = r.rating.toFloat()
            text.text = r.comment
            val curr = AuthManager.currentUserEmail()
            if (curr != null && curr == r.userObj?.email) {
                del.visibility = Button.VISIBLE
                del.setOnClickListener {
                    coroutineScope.launch {
                        showLoading(true)
                        try {
                            val response = withContext(Dispatchers.IO) { apiService.deleteReview(r.id!!).execute() }
                            if (response.isSuccessful) {
                                val updatedReviews = withContext(Dispatchers.IO) { apiService.getReviewsByBookId(bookId).execute().body() }
                                showReviews(updatedReviews ?: emptyList())
                                Toast.makeText(this@BookDetailsActivity, "Отзыв удален", Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(this@BookDetailsActivity, "Ошибка удаления", Toast.LENGTH_SHORT).show()
                        } finally {
                            showLoading(false)
                        }
                    }
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

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
    }
}