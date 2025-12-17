//package by.bsu.bookstore.model
//
//import by.bsu.bookstore.R
//import java.io.Serializable
//import java.util.*
//
////data class Book(
////    val bookId: Int = 0,
////    val title: String,
////    val authors: List<String> = emptyList(),
////    val publisher: String? = null,
////    val price: Double = 0.0,
////    val description: String? = null,
////    val category: String? = null,
////    val rating: Float = 0f,
////    val inStock: Int = 0,
////    val preorder: Boolean = false,
////    val availabilityDate: Date? = null,
////    val coverResId: Int? = R.drawable.book_cover
////) : Serializable
//
//data class Book(
//    val id: Int = 0,
//    val isbn: String = "",
//    val publisherId: Int = 0,
//    val title: String,
//    //val authorId: Int = 0,
//    val description: String = "",
//    val price: Double = 0.0,
//    val language: String = "Русский",
//    val category: String = "",
//    val author: String,
//    val coverUrl: String = "",
//    val defaultCover: Int? = R.drawable.book_cover,
//    val rating: Float = 0f,
//
//    //val pageCount: Int = 0, //
//    //val publicationDate: Date? = null,
//    //val inStock: Int = 0,
//    val preorder: Boolean = false, //
//    val availabilityDate: Date? = null, //
//    //val publisherName: String = "",
//    val discountId: Int? = null //
//) : Serializable
//
//data class Promotion(
//    val id: String = "",
//    var title: String,
//    var description: String,
//    var percentage: Double = 0.0
//) : Serializable
//
//data class Review(
//    val id: Int,
//    var rating: Int, //От 1 до 5
//    var comment: String = "",
//    var bookId: Int, //Добавить в БД!
//    var userId: Int //Добавить в БД!
//) : Serializable
//
//data class Publisher(
//    val id: Int,
//    var name: String,
//    var email: String? = null,
//    var phone: String? = null
//) : Serializable
//
//data class BookSection(
//    val title: String,
//    val books: List<Book>
//) : Serializable
//
//data class User(
//    val id: Int = 0,
//    val firstName: String,
//    val lastName: String,
//    val email: String,
//    val role: String = "Пользователь",
//    val phone: String? = null,
//    val address: String? = null,
//    val regDate: Date? = null,
//    val password: String? = null
//) : Serializable
//
//data class CartItem(
//    val book: Book,
//    var quantity: Int = 1
//) : Serializable
//
//data class OrderItem(
//    val book: Book,
//    var quantity: Int = 1
//)
//
//data class Order(
//    val orderId: Int = 0,
//    val customerId: Int,
//    val items: List<OrderItem>,
//    val totalAmount: Double,
//    var status: String = "Новый",
//    val address: String
//) : Serializable
//

package by.bsu.bookstore.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Book(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("title") val title: String,
    @SerializedName("author") val author: String,
    @SerializedName("description") val description: String = "",
    @SerializedName("price") val price: Double = 0.0,
    @SerializedName("isbn") val isbn: String = "",
    @SerializedName("category_id") val categoryId: Int = 0,
    @SerializedName("publisher_id") val publisherId: Int = 0,
    @SerializedName("discount_id") val discountId: Int = 0,
    @SerializedName("language") val language: String = "Русский",
    @SerializedName("link") val coverUrl: String? = null,
    @SerializedName("rating") val rating: Float = 0f,
    @SerializedName("preorder") val preorder: Boolean = false,
    @SerializedName("available_date") val availableDate: String? = null,

    @SerializedName("category") val categoryObj: Category? = null,
    @SerializedName("publisher") val publisherObj: Publisher? = null,
    @SerializedName("discount") val discountObj: Discount? = null
) : Serializable {
    val categoryName: String get() = categoryObj?.name ?: "Общее"
    val publisherName: String get() = publisherObj?.name ?: ""
}

data class UserProfileUpdateDTO(
    @SerializedName("first_name") val firstName: String,
    @SerializedName("last_name") val lastName: String,
    @SerializedName("phone") val phone: String?,
    @SerializedName("address") val address: String?
)

data class Category(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String
) : Serializable

data class Review(
    @SerializedName("id") val id: Int?,
    @SerializedName("rating") var rating: String,
    @SerializedName("comment") var comment: String = "",
    @SerializedName("book_id") var bookId: Int,
    @SerializedName("user_id") var userId: Int,

    @SerializedName("user") val userObj: User? = null,
    @SerializedName("book") val bookObj: Book? = null,
) : Serializable{
    constructor(
        rating: String,
        comment: String,
        bookId: Int,
        userId: Int
    ) : this(
        id = null,
        rating = rating,
        comment = comment,
        bookId = bookId,
        userId = userId,
        userObj = null,
        bookObj = null
    )
}

data class Publisher(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("email") val email: String? = null,
    @SerializedName("phone") val phone: String? = null
) : Serializable

data class Discount(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("percentage") val percentage: Double,
    @SerializedName("description") val description: String? = null
) : Serializable

data class User(
    @SerializedName("id") val id: Int?,
    @SerializedName("first_name") val firstName: String,
    @SerializedName("last_name") val lastName: String,
    @SerializedName("email") val email: String,
    @SerializedName("role") val role: String = "Пользователь",
    @SerializedName("phone") val phone: String? = null,
    @SerializedName("address") val address: String? = null,
    @SerializedName("reg_date") val regDate: String? = null,
    @SerializedName("password") val password: String? = null
) : Serializable {
    constructor(
        firstName: String,
        lastName: String,
        email: String,
        phone: String? = null,
    password: String?
    ) : this(
    id = null,
    firstName = firstName,
    lastName = lastName,
    email = email,
    phone = phone,
    password = password,
    regDate = null
    )
}

data class Order(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("user_id") val userId: Int,
    @SerializedName("total_amount") val totalAmount: Double,
    @SerializedName("status") var status: String = "Новый",
    @SerializedName("address") val address: String,
    @SerializedName("items") val itemsString: String,

    @SerializedName("user") val userObj: User? = null,
) : Serializable

data class OrderItem(
    val book: Book,
    var quantity: Int = 1
)

data class BookSection(
    val title: String,
    val books: List<Book>
) : Serializable

data class LoginRequest(
    val email: String,
    val password: String
)

data class CartItem(
    val book: Book,
    var quantity: Int = 1
) : Serializable