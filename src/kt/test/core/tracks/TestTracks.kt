package kt.test.core.tracks

import kt.main.core.TProfile
import kt.main.core.Track
import kt.main.core.User
import kt.test.core.users.TestUserWithAllFields
import kt.test.core.users.TestUserWithRestoredId
import org.joda.time.DateTime
import java.time.Duration
import java.util.*

open class BaseTestTrackWithId(
    name: String,
    author: String?,
    uploader: User,
    uploadDate: DateTime,
    genre: String?,
    duration: Duration,
    id: UUID,
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
    DateTime(),
    "Alternative",
    Duration.ofSeconds(30),
    UUID.fromString("dd5e0c5f-0707-406e-b0e8-c51c6cfb3728")
)

class  TestTrackWithRandomId : BaseTestTrackWithId (
    "This War Is Ours",
    "Escape the Fate",
    TestUserWithAllFields(),
    DateTime(),
    "Metalcore",
    Duration.ofSeconds(266),
    UUID.randomUUID()
)