package kt.test.core

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kt.main.core.Room
import kt.main.core.Track
import kt.main.core.User
import kt.test.core.rooms.BaseTestRoomsWithID
import kt.test.core.tracks.BaseTestTrackWithId
import kt.test.core.users.TestUserWithRestoredId
import org.joda.time.DateTime
import org.testng.AssertJUnit.assertEquals
import org.testng.annotations.Test
import java.time.Duration
import java.util.*

// Здесь нужно класс с полями, в которых не генерится рандомный UUID

class TestRoomForSerialization():
    BaseTestRoomsWithID(
        "CoolRoom",
        "We love pop and rock",
        mutableSetOf(
            TestUserWithRestoredId(), TestUserWithRestoredId(),
        ),
        LinkedList(),
        UUID.fromString("9771a0b7-3827-475d-8b2e-be4af53ab84b"))

class TestTrackForSerialization():
    BaseTestTrackWithId(
        "Californication",
        "Red Hot Chili Peppers",
        TestUserWithRestoredId(),
        DateTime(2024, 11, 12, 13, 11),
        "Alternative",
        Duration.ofSeconds(30),
        UUID.fromString("dd5e0c5f-0707-406e-b0e8-c51c6cfb3728")
    )


class SerializationRoom {
    private val jsonString = """{"rProfile":{"name":"CoolRoom","description":"We love pop and rock"},"participants":[{"uProfile":{"firstName":"VovanWithGlasses","secondName":"Biba","lastName":"Popovich","age":22},"id":"1c05c129-bfa2-466c-9b4c-896237749385"}],"id":"9771a0b7-3827-475d-8b2e-be4af53ab84b"}"""
    private val jsonDeserialization = """{"rProfile":{"name":"CoolRoom","description":"We love pop and rock"},"participants":[{"uProfile":{"firstName":"VovanWithGlasses","secondName":"Biba","lastName":"Popovich","age":22},"auth":{"login":"Popovka","hashPass":"c2e55e75e592023cdcbe029816c14e07"},"id":"1c05c129-bfa2-466c-9b4c-896237749385"}],"id":"9771a0b7-3827-475d-8b2e-be4af53ab84b"}"""

    @Test
    fun testRoomSerialization() {
        val testRoomsWithID = TestRoomForSerialization() as Room
        val json = Json.encodeToString(testRoomsWithID)
        assertEquals(jsonString, json)
    }

    @Test
    fun testRoomDeserialization() {
        assertEquals(Json.decodeFromString<Room>(jsonDeserialization), TestRoomForSerialization())
    }
}


class SerializationTrack {
    private val jsonString = """{"tProfile":{"name":"Californication","author":"Red Hot Chili Peppers","uploader":{"uProfile":{"firstName":"VovanWithGlasses","secondName":"Biba","lastName":"Popovich","age":22},"id":"1c05c129-bfa2-466c-9b4c-896237749385"},"uploadDate":"2024-11-12T13:11:00.000+05:00","genre":"Alternative","duration":"PT30S"},"data":[],"id":"dd5e0c5f-0707-406e-b0e8-c51c6cfb3728"}"""
    private val jsonDeserialization = """{"tProfile":{"name":"Californication","author":"Red Hot Chili Peppers","uploader":{"uProfile":{"firstName":"VovanWithGlasses","secondName":"Biba","lastName":"Popovich","age":22}, "auth":{"login":"Popovka","hashPass":"c2e55e75e592023cdcbe029816c14e07"}},"uploadDate":"2024-11-12T13:11:00.000+05:00","genre":"Alternative","duration":"PT30S"},"data":[]}"""

    @Test
    fun testTrackSerialization() {
        val testTrack = TestTrackForSerialization() as Track
        val json = Json.encodeToString(testTrack)
        assertEquals(jsonString, json)
    }

    @Test
    fun testTrackDeserialization() {
        assertEquals(Json.decodeFromString<Track>(jsonDeserialization), TestTrackForSerialization())
    }
}

class SerializerUser {
    private val jsonString = """{"uProfile":{"firstName":"VovanWithGlasses","secondName":"Biba","lastName":"Popovich","age":22},"id":"1c05c129-bfa2-466c-9b4c-896237749385"}"""
    private val jsonDeserialization = """{"uProfile":{"firstName":"VovanWithGlasses","secondName":"Biba","lastName":"Popovich","age": 22},"auth":{"login":"Popovka","hashPass":"c2e55e75e592023cdcbe029816c14e07"}}"""

    @Test
    fun testUserSerialization() {
        val testUser = TestUserWithRestoredId() as User
        val json = Json.encodeToString(testUser)
        assertEquals(jsonString, json)
    }

    @Test
    fun testUserDeserialization() {
        val testUser = Json.decodeFromString<User>(jsonDeserialization)
        assertEquals(testUser, TestUserWithRestoredId())
    }

}