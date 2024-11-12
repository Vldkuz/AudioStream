package kt.main.core

import java.util.*
import kotlin.time.Duration

data class TProfile(
    val name: String,
    val author: String?,
    val uploader: User,
    val uploadDate: Date,
    val genre: String?,
    val duration: Duration
)

open class Track(
    val tProfile: TProfile,
    val data: ByteArray,
    id: UUID? = null,
) : Entity(id)