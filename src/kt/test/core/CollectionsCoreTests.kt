package kt.test.core

import kt.main.core.Room
import kt.main.core.Track
import kt.main.core.User
import kt.test.fakers.FakeRoomGenerator
import kt.test.fakers.FakeTrackGenerator
import kt.test.fakers.FakeUserGenerator
import org.testng.annotations.Test
import java.util.*

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