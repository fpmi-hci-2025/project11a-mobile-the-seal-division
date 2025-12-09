package by.bsu.bookstore.repositories

import android.icu.text.DateFormat
import by.bsu.bookstore.R
import by.bsu.bookstore.model.Book
import java.util.Calendar
import java.util.Collections.max
import java.util.Date

object BooksRepository {
    val books: MutableList<Book> = mutableListOf(
        Book(id = 1, isbn = "978-5-04-116479-9", publisherId = 1, title = "1984", description ="Легендарная антиутопия", price = 12.5, language = "Русский", category = "Новинки", author = "Джордж Оруэлл", coverUrl = "https://www.bookclub.by/goods_img/55545_08269aa036fe2a501e49c7bab8c5368605f937eb.webp?v=1", rating = 4.5f),
        Book(id = 2, isbn = "978-5-04-177034-1", title = "Мастер и Маргарита", author = "М. Булгаков", publisherId = 2, preorder=true, availabilityDate = Calendar.getInstance().apply { add(
            Calendar.DAY_OF_YEAR, 1) }.time, price = 18.0, description = "Роман", category = "Классика", coverUrl = "https://content.img-gorod.ru/pim/products/images/e9/9b/018fa279-3bbb-7d59-bad3-940d92f5e99b.jpg", rating = 4.8f),
        Book(id = 3, isbn = "978-5-04-116479-7", title = "Преступление и наказание", author = "Ф. Достоевский", publisherId = 1, price = 15.0, description = "Классика русской литературы", category = "Классика", coverUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTr8sXE2K6LM1XXClozcn5id1Bq0NwUeoEXBQ&s", rating = 4.6f),
        Book(id = 4, isbn = "978-5-04-116479-6", title = "Моби Дик", author = "Герман Мелвилл", publisherId = 4, price = 20.0, description = "Приключенческий роман", category = "Классика", coverUrl = "https://cdn.azbooka.ru/cv/w1100/3cbe0a3b-03fb-4872-ac70-e1e6ad106e09.jpg", rating = 4.2f),
        Book(id = 5, isbn = "978-5-04-116479-5", title = "Слепой убийца", author = "Маргарет Этвуд", publisherId = 3, price = 16.5, description = "Фикшн", category = "Новинки", coverUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQdh0ab_zrP-_1TiG01ckaI7CH9HZCCL3l7Pw&s", rating = 4.8f),
        Book(id = 6, isbn = "978-5-04-116479-4", title = "Убить пересмешника", author = "Харпер Ли", publisherId = 1, price = 14.0, description = "Современный роман", category = "Новинки", coverUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRQ6ergHIE4bg_JspZK1s4uJtiNrE8yaiXeLg&s", rating = 4.7f)
    )

    fun createBook(book: Book): Book {
        val newBook = if (book.id == 0) {
            book.copy(id = getNewId())
        } else {
            book
        }
        books.add(newBook)
        return newBook
    }


    fun getNewId(): Int {
        val maxId = books.maxByOrNull { it.id }?.id ?: 0
        return maxId + 1
    }


    fun getAllBooks(): List<Book> {
        return books.toList()
    }

    fun getBooksByCategory(category: String): List<Book> {
        if (category == "Все книги") {
            return getAllBooks()
        }
        return books.filter { it.category.equals(category, ignoreCase = true) }
    }

    fun getBookById(id: Int): Book? {
        return books.find { it.id == id }
    }

    fun getPriceById(id: Int): Double? {
        return getBookById(id)?.price
    }

    fun updateBook(id: Int, updatedBook: Book): Boolean {
        val bookIndex = books.indexOfFirst { it.id == id }
        if (bookIndex != -1) {
            books[bookIndex] = updatedBook.copy(id = id)
            return true
        }
        return false
    }

    fun deleteBook(id: Int): Boolean {
        return books.removeIf { it.id == id }
    }
}