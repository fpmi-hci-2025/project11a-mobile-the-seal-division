package by.bsu.bookstore.repositories

import by.bsu.bookstore.model.Review

object ReviewsRepository {
    val reviews: MutableList<Review> = mutableListOf(
        Review(id = 1, bookId = 1, rating = 5, comment = "Великая книга, актуальна как никогда.", userId = 1),
        Review(id = 2, bookId = 1, rating = 4, comment = "Заставляет задуматься. Рекомендую.", userId = 2),
        Review(id = 3, bookId = 2, rating = 5, comment = "Шедевр на все времена!", userId = 3),
        Review(id = 4, bookId = 3, rating = 5, comment = "Глубокое произведение о человеческой душе.", userId = 1)
    )

    /**
     * CREATE: Добавляет новый отзыв.
     */
    fun create(review: Review): Review {
        val newReview = if (review.id == 0) {
            review.copy(id = getNewId())
        } else {
            review
        }
        reviews.add(newReview)
        return newReview
    }

    /**
     * Генерирует новый уникальный ID для отзыва.
     */
    fun getNewId(): Int {
        val maxId = reviews.maxByOrNull { it.id }?.id ?: 0
        return maxId + 1
    }

    /**
     * READ: Возвращает все отзывы.
     */
    fun findAll(): List<Review> {
        return reviews.toList()
    }

    /**
     * READ: Находит отзыв по его ID.
     */
    fun findById(id: Int): Review? {
        return reviews.find { it.id == id }
    }

    /**
     * READ: Находит все отзывы для конкретной книги.
     */
    fun findByBookId(bookId: Int): List<Review> {
        return reviews.filter { it.bookId == bookId }
    }

    /**
     * READ: Находит все отзывы от конкретного пользователя.
     */
    fun findByUserId(userId: Int): List<Review> {
        return reviews.filter { it.userId == userId }
    }

    /**
     * UPDATE: Обновляет существующий отзыв.
     */
    fun update(id: Int, updatedReview: Review): Boolean {
        val index = reviews.indexOfFirst { it.id == id }
        if (index != -1) {
            val original = reviews[index]
            reviews[index] = updatedReview.copy(
                id = original.id,
                bookId = original.bookId,
                userId = original.userId
            )
            return true
        }
        return false
    }

    /**
     * DELETE: Удаляет отзыв по его ID.
     */
    fun delete(id: Int): Boolean {
        return reviews.removeIf { it.id == id }
    }
}