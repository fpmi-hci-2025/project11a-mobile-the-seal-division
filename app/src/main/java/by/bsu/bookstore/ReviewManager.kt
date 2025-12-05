package by.bsu.bookstore

data class Review(val id: Long, val bookId: Int, val authorEmail: String?, val text: String, val rating: Int)

object ReviewManager {
    private val reviews = mutableListOf<Review>()

    fun init() {
    }

    fun addReview(bookId: Int, text: String, rating: Int, authorEmail: String?): Review {
        val r = Review(System.currentTimeMillis(), bookId, authorEmail, text, rating)
        reviews.add(0, r)
        return r
    }

    fun removeReview(reviewId: Long, authorEmail: String?): Boolean {
        val it = reviews.find { it.id == reviewId && it.authorEmail == authorEmail }
        return if (it != null) {
            reviews.remove(it)
            true
        } else {
            false
        }
    }

    fun getForBook(bookId: Int): List<Review> {
        return reviews.filter { it.bookId == bookId }
    }
}
