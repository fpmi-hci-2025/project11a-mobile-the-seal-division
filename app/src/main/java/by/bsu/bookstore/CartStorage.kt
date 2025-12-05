package by.bsu.bookstore

object CartStorage {
    val items: MutableList<CartItem> = mutableListOf()

    fun add(book: Book, qty: Int = 1) {
        val existing = items.find { it.book.bookId == book.bookId }
        if (existing != null) existing.quantity += qty
        else items.add(CartItem(book, qty))
    }

    fun remove(item: CartItem) {
        items.remove(item)
    }

    fun clear() {
        items.clear()
    }
}
