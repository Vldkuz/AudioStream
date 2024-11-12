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
    id: UUID? = null
) : Room(RProfile(name, desc), participants, tracks, id)

open class BaseTestRoomsWithoutID(
    name: String,
    desc: String,
    participants: MutableSet<User>,
    tracks: Queue<Track>,
) : BaseTestRoomsWithID(name, desc, participants, tracks, null)

sealed class TestRoomWithRestoredID : BaseTestRoomsWithID(
    "CoolRoom",
    "We love pop and rock",
    mutableSetOf(
        TestUserWithAllFields() as User, TestUserWithRestoredId() as User, TestUserWithNullAge() as User,
        TestUserWithNullSecondName() as User, TestUserWithNullLastName() as User, TestUserWithNullAllFields() as User,
        TestUserWithUpperLogin() as User
    ),
    LinkedList()
)

