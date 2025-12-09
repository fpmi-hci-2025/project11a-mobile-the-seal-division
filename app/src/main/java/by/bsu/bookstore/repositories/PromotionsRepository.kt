package by.bsu.bookstore.repositories

import by.bsu.bookstore.model.Promotion
import java.util.UUID

object PromotionsRepository {
    val promotions: MutableList<Promotion> = mutableListOf(
        Promotion(id = "CLASSIC20", title = "Скидка 20% на классику", description = "Только до конца месяца!", percentage = 20.0),
        Promotion(id = "NEW15", title = "Новинки со скидкой 15%", description = "2026 год начинается с отличных книг!", percentage = 15.0),
        Promotion(id = "FREE_DELIVERY", title = "Бесплатная доставка", description = "При заказе от 50 BYN", percentage = 0.0)
    )

    /**
     * CREATE: Создает и добавляет новую акцию в репозиторий.
     * @param title Заголовок акции.
     * @param description Описание акции.
     * @param percentage Процент скидки.
     * @return Созданный объект Promotion с присвоенным ID.
     */
    fun create(title: String, description: String, percentage: Double): Promotion {
        val newId = UUID.randomUUID().toString()
        val newPromotion = Promotion(id = newId, title = title, description = description, percentage = percentage)
        promotions.add(newPromotion)
        return newPromotion
    }

    /**
     * READ (All): Возвращает весь список акций.
     * @return Неизменяемый список (List) всех акций.
     */
    fun findAll(): List<Promotion> {
        return promotions.toList()
    }

    /**
     * READ (By ID): Находит акцию по её уникальному идентификатору.
     * @param id Уникальный ID акции.
     * @return Найденный объект Promotion или null, если акция не найдена.
     */
    fun findById(id: String): Promotion? {
        return promotions.find { it.id == id }
    }

    /**
     * UPDATE: Обновляет существующую акцию.
     * @param id ID акции, которую нужно обновить.
     * @param updatedPromotion Объект с новыми данными.
     * @return Обновленный объект Promotion или null, если акция с таким ID не найдена.
     */
    fun update(id: String, updatedPromotion: Promotion): Promotion? {
        val existingPromotion = findById(id)
        if (existingPromotion != null) {
            existingPromotion.title = updatedPromotion.title
            existingPromotion.description = updatedPromotion.description
            existingPromotion.percentage = updatedPromotion.percentage
            return existingPromotion
        }
        return null // Акция не найдена
    }

    /**
     * DELETE: Удаляет акцию из репозитория по её ID.
     * @param id ID акции для удаления (тип String).
     * @return true, если удаление прошло успешно, false - если акция не была найдена.
     */
    fun delete(id: String): Boolean {
        return promotions.removeIf { it.id == id }
    }
}