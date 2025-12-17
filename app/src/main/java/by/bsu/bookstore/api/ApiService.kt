package by.bsu.bookstore.api

import by.bsu.bookstore.model.*
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    // --- BOOKS ---
    @GET("books/all")
    fun getAllBooks(): Call<List<Book>>

    @GET("books/{id}")
    fun getBook(@Path("id") id: Int): Call<Book>

    @GET("books/categories/{category}")
    fun getBooksByCategory(@Path("category") category: String): Call<List<Book>>

    @GET("books/publishers/{publisher}")
    fun getBooksByPublisher(@Path("publisher") publisher: String): Call<List<Book>>

    @GET("books/authors/{author}")
    fun getBooksByAuthor(@Path("author") author: String): Call<List<Book>>

    @POST("books")
    fun addBook(@Body book: Book): Call<Book>

    @PUT("books/{id}")
    fun updateBook(@Path("id") id: Int, @Body book: Book): Call<Book>

    @DELETE("books/{id}")
    fun deleteBook(@Path("id") id: Int): Call<Void>

    // --- REVIEWS ---
    @GET("books/reviews/{id}")
    fun getReviewById(@Path("id") id: Int): Call<Review>

    @GET("books/reviews/book/{id}")
    fun getReviewsByBookId(@Path("id") id: Int): Call<List<Review>>

    @POST("books/reviews/{id}")
    fun addReview(@Path("id") bookId: Int, @Body review: Review): Call<Review>

    @PUT("books/reviews/{id}")
    fun updateReview(@Path("id") id: Int, @Body review: Review): Call<Review>

    @DELETE("books/reviews/{id}")
    fun deleteReview(@Path("id") id: Int): Call<Void>

    // --- CATEGORIES ---
    @GET("categories/all")
    fun getAllCategories(): Call<List<Category>>

    @GET("categories/{id}")
    fun getCategory(@Path("id") id: Int): Call<Category>

    @POST("categories")
    fun addCategory(@Body category: Category): Call<Category>

    // --- DISCOUNTS ---
    @GET("discounts/all")
    fun getAllDiscounts(): Call<List<Discount>>

    @POST("discounts")
    fun addDiscount(@Body discount: Discount): Call<Discount>

    @PATCH("discounts/{id}")
    fun updateDiscount(@Path("id") id: Int, @Body discount: Discount): Call<Discount>

    // --- PUBLISHERS ---
    @GET("publishers/{id}")
    fun getPublisher(@Path("id") id: Int): Call<Publisher>

    @POST("publishers")
    fun addPublisher(@Body publisher: Publisher): Call<Publisher>

    // --- USERS ---
    @POST("users")
    fun register(@Body user: User): Call<User>

    @POST("login")
    fun login(@Body credentials: LoginRequest): Call<User>

    @PUT("users/{id}")
    fun updateUserProfile(@Path("id") id: Int, @Body profileUpdate: UserProfileUpdateDTO): Call<User>

    // --- ORDERS ---
    @GET("orders/all")
    fun getAllOrders(): Call<List<Order>>

    @GET("orders/user/{id}")
    fun getOrdersByUserId(@Path("id") userId: Int): Call<List<Order>>

    @GET("orders/{id}")
    fun getOrder(@Path("id") id: Int): Call<Order>

    @POST("orders")
    fun createOrder(@Body order: Order): Call<Order>

    @DELETE("orders/{id}")
    fun deleteOrder(@Path("id") id: Int): Call<Void>

    // --- HEALTH ---
    @GET("health")
    fun healthCheck(): Call<Map<String, String>>


}