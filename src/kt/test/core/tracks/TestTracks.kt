package kt.test.core.tracks

import kt.main.core.TProfile
import kt.main.core.Track
import kt.main.core.User
import kt.test.core.users.TestUserWithAllFields
import kt.test.core.users.TestUserWithRestoredId
import java.util.*
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

open class BaseTestTrackWithId(
    val name: String,
    val author: String?,
    val uploader: User,
    val uploadDate: Date,
    val genre: String?,
    val duration: Duration,
    id: UUID? = null,
) : Track(TProfile(
    name,
    author,
    uploader,
    uploadDate,
    genre,
    duration
), byteArrayOf(), id)

class TestTrackWithRestoredId : BaseTestTrackWithId (
    "Californication",
    "Red Hot Chili Peppers",
    TestUserWithRestoredId(),
    Date(2024, 11, 12),
    "Alternative",
    329.seconds,
    UUID.fromString("dd5e0c5f-0707-406e-b0e8-c51c6cfb3728")
)

class  TestTrackWithRandomId : BaseTestTrackWithId (
    "This War Is Ours",
    "Escape the Fate",
    TestUserWithAllFields(),
    Date(2024, 11, 12),
    "Metalcore",
    266.seconds
)