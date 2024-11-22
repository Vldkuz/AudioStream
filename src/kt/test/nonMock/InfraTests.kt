package kt.test.nonMock

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.util.*
import kotlinx.coroutines.runBlocking
import kt.main.core.*
import kt.main.infra.*
import kt.main.infra.repositories.RoomRepository
import kt.main.infra.repositories.TrackRepository
import kt.main.infra.repositories.UserRepository
import kt.test.fakers.FakeRoomGenerator
import kt.test.fakers.FakeTrackGenerator
import kt.test.fakers.FakeUserGenerator
import kt.webDav.YandexFileManager
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.testng.AssertJUnit.assertEquals
import org.testng.AssertJUnit.assertNull
import org.testng.annotations.BeforeClass
import org.testng.annotations.BeforeTest
import org.testng.annotations.Test
import java.util.*


open class BaseInfraTest {
    private val dbUser = System.getenv("TEST_USER") // TODO(Надо в конфиг файл унести вот это)
    private val dbPassword = System.getenv("TEST_PASSWORD")
    private val dbUrl = System.getenv("TEST_DB_URL")

    protected val dbInstance = Database.connect(
        dbUrl, user = dbUser, password = dbPassword,
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
class TrackRepositoryTest : BaseInfraTest() {
    private val webdavPassword = System.getenv("TEST_WEBDAV_PASSWORD")
    private val webdavLogin = System.getenv("TEST_WEBDAV_LOGIN")
    private val token = "Basic " + "$webdavLogin:$webdavPassword".encodeBase64()

    private var userRepository: UserRepository? = null
    private var trackRepository: TrackRepository? = null

    private val testDataCounter = 5
    private val testData = testTracksAndUsers(testDataCounter)
    private val testTracks = testData.first
    private val testUsers = testData.second


    private fun testTracksAndUsers(n: Int): Pair<List<Track>, List<User>> {
        val faker = FakeTrackGenerator()
        val users = LinkedList<User>()
        val tracks = LinkedList<Track>()

        for (i in 0..n) {
            val fakeTrack = faker.createInstance()
            val fakeUser = fakeTrack.tProfile.uploader

            tracks.add(fakeTrack)
            users.add(fakeUser)
        }

        return Pair(tracks, users)
    }

    @BeforeTest
    fun setup() {
        transaction(dbInstance) {
            SchemaUtils.drop(TrackTable, TrackProfilesTable)
            SchemaUtils.drop(UsersTable, UserProfilesTable, AuthTable)
            userRepository = UserRepository(dbInstance)
            trackRepository = TrackRepository(
                dbInstance,
                YandexFileManager(token, HttpClient(CIO)),
                userRepository!!
            )
        }

        runBlocking {
            for (user in testUsers) {
                userRepository!!.add(user)
            }
        }

        runBlocking {
            for (track in testTracks) {
                trackRepository!!.add(track)
            }
        }
    }

    @Test
    fun testGetAll() {
        val allTracks = runBlocking {
            trackRepository!!.getAll()
        }

        println(allTracks.map { it -> it.id })
        println(testTracks.map { it -> it.id })

        assertEquals(allTracks, testTracks)
    }

    @Test
    fun testGetByName() {
        val trackName = testTracks[0].tProfile.name
        val tracksWithName = testTracks.filter { it.tProfile.name == trackName }

        val tracksInDB =  runBlocking {
            trackRepository!!.getByName(trackName)
        }

        assertEquals(tracksWithName, tracksInDB)
    }

    @Test
    fun testGetById() {
        val track = testTracks[0]
        val id = track.id

        val trackInDb = runBlocking {
            trackRepository!!.getById(id)
        }

        assertEquals(track, trackInDb)
    }

    @Test
    fun testUpdate() {
        val trackId = testTracks[1].id
        val tProfile = TProfile (
            "joskij move",
            "slavik_1994",
            testTracks[1].tProfile.uploader,
            testTracks[1].tProfile.uploadDate,
            null,
            testTracks[1].tProfile.duration
        )

        val track = Track(tProfile, byteArrayOf(1, 2, 3, 4, 5), trackId)

        runBlocking {
            trackRepository!!.update(track)
        }

        val trackInDb = runBlocking {
            trackRepository!!.getById(trackId)
        }

        assertEquals(track, trackInDb)
    }

    @Test
    fun testRemove() {
        val track = testTracks[0]

        runBlocking {
            trackRepository!!.remove(track)
        }

        val trackInDb = runBlocking {
            trackRepository!!.getById(track.id)
        }

        assertNull(trackInDb)
    }
}

//TODO(Пофиксить кейсы в testUpdate, testGetById и testRemove)

class RoomRepositoryTest : BaseInfraTest() {
    private var roomRepository: RoomRepository? = null
    private val testRoomsCount = 10
    private val rooms = testRoomsList(testRoomsCount)


    private fun testRoomsList(n: Int): List<Room> {
        val rooms = LinkedList<Room>()
        val faker = FakeRoomGenerator()

        for (i in 0..n) {
            rooms.add(faker.createInstance())
        }
        return rooms
    }

    @BeforeTest
    fun setupDatabase() {
        transaction(dbInstance) {
        }
    }
}

//TODO(Дописать RoomRepositoryTest)