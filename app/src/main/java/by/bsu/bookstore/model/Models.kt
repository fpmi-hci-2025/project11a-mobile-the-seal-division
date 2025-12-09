package by.bsu.bookstore.model

import by.bsu.bookstore.R
import java.io.Serializable
import java.util.*

//data class Book(
//    val bookId: Int = 0,
//    val title: String,
//    val authors: List<String> = emptyList(),
//    val publisher: String? = null,
//    val price: Double = 0.0,
//    val description: String? = null,
//    val category: String? = null,
//    val rating: Float = 0f,
//    val inStock: Int = 0,
//    val preorder: Boolean = false,
//    val availabilityDate: Date? = null,
//    val coverResId: Int? = R.drawable.book_cover
//) : Serializable

data class Book(
    val id: Int = 0,
    val isbn: String = "",
    val publisherId: Int = 0,
    val title: String,
    //val authorId: Int = 0,
    val description: String = "",
    val price: Double = 0.0,
    val language: String = "Русский",
    val category: String = "",
    val author: String,
    val coverUrl: String = "",
    val defaultCover: Int? = R.drawable.book_cover,
    val rating: Float = 0f, //Добавить в БД!

    //val pageCount: Int = 0, //
    //val publicationDate: Date? = null,
    //val inStock: Int = 0,
    val preorder: Boolean = false, //
    val availabilityDate: Date? = null, //
    //val publisherName: String = "",
    //val discountId: Int? = null //
) : Serializable

data class Promotion(
    val id: String = "",
    var title: String,
    var description: String,
    var percentage: Double = 0.0
) : Serializable

data class Review(
    val id: Int,
    var rating: Int, //От 1 до 5
    var comment: String = "",
    var bookId: Int, //Добавить в БД!
    var userId: Int //Добавить в БД!
) : Serializable

data class Publisher(
    val id: Int,
    var name: String,
    var email: String? = null,
    var phone: String? = null
) : Serializable

data class BookSection(
    val title: String,
    val books: List<Book>
) : Serializable

data class User(
    val id: Int = 0,
    val firstName: String,
    val lastName: String,
    val email: String,
    val role: String = "Пользователь",
    val phone: String? = null,
    val address: String? = null,
    val regDate: Date? = null,
    val password: String? = null
) : Serializable

data class CartItem(
    val book: Book,
    var quantity: Int = 1
) : Serializable

data class OrderItem(
    val book: Book,
    var quantity: Int = 1
)

data class Order(
    val orderId: Int = 0,
    val customerId: Int,
    val items: List<OrderItem>,
    val totalAmount: Double,
    var status: String = "новый",
    val address: String
) : Serializable

