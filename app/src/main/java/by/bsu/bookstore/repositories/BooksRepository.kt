package by.bsu.bookstore.repositories

import by.bsu.bookstore.Book
import by.bsu.bookstore.User
import by.bsu.bookstore.repositories.UserRepository.users
import java.util.Collections.max

object BooksRepository {
    val books: MutableList<Book> = mutableListOf(
        Book(1, "1984", listOf("Джордж Оруэлл"), "АСТ", 12.5, "Антиутопия", "Новинки", 4.5f, coverResId = null),
        Book(2, "Мастер и Маргарита", listOf("М. Булгаков"), "Эксмо", 18.0, "Роман", "Классика", 4.8f, coverResId = null),
        Book(3, "Преступление и наказание", listOf("Ф. Достоевский"), "Питер", 15.0, "Классика русской литературы", "Классика", 4.6f, coverResId = null),
        Book(4, "Моби Дик", listOf("Герман Мелвилл"), "Иностранка", 20.0, "Приключенческий роман", "Классика", 4.2f, coverResId = null),
        Book(5, "Слепой убийца", listOf("Маргарет Этвуд"), "Астрель", 16.5, "Фикшн", "Новинки", 4.8f, coverResId = null),
        Book(6, "Пересмешник", listOf("Харпер Ли"), "АСТ", 14.0, "Современный роман", "Новинки", 4.7f, coverResId = null)
    )

    fun createBook(book: Book): Book {
        books.add(book)
        return book
    }

    fun getNewId(): Int{
        if (getAllIds().isNotEmpty()){
            return max(getAllIds()) + 1
        }
        else return 1;
    }

    fun getBooksByCategory(category: String): List<Book> {
        if (category == "Все книги"){
            return books
        }
        return books.filter { it.category.equals(category, ignoreCase = true) }
    }

    fun getBookById(id: Int): Book? {
        return books.find { it.bookId == id }
    }

    fun getPriceById(id: Int): Double? {
        return books.find { it.bookId == id }?.price
    }


    fun getAllIds(): List<Int> {
        val ids : MutableList<Int> = mutableListOf()
        for (b in books){
            ids.add(b.bookId)
        }
        return ids
    }

    fun getAllBooks(): List<Book> {
        return books
    }

    fun updateBook(id: Int, updatedBook: Book): Boolean {
        val bookIndex = books.indexOfFirst { it.bookId == id }
        if (bookIndex != -1) {
            books[bookIndex] = updatedBook
            return true
        }
        return false
    }

    fun deleteBook(id: Int): Boolean {
        return books.removeIf { it.bookId == id }
    }
}