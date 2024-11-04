package kt.main.Core

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

class Track(
    val tProfile: TProfile,
    val data: ByteArray,
) : Entity()