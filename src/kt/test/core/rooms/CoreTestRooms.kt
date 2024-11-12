package kt.test.core.rooms

import kt.main.core.Room
import kt.test.core.users.TestUserWithAllFields
import kt.test.core.users.TestUserWithRestoredId
import org.testng.Assert.assertEquals
import org.testng.Assert.assertFalse
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
        for (i in 1..10) {
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
}