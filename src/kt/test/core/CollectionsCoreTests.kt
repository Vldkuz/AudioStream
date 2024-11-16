package kt.test.core

import kt.main.core.*
import net.datafaker.Faker
import org.testng.annotations.Test
import java.util.*
import kotlin.time.toKotlinDuration

abstract class BaseFakeGenerator<T> : InstanceGenerator<T> {
    companion object FakeFactory {
        val faker = Faker()
    }
}

class FakeUserGenerator : InstanceGenerator<User>, BaseFakeGenerator<User>() {
    override fun createInstance(): User {
        return User(
            uProfile = UProfile(
                faker.name().firstName(),
                faker.name().lastName(),
                faker.name().nameWithMiddle(),
                faker.number().numberBetween(1, 100)
            ),
            Auth(faker.name().username(), faker.number().digits(12).toString())
        )
    }
}

class FakeRoomGenerator : BaseFakeGenerator<Room>(), InstanceGenerator<Room> {
    override fun createInstance(): Room {
        return Room(
            RProfile(faker.name().firstName(), faker.eldenRing().location()),
        )
    }
}

class FakeTrackGenerator : BaseFakeGenerator<Track>(), InstanceGenerator<Track> {
    companion object UserGenerator {
        val userGen = FakeUserGenerator()
    }

    override fun createInstance(): Track {
        return Track(
            TProfile(
                faker.music().key(),
                uploader = userGen.createInstance(),
                uploadDate = Date(),
                duration = faker.duration().atMostSeconds(50).toKotlinDuration(),
                author = faker.name().fullName(),
                genre = faker.music().genre()
            ),
            data = byteArrayOf()
        )
    }

}

class CollectionsUserTests : CollectionsBaseTest<User>(FakeUserGenerator()) {
    @Test
    fun testListCollection() {
        val list = LinkedList<User>()
        testAddToCollection(list)
        testRemoveFromCollection(list)
    }

    @Test
    fun testArrayCollection() {
        val list = ArrayList<User>()
        testAddToCollection(list)
        testRemoveFromCollection(list)
    }

    @Test
    fun testHashSetCollection() {
        val set = HashSet<User>()
        testHashCollections(set)
    }

    @Test
    fun testHashMapCollection() {
        val map = HashMap<User, Int>()
        testMapCollection(map)
    }

    @Test
    fun testReverseMapCollection() {
        val map = HashMap<Int, User>()
        testReverseMap(map)
    }
}

class CollectionsTracksTests : CollectionsBaseTest<Track>(FakeTrackGenerator()) {
    @Test
    fun testListCollection() {
        val list = LinkedList<Track>()
        testAddToCollection(list)
        testRemoveFromCollection(list)
    }

    @Test
    fun testArrayCollection() {
        val list = ArrayList<Track>()
        testAddToCollection(list)
        testRemoveFromCollection(list)
    }

    @Test
    fun testHashSetCollection() {
        val set = HashSet<Track>()
        testHashCollections(set)
    }

    @Test
    fun testHashMapCollection() {
        val map = HashMap<Track, Int>()
        testMapCollection(map)
    }

    @Test
    fun testReverseMapCollection() {
        val map = HashMap<Int, Track>()
        testReverseMap(map)
    }
}

class CollectionsRoomTests : CollectionsBaseTest<Room>(FakeRoomGenerator()) {
    @Test
    fun testListCollection() {
        val list = LinkedList<Room>()
        testAddToCollection(list)
        testRemoveFromCollection(list)
    }

    @Test
    fun testArrayCollection() {
        val list = ArrayList<Room>()
        testAddToCollection(list)
        testRemoveFromCollection(list)
    }

    @Test
    fun testHashSetCollection() {
        val set = HashSet<Room>()
        testHashCollections(set)
    }

    @Test
    fun testHashMapCollection() {
        val map = HashMap<Room, Int>()
        testMapCollection(map)
    }

    @Test
    fun testReverseMapCollection() {
        val map = HashMap<Int, Room>()
        testReverseMap(map)
    }
}