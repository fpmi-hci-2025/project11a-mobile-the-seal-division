package by.bsu.bookstore

data class CartItem(
    val book: Book,
    var quantity: Int = 1
)
