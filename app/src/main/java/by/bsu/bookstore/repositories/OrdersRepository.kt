package by.bsu.bookstore.repositories

import by.bsu.bookstore.Book
import by.bsu.bookstore.Order
import by.bsu.bookstore.OrderItem
import by.bsu.bookstore.User
import by.bsu.bookstore.repositories.BooksRepository.books
import java.util.Collections.max

object OrdersRepository {
    val orders : MutableList<Order> = mutableListOf(
        Order(1001, UserRepository.getUserById(1)!!, listOf(OrderItem(BooksRepository.getBookById(1)!!)), BooksRepository.getPriceById(1)!!, "новый", "ул. Ленина, 1"),
        Order(1002, UserRepository.getUserById(2)!!, listOf(OrderItem(BooksRepository.getBookById(2)!!)), BooksRepository.getPriceById(2)!!,"в обработке", "ул. Садовая, 5")
    )

    fun createOrder(order: Order): Order {
        orders.add(order)
        return order
    }

    fun getNewId(): Int {
        return if (orders.isNotEmpty()) max(orders.map { it.orderId }) + 1 else 1
    }

    fun getOrderById(id: Int): Order? {
        return orders.find { it.orderId == id }
    }

    fun getOrdersByEmail(email: String): List<Order> {
        return orders.filter { it.customer.email.equals(email, ignoreCase = true) }
    }

    fun getAllOrders(): List<Order> {
        return orders
    }

    fun updateOrder(id: Int, updatedOrder: Order): Boolean {
        val orderIndex = orders.indexOfFirst { it.orderId == id }
        return if (orderIndex != -1) {
            orders[orderIndex] = updatedOrder
            true
        } else {
            false
        }
    }

    fun deleteOrder(id: Int): Boolean {
        return orders.removeIf { it.orderId == id }
    }
}