package by.bsu.bookstore.repositories

import by.bsu.bookstore.model.Order
import by.bsu.bookstore.model.OrderItem

object OrdersRepository {
    val orders: MutableList<Order> = mutableListOf(
        Order(
            orderId = 1001,
            customerId = 1,
            items = listOf(OrderItem(BooksRepository.getBookById(1)!!)),
            totalAmount = BooksRepository.getPriceById(1)!!,
            status = "новый",
            address = "ул. Ленина, 1"
        ),
        Order(
            orderId = 1002,
            customerId = 2,
            items = listOf(OrderItem(BooksRepository.getBookById(2)!!)),
            totalAmount = BooksRepository.getPriceById(2)!!,
            status = "в обработке",
            address = "ул. Садовая, 5"
        )
    )

    fun createOrder(order: Order): Order {
        val newOrder = if (order.orderId == 0) {
            order.copy(orderId = getNewId())
        } else {
            order
        }
        orders.add(newOrder)
        return newOrder
    }

    fun getNewId(): Int {
        val maxId = orders.maxByOrNull { it.orderId }?.orderId ?: 0
        return maxId + 1
    }


    fun getOrderById(id: Int): Order? {
        return orders.find { it.orderId == id }
    }

    fun getOrdersByCustomerId(customerId: Int): List<Order> {
        if (customerId < 0){
            return emptyList()
        }
        return orders.filter { it.customerId == customerId }
    }


    fun getAllOrders(): List<Order> {
        return orders.toList()
    }

    fun updateOrder(id: Int, updatedOrder: Order): Boolean {
        val orderIndex = orders.indexOfFirst { it.orderId == id }
        return if (orderIndex != -1) {
            orders[orderIndex] = updatedOrder.copy(orderId = id)
            true
        } else {
            false
        }
    }

    fun deleteOrder(id: Int): Boolean {
        return orders.removeIf { it.orderId == id }
    }
}