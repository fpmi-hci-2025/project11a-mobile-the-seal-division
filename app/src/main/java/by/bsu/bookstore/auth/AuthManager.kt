package by.bsu.bookstore.auth

import android.content.Context
import android.content.SharedPreferences
import by.bsu.bookstore.model.User

object AuthManager {
    private const val PREF = "auth_prefs"

    private const val KEY_LOGGED = "is_logged"
    private const val KEY_ID = "user_id"
    private const val KEY_EMAIL = "user_email"
    private const val KEY_NAME = "user_name"
    private const val KEY_LAST_NAME = "user_last_name"
    private const val KEY_PHONE = "user_phone"
    private const val KEY_ADDRESS = "user_address"
    private const val KEY_ROLE = "user_role"
    private const val KEY_REG_DATE = "user_reg_date"

    private lateinit var sp: SharedPreferences

    fun init(context: Context) {
        sp = context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
    }

    fun isLogged(): Boolean = this::sp.isInitialized && sp.getBoolean(KEY_LOGGED, false)

    /**
     * Сохраняет все данные пользователя в SharedPreferences.
     * Если какое-то поле в объекте User равно null, оно не будет записано.
     */
    fun login(context: Context, user: User) {
        if (user.id == null) return
        if (!this::sp.isInitialized) init(context)

        sp.edit().apply {
            putBoolean(KEY_LOGGED, true)
            putInt(KEY_ID, user.id)
            putString(KEY_EMAIL, user.email)
            putString(KEY_NAME, user.firstName)
            putString(KEY_LAST_NAME, user.lastName)

            user.phone?.let { putString(KEY_PHONE, it) }
            user.address?.let { putString(KEY_ADDRESS, it) }
            putString(KEY_ROLE, user.role)
            user.regDate?.let { putString(KEY_REG_DATE, it) }

            apply()
        }
    }

    /**
     * Очищает все сохраненные данные пользователя.
     */
    fun logout() {
        if (!this::sp.isInitialized) return
        sp.edit().clear().apply()
    }

    fun currentUserEmail(): String? = if (this::sp.isInitialized) sp.getString(KEY_EMAIL, null) else null
    fun getCurrentUserLastName(): String? = if (this::sp.isInitialized) sp.getString(KEY_LAST_NAME, null) else null
    fun getCurrentUserName(): String? = if (this::sp.isInitialized) sp.getString(KEY_NAME, null) else null
    fun getCurrentUserPhone(): String? = if (this::sp.isInitialized) sp.getString(KEY_PHONE, null) else null
    fun getCurrentUserAddress(): String? = if (this::sp.isInitialized) sp.getString(KEY_ADDRESS, null) else null

    fun getCurrentUserId(): Int? {
        if (!this::sp.isInitialized || !isLogged()) return null
        val id = sp.getInt(KEY_ID, -1)
        return if (id == -1) null else id
    }

    /**
     * Собирает все данные о текущем пользователе из SharedPreferences и возвращает объект User.
     * @return Объект User, если пользователь залогинен, иначе null.
     */
    fun getCurrentUser(): User? {
        if (!isLogged()) {
            return null
        }

        val id = getCurrentUserId()
        val email = currentUserEmail()
        val firstName = getCurrentUserName()
        val lastName = getCurrentUserLastName()

        if (id == null || email == null || firstName == null || lastName == null) {
            return null
        }

        return sp.getString(KEY_ROLE, "Пользователь")?.let {
            User(
                id = id,
                email = email,
                firstName = firstName,
                lastName = lastName,
                phone = sp.getString(KEY_PHONE, null),
                address = sp.getString(KEY_ADDRESS, null),
                role = it,
                regDate = sp.getString(KEY_REG_DATE, null),
                password = null
            )
        }
    }
}