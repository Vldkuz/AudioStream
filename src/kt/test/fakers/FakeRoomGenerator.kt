package kt.test.fakers

import kt.main.core.RProfile
import kt.main.core.Room
import kt.test.core.InstanceGenerator

class FakeRoomGenerator : BaseFakeGenerator<Room>(), InstanceGenerator<Room> {
    override fun createInstance(): Room {
        return Room(
            RProfile(faker.name().firstName(), faker.eldenRing().location()),
        )
    }
}