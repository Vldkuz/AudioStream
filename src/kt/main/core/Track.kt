package kt.main.core

import kotlinx.serialization.Serializable
import kt.main.infra.DateTimeSerializer
import kt.main.infra.DurationSerializer
import kt.main.infra.UUIDSerializer
import org.joda.time.DateTime
import java.time.Duration
import java.util.*

@Serializable
data class TProfile(
    val name: String,
    val author: String?,
    val uploader: User,
    @Serializable(with = DateTimeSerializer::class) val uploadDate: DateTime,
    val genre: String?,
    @Serializable(with = DurationSerializer::class) val duration: Duration
)

@Serializable
open class Track(
    val tProfile: TProfile,
    val data: ByteArray,
    @Serializable(with = UUIDSerializer::class) override val id: UUID = UUID.randomUUID(),
) : Entity()