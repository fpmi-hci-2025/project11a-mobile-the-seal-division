package by.bsu.bookstore

object OrdersStorage {
    val orders: MutableList<Order> = mutableListOf()

    init {
        // sample
        val u = User(1, "Иван", "Иванов", "ivan@example.com" )
        orders.add(Order(1001, u, listOf(), 0.0, "в обработке", "ул. Ленина"))
    }

    fun add(order: Order) { orders.add(order) }
    fun clear() = orders.clear()
}
