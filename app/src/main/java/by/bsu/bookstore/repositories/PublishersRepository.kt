package by.bsu.bookstore.repositories

import by.bsu.bookstore.model.Publisher

object PublishersRepository {
    val publishers: MutableList<Publisher> = mutableListOf(
        Publisher(id = 1, name = "Эксмо", email = "contact@eksmo.ru", phone = "+7-495-411-68-86"),
        Publisher(id = 2, name = "АСТ", email = "office@ast.ru", phone = "+7-495-933-76-01"),
        Publisher(id = 3, name = "Азбука-Аттикус", email = "info@azbooka-atticus.ru", phone = "+7-812-327-04-55"),
        Publisher(id = 4, name = "Альпина Паблишер", email = "info@alpinabook.ru", phone = "+7-495-980-53-54"),
        Publisher(id = 5, name = "Питер", email = "sales@piter.com", phone = "+7-812-703-73-73")
    )

    /**
     * CREATE: Добавляет новое издательство.
     */
    fun create(publisher: Publisher): Publisher {
        val newPublisher = if (publisher.id == 0) {
            publisher.copy(id = getNewId())
        } else {
            publisher
        }
        publishers.add(newPublisher)
        return newPublisher
    }

    /**
     * Генерирует новый уникальный ID для издательства.
     */
    fun getNewId(): Int {
        val maxId = publishers.maxByOrNull { it.id }?.id ?: 0
        return maxId + 1
    }

    /**
     * READ: Возвращает список всех издательств.
     */
    fun findAll(): List<Publisher> {
        return publishers.toList()
    }

    /**
     * READ: Находит издательство по его ID.
     */
    fun findById(id: Int): Publisher? {
        return publishers.find { it.id == id }
    }

    /**
     * UPDATE: Обновляет существующее издательство.
     */
    fun update(id: Int, updatedPublisher: Publisher): Boolean {
        val index = publishers.indexOfFirst { it.id == id }
        if (index != -1) {
            publishers[index] = updatedPublisher.copy(id = id)
            return true
        }
        return false
    }

    /**
     * DELETE: Удаляет издательство по его ID.
     */
    fun delete(id: Int): Boolean {
        return publishers.removeIf { it.id == id }
    }
}