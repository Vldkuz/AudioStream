package kt.main.core
import java.util.*

data class RProfile(
    val name: String,
    val description: String?
)


open class Room(
    val rProfile: RProfile,
    val participants: MutableSet<User> = mutableSetOf(),
    val trackQueue: Queue<Track> = LinkedList(),
    id: UUID? = null
) : Entity(id)
