package by.bsu.bookstore

import by.bsu.bookstore.model.Book
import by.bsu.bookstore.model.Discount
import org.junit.Assert.assertEquals
import org.junit.Test

class PriceCalculationTest {

    private fun calculateNewPrice(book: Book): Double {
        val discount = book.discountObj
        return if (discount != null && discount.percentage > 0) {
            book.price * (1 - discount.percentage / 100.0)
        } else {
            book.price
        }
    }

    @Test
    fun `calculateNewPrice with 20 percent discount`() {
        val discount = Discount(id = 1, title = "Sale", description = "", percentage = 20.0)
        val book = Book(id = 1, title = "Test Book", author="author", price = 100.0, discountObj = discount)

        val newPrice = calculateNewPrice(book)

        assertEquals(80.0, newPrice, 0.001)
    }

    @Test
    fun `calculateNewPrice without discount`() {
        val book = Book(id = 2, title = "Another Book", author="author2", price = 150.0, discountObj = null)

        val newPrice = calculateNewPrice(book)

        assertEquals(150.0, newPrice, 0.001)
    }

    @Test
    fun `calculateNewPrice with zero percent discount`() {
        val discount = Discount(id = 2, title = "Fake Sale", description = "", percentage = 0.0)
        val book = Book(id = 3, title = "Book With Zero Discount", author="author3", price = 200.0, discountObj = discount)

        val newPrice = calculateNewPrice(book)

        assertEquals(200.0, newPrice, 0.001)
    }
}