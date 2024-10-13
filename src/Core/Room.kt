package Core

import java.util.*


data class RProfile(
    val name: String,
    val description: String
)


class Room(
    val rProfile: RProfile,
    val participants: MutableSet<User> = mutableSetOf(),
    val trackQueue: Queue<Track> = LinkedList(),
) : Entity()

