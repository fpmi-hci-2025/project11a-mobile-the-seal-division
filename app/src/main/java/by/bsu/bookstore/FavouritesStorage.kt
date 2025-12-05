package by.bsu.bookstore

object FavoritesStorage {
    private val favs = mutableListOf<Book>()

    fun items(): List<Book> = favs.toList()

    fun isFavorite(book: Book): Boolean = favs.any { it.bookId == book.bookId }

    fun toggle(book: Book) {
        val existing = favs.find { it.bookId == book.bookId }
        if (existing != null) favs.remove(existing) else favs.add(book)
    }

    fun clear() = favs.clear()
}
