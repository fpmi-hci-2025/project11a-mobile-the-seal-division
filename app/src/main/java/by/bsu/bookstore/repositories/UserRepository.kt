package by.bsu.bookstore.repositories

import by.bsu.bookstore.User
import java.util.Collections.max

object UserRepository {
    val users: MutableList<User> = mutableListOf(
        User(1, "Иван", "Иванов", "ivan@ivanov.com"),
        User(2, "Пётр", "Петров", "petr@petrov.com"),
        User(3, "Игорь", "Сидоров", "igor@sidorov.com")
    )

    fun createUser(user: User): User {
        users.add(user)
        return user
    }

    fun getNewId(): Int{
        if (getAllIds().isNotEmpty()){
            return max(getAllIds()) + 1
        }
        else return 1;
    }

    fun getUserById(id: Int): User? {
        return users.find { it.userId == id }
    }

    fun getUserByEmail(email: String): User? {
        return users.find { it.email == email }
    }

    fun getAllIds(): List<Int> {
        val ids : MutableList<Int> = mutableListOf()
        for (u in users){
            ids.add(u.userId)
        }
        return ids
    }

    fun getAllEmails(): List<String> {
        val  emails : MutableList<String> = mutableListOf()
        for (u in users){
            emails.add(u.email)
        }
        return emails
    }

    fun getAllUsers(): List<User> {
        return users
    }

    fun updateUser(id: Int, updatedUser: User): Boolean {
        val userIndex = users.indexOfFirst { it.userId == id }
        if (userIndex != -1) {
            users[userIndex] = updatedUser
            return true
        }
        return false
    }

    fun deleteUser(id: Int): Boolean {
        return users.removeIf { it.userId == id }
    }
}

