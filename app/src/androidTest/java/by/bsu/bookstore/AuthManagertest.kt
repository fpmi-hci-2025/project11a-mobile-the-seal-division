package by.bsu.bookstore

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import by.bsu.bookstore.auth.AuthManager
import by.bsu.bookstore.model.User
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AuthManagerTest {

    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    @Before
    fun setup() {
        AuthManager.init(context)
        AuthManager.logout()
    }

    @After
    fun teardown() {
        AuthManager.logout()
    }

    @Test
    fun loginAndGetCurrentUser_savesAndRetrievesCorrectly() {
        val testUser = User(
            id = 123,
            firstName = "John",
            lastName = "Doe",
            email = "john.doe@example.com",
            phone = "123456789",
            address = "123 Test St",
            role = "user",
            regDate = "2025-01-01"
        )

        AuthManager.login(context, testUser)
        val retrievedUser = AuthManager.getCurrentUser()

        assertNotNull("Retrieved user should not be null", retrievedUser)
        assertEquals("User ID should match", testUser.id, retrievedUser?.id)
        assertEquals("First name should match", testUser.firstName, retrievedUser?.firstName)
        assertEquals("Email should match", testUser.email, retrievedUser?.email)
        assertEquals("Phone should match", testUser.phone, retrievedUser?.phone)
    }

    @Test
    fun logout_clearsUserData() {
        val testUser = User(id = 456, firstName = "Jane", lastName = "Smith", email = "jane@test.com")
        AuthManager.login(context, testUser)

        assertTrue("User should be logged in after login", AuthManager.isLogged())

        AuthManager.logout()

        assertFalse("User should be logged out after logout", AuthManager.isLogged())
        assertNull("Current user should be null after logout", AuthManager.getCurrentUser())
    }
}