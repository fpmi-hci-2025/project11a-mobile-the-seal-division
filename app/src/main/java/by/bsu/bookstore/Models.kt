package by.bsu.bookstore

//data class Book(
//    val title: String,
//    val author: String,
//    val rating: Float,
//    val coverUrl: String? = null
//)
//
//data class Promotion(
//    val title: String,
//    val description: String,
//    val imageUrl: String? = null
//)
//
//data class BookSection(
//    val title: String,
//    val books: List<Book>
//)
// File: app/src/main/java/by/bsu/bookstore/models.kt

import java.io.Serializable
import java.util.*

data class Book(
    val bookId: Int = 0,
    val title: String,
    val authors: List<String> = emptyList(),
    val publisher: String? = null,
    val price: Double = 0.0,
    val description: String? = null,
    val category: String? = null,
    val rating: Float = 0f,
    val inStock: Int = 0,
    val preorder: Boolean = false,
    val availabilityDate: Date? = null,
    val coverResId: Int? = R.drawable.book_cover // default drawable
) : Serializable

data class Promotion(val title: String, val description: String) : Serializable

data class BookSection(val title: String, val books: List<Book>) : Serializable

data class User(
    val userId: Int = 0,
    val firstName: String,
    val lastName: String,
    val email: String
) : Serializable

object UserSession {
    var currentUser: User? = null
}

data class OrderItem(val book: Book, var quantity: Int = 1)
data class Order(
    val orderId: Int = 0,
    val customer: User,
    val items: List<OrderItem>,
    val totalAmount: Double,
    var status: String = "новый",
    val address: String
) : Serializable

//object SampleData {
//    val sampleBooks: List<Book> = listOf(
//        Book(1, "1984", listOf("Джордж Оруэлл"), "АСТ", 12.5, "Антиутопия", 4.5f, coverResId = null),
//        Book(2, "Мастер и Маргарита", listOf("М. Булгаков"), "Эксмо", 18.0, "Роман", 4.8f, coverResId = null),
//        Book(3, "Преступление и наказание", listOf("Ф. Достоевский"), "Питер", 15.0, "Классика", 4.6f, coverResId = null)
//    )
//}

