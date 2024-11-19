package kt.test.fakers

import kt.test.core.InstanceGenerator
import net.datafaker.Faker

abstract class BaseFakeGenerator<T> : InstanceGenerator<T> {
    companion object FakeFactory {
        val faker = Faker()
    }
}


