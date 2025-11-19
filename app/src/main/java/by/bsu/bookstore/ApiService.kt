package by.bsu.bookstore

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("books")
    suspend fun getBooks(): Response<List<Book>>

    @GET("books/categories/{category}")
    suspend fun getBooksByCategory(@Path("category") category: String): Response<List<Book>>

    @GET("promotions")
    suspend fun getPromotions(): Response<List<Promotion>>
}