package by.bsu.bookstore

object CartManager {
    private val items = mutableListOf<OrderItem>()

    fun addToCart(book: Book, quantity: Int = 1) {
        val existing = items.find { it.book.bookId == book.bookId && it.book.title == book.title }
        if (existing != null) {
            existing.quantity += quantity
        } else {
            items.add(OrderItem(book, quantity))
        }
    }

    fun removeFromCart(bookId: Int) {
        items.removeAll { it.book.bookId == bookId }
    }

    fun clear() = items.clear()

    fun getItems(): List<OrderItem> = items.toList()

    fun getTotal(): Double = items.sumOf { it.book.price * it.quantity }
}
