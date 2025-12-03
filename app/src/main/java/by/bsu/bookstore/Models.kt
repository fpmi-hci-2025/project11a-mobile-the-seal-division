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

data class OrderItem(val book: Book, var quantity: Int = 1)
data class Order(
    val orderId: Int = 0,
    val customer: User,
    val items: List<OrderItem>,
    val totalAmount: Double,
    var status: String = "новый",
    val address: String
) : Serializable
