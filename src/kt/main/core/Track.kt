package kt.main.core

import org.joda.time.DateTime
import java.time.Duration
import java.util.*

data class TProfile(
    val name: String,
    val author: String?,
    val uploader: User,
    val uploadDate: DateTime,
    val genre: String?,
    val duration: Duration
)

open class Track(
    val tProfile: TProfile,
    val data: ByteArray,
    id: UUID? = null,
) : Entity(id)