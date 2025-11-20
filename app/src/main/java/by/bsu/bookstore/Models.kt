package by.bsu.bookstore

data class Book(
    val title: String,
    val author: String,
    val rating: Float,
    val coverUrl: String? = null
)

data class Promotion(
    val title: String,
    val description: String,
    val imageUrl: String? = null
)

data class BookSection(
    val title: String,
    val books: List<Book>
)