package kt.test.core.rooms

import kt.main.core.RProfile
import kt.main.core.Room
import kt.main.core.Track
import kt.main.core.User
import kt.test.core.users.*
import java.util.*

open class BaseTestRoomsWithID(
    name: String,
    desc: String,
    participants: MutableSet<User>,
    tracks: Queue<Track>,
    id: UUID
) : Room(RProfile(name, desc), participants, tracks, id)

open class BaseTestRoomsWithoutID(
    name: String,
    desc: String,
    participants: MutableSet<User>,
    tracks: Queue<Track>,
) : BaseTestRoomsWithID(name, desc, participants, tracks, UUID.randomUUID())

class TestRoomWithRestoredID : BaseTestRoomsWithID(
    "CoolRoom",
    "We love pop and rock",
    mutableSetOf(
        TestUserWithAllFields() as User, TestUserWithRestoredId() as User, TestUserWithNullAge() as User,
        TestUserWithNullSecondName() as User, TestUserWithNullLastName() as User, TestUserWithNullAllFields() as User,
        TestUserWithUpperLogin() as User
    ),
    LinkedList(),
    UUID.fromString("9771a0b7-3827-475d-8b2e-be4af53ab84b")
)

class TestRoomWithRandomID : BaseTestRoomsWithoutID(
    "CoolRoom",
    "We love pop and rock",
    mutableSetOf(),
    LinkedList(),
)


