package kt.main.core
import kotlinx.serialization.Serializable
import kt.main.infra.UUIDSerializer
import java.util.*

@Serializable
data class RProfile(
    val name: String,
    val description: String?
)

@Serializable
open class Room(
    val rProfile: RProfile,
    val participants: MutableSet<User> = mutableSetOf(),
    val trackQueue: Queue<Track> = LinkedList(),
    @Serializable(with = UUIDSerializer::class) override val id: UUID = UUID.randomUUID(),
) : Entity()

