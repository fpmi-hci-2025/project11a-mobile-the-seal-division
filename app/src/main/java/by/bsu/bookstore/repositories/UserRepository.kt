package by.bsu.bookstore.repositories

import by.bsu.bookstore.model.User
import java.util.Date

object UserRepository {
    val users: MutableList<User> = mutableListOf(
        User(id = 1, firstName = "Иван", lastName = "Иванов", email = "ivan@ivanov.com", regDate = Date(), password = "123123"),
        User(id = 2, firstName = "Пётр", lastName = "Петров", email = "petr@petrov.com", regDate = Date(), password = "111111"),
        User(id = 3, firstName = "Игорь", lastName = "Сидоров", email = "igor@sidorov.com", regDate = Date(), password = "222222"),
        User(id = 4, firstName = "Админ", lastName = "", email = "admin@mir-knig.by", regDate = Date(), password = "123456")
    )

    /**
     * CREATE: Добавляет нового пользователя в репозиторий.
     * Если у пользователя id = 0, присваивает ему новый уникальный id.
     */
    fun createUser(user: User): User {
        val newUser = if (user.id == 0) {
            user.copy(id = getNewId(), regDate = Date())
        } else {
            user
        }
        users.add(newUser)
        return newUser
    }

    /**
     * Генерирует новый уникальный ID для пользователя.
     */
    fun getNewId(): Int {
        val maxId = users.maxByOrNull { it.id }?.id ?: 0
        return maxId + 1
    }

    /**
     * READ: Находит пользователя по его ID.
     */
    fun getUserById(id: Int): User? {
        return users.find { it.id == id }
    }

    /**
     * READ: Находит ID пользователя по его email (без учета регистра).
     */
    fun getIdByEmail(email: String): Int {
        val usr = users.find { it.email.equals(email, ignoreCase = true) }
        if (usr != null) {
            return usr.id
        }
        return -1
    }

    /**
     * READ: Находит пользователя по его email (без учета регистра).
     */
    fun getUserByEmail(email: String): User? {
        return users.find { it.email.equals(email, ignoreCase = true) }
    }

    /**
     * READ: Возвращает список всех пользователей.
     */
    fun getAllUsers(): List<User> {
        return users.toList() // Возвращаем копию для безопасности
    }

    /**
     * READ: Возвращает список всех email.
     */
    fun getAllEmails(): List<String> {
        return users.map { it.email }
    }

    /**
     * UPDATE: Обновляет существующего пользователя по его ID.
     */
    fun updateUser(id: Int, updatedUser: User): Boolean {
        val userIndex = users.indexOfFirst { it.id == id }
        if (userIndex != -1) {
            val originalUser = users[userIndex]
            users[userIndex] = updatedUser.copy(id = id, regDate = originalUser.regDate)
            return true
        }
        return false
    }

    /**
     * DELETE: Удаляет пользователя по его ID.
     */
    fun deleteUser(id: Int): Boolean {
        return users.removeIf { it.id == id }
    }
}