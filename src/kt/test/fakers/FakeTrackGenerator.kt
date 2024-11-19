package kt.test.fakers

import kt.main.core.TProfile
import kt.main.core.Track
import kt.test.core.InstanceGenerator
import org.joda.time.DateTime

class FakeTrackGenerator : BaseFakeGenerator<Track>(), InstanceGenerator<Track> {
    companion object UserGenerator {
        val userGen = FakeUserGenerator()
    }

    override fun createInstance(): Track {
        return Track(
            TProfile(
                faker.music().key(),
                uploader = userGen.createInstance(),
                uploadDate = DateTime(),
                duration = faker.duration().atMostSeconds(50),
                author = faker.name().fullName(),
                genre = faker.music().genre()
            ),
            data = byteArrayOf()
        )
    }

}