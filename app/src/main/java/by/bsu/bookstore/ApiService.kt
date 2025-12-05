package by.bsu.bookstore

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @GET("books")
    fun getBooks(): Call<List<Book>>

    @GET("books/{id}")
    fun getBook(@Path("id") id: Int): Call<Book>

    @POST("orders")
    fun createOrder(@Body order: Order): Call<Order>

    @POST("auth/register")
    fun register(@Body userData: Map<String, String>): Call<User>

    @POST("auth/login")
    fun login(@Body credentials: Map<String, String>): Call<Map<String, String>>
}