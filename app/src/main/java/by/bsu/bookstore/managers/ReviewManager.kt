package by.bsu.bookstore.managers

import android.content.Context
import by.bsu.bookstore.model.Review
import by.bsu.bookstore.repositories.ReviewsRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object ReviewManager {
    private const val PREFS_NAME = "reviews_prefs"
    private const val KEY_REVIEWS = "reviews_key"
    private val gson = Gson()
    private var reviews: MutableList<Review> = mutableListOf()

    /**
     * Инициализирует менеджер, загружая отзывы из SharedPreferences.
     * Если сохраненных отзывов нет, загружает начальные данные из ReviewsRepository.
     */
    fun init(context: Context) {
        val sp = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = sp.getString(KEY_REVIEWS, null)
        if (!json.isNullOrEmpty()) {
            val type = object : TypeToken<MutableList<Review>>() {}.type
            reviews = gson.fromJson(json, type)
        } else {
            reviews = ReviewsRepository.reviews.toMutableList()
            save(context)
        }
    }

    /**
     * Добавляет новый отзыв и сохраняет обновленный список.
     * @param bookId ID книги.
     * @param comment Текст отзыва.
     * @param rating Оценка от 1 до 5.
     * @param userId ID пользователя, оставившего отзыв.
     * @return Созданный объект Review.
     */
    fun addReview(context: Context, bookId: Int, comment: String, rating: Int, userId: Int): Review {
        val newId = (reviews.maxByOrNull { it.id }?.id ?: 0) + 1
        val newReview = Review(
            id = newId,
            bookId = bookId,
            comment = comment,
            rating = rating,
            userId = userId
        )
        reviews.add(0, newReview)
        save(context)
        return newReview
    }

    /**
     * Удаляет отзыв, если он принадлежит указанному пользователю.
     * @param reviewId ID отзыва для удаления.
     * @param userId ID пользователя, который пытается удалить отзыв.
     * @return true, если удаление прошло успешно, иначе false.
     */
    fun removeReview(context: Context, reviewId: Int, userId: Int): Boolean {
        val removed = reviews.removeIf { it.id == reviewId && it.userId == userId }
        if (removed) {
            save(context)
        }
        return removed
    }

    /**
     * Возвращает список всех отзывов для конкретной книги.
     */
    fun getForBook(bookId: Int): List<Review> {
        return reviews.filter { it.bookId == bookId }
    }

    /**
     * Возвращает все отзывы, оставленные конкретным пользователем.
     */
    fun getForUser(userId: Int): List<Review> {
        return reviews.filter { it.userId == userId }
    }

    /**
     * Сохраняет текущий список отзывов в SharedPreferences.
     */
    private fun save(context: Context) {
        val sp = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = gson.toJson(reviews)
        sp.edit().putString(KEY_REVIEWS, json).apply()
    }
}