package kt.test.fakers

import kt.main.core.Auth
import kt.main.core.UProfile
import kt.main.core.User
import kt.test.core.InstanceGenerator

class FakeUserGenerator : InstanceGenerator<User>, BaseFakeGenerator<User>() {
    override fun createInstance(): User {
        return User(
            uProfile = UProfile(
                faker.name().firstName(),
                faker.name().lastName(),
                faker.name().nameWithMiddle(),
                faker.number().numberBetween(1, 100)
            ),
            Auth(faker.name().username(), faker.number().digits(12).toString())
        )
    }
}