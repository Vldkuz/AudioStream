package kt.test.nonMock

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import kotlinx.coroutines.runBlocking
import kt.main.core.Auth
import kt.main.core.UProfile
import kt.main.core.User
import kt.main.infra.AuthTable
import kt.main.infra.UserProfilesTable
import kt.main.infra.UsersTable
import kt.main.infra.repositories.UserRepository
import kt.test.fakers.FakeUserGenerator
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.testng.AssertJUnit.assertEquals
import org.testng.annotations.AfterClass
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test
import java.util.*


open class BaseInfraTest {
    private val user = System.getenv("TEST_USER")
    private val password = System.getenv("TEST_PASSWORD")
    private val dbUrl = System.getenv("TEST_DB_URL")

    protected val dbInstance = Database.connect(
        dbUrl, user = user, password = password,
    )

    private val httpClient = HttpClient(CIO)
}


class UserRepositoryTest : BaseInfraTest() {
    private var userRepository: UserRepository? = null
    private val countUsers = 10
    private val users = testUsersList(countUsers)

    private fun testUsersList(n: Int): List<User> {
        val list = LinkedList<User>()
        val faker = FakeUserGenerator()

        for (i in 0..n) {
            list.add(faker.createInstance())
        }

        return list
    }

    @BeforeClass
    fun setupDatabase() {
        transaction(dbInstance) {
            SchemaUtils.drop(UsersTable, UserProfilesTable, AuthTable)
            userRepository = UserRepository(dbInstance)
        }


        runBlocking {
            for (user in users) {
                userRepository!!.add(user)
            }
        }
    }

    @Test
    fun testGetAll() {
        val allUsers = runBlocking {
            userRepository!!.getAll()
        }


        assertEquals(allUsers, users)
    }

    @Test
    fun testGetByName() {
        val firstName = users[0].uProfile.firstName
        val usersWithFirstName = users.filter { it.uProfile.firstName == firstName }

        val usersInDB =  runBlocking {
            userRepository!!.getByName(firstName)
        }

        assertEquals(usersWithFirstName, usersInDB)
    }


    @Test
    fun testGetById() {
        val user = users[0]
        val id = user.id

        val userInDb = runBlocking {
            userRepository!!.getById(id)
        }


        assertEquals(user, userInDb)
    }


    @Test
    fun testUpdate() {
        val id = users[0].id

        val rProfile = UProfile(
            "Biba"
        )

        val auth = Auth(
            "loginov",
            "Biba023543"
        )

        val user = User(rProfile, auth, id)

        runBlocking {
            userRepository!!.update(user)
        }

        val userInDb = runBlocking {
            userRepository!!.getById(id)
        }

        assertEquals(user, userInDb)
    }

    @Test
    fun testRemove() {
        runBlocking {
            val deleteUser = users[1]
            val id = deleteUser.id
            userRepository!!.remove(deleteUser)
            assertEquals(null, userRepository!!.getById(id))
        }
    }
}

//TODO("Надо тесты еще дописать на TrackRepository, RoomRepository")