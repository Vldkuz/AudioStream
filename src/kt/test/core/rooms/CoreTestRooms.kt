package kt.test.core.rooms

import kt.main.core.Room
import kt.main.core.Track
import kt.test.core.tracks.TestTrackWithRandomId
import kt.test.core.tracks.TestTrackWithRestoredId
import kt.test.core.users.TestUserWithAllFields
import kt.test.core.users.TestUserWithRestoredId
import org.testng.Assert.*
import org.testng.annotations.Test
import java.util.UUID

class CoreTestRooms {
    @Test
    fun testRestoredId() {
        assertEquals(TestRoomWithRestoredID().id, UUID.fromString("9771a0b7-3827-475d-8b2e-be4af53ab84b"))
    }

    @Test
    fun testReflexiveEqual() {
        val room = TestRoomWithRandomID() as Room
        assertEquals(room, room)
    }

    @Test
    fun testIdentityByUUID() {
        val restoredIdRoom = TestRoomWithRestoredID() as Room
        val randomIdRoom = TestRoomWithRandomID() as Room
        assertFalse(restoredIdRoom == randomIdRoom)
    }

    @Test
    fun testSingleUserInserting() {
        val room = TestRoomWithRandomID() as Room
        room.participants.add(TestUserWithAllFields())
        assertEquals(room.participants.size, 1)
    }

    @Test
    fun testMultipleUserInserting() {
        val room = TestRoomWithRandomID() as Room
        for (i in 1 .. 10) {
            room.participants.add(TestUserWithAllFields())
        }
        assertEquals(room.participants.size, 10)
    }

    @Test
    fun testUserInsertingIdempotency() {
        val room = TestRoomWithRandomID() as Room
        for (i in 1 .. 10) {
            room.participants.add(TestUserWithRestoredId())
        }
        assertEquals(room.participants.size, 1)
    }

    @Test
    fun testUserRemoving() {
        val room = TestRoomWithRandomID() as Room
        room.participants.add(TestUserWithRestoredId())
        for (i in 1 .. 10) {
            room.participants.add(TestUserWithAllFields())
        }
        room.participants.remove(TestUserWithRestoredId())
        assertEquals(room.participants.size, 10)
        assertFalse(TestUserWithRestoredId() in room.participants)
    }

    @Test
    fun testTrackPushing() {
        val room = TestRoomWithRandomID() as Room
        room.trackQueue.add(TestTrackWithRestoredId() as Track)
        assertEquals(room.trackQueue.size, 1)
    }

    @Test
    fun testTrackRemoving() {
        val room = TestRoomWithRandomID() as Room
        room.trackQueue.add(TestTrackWithRestoredId() as Track)
        assertEquals(room.trackQueue.remove(), TestTrackWithRestoredId() as Track)
    }

    @Test
    fun testOrderPreservation() {
        val room = TestRoomWithRandomID() as Room
        val orderCertifier = List(10) { TestTrackWithRandomId() as Track }
        for (t in orderCertifier) {
            room.trackQueue.add(t)
        }
        val trackSequence = List(10) {room.trackQueue.remove()}
        assertEquals(orderCertifier, trackSequence)
    }
}