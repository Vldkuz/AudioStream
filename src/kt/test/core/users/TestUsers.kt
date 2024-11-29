package kt.test.core.users

import kt.main.core.Auth
import kt.main.core.UProfile
import kt.main.core.User
import java.security.MessageDigest
import java.util.*

object TestCryptWrapper {
    private val cryptPasswords = MessageDigest.getInstance("MD5")

    @OptIn(ExperimentalStdlibApi::class)
    fun digest(bytes: ByteArray): String {
        cryptPasswords.reset()
        return cryptPasswords.digest(bytes).toHexString()
    }

}


open class BaseTestUserWithID(
    firstName: String,
    secondName: String?,
    lastName: String?,
    age: Int?,
    login: String,
    password: String,
    id: UUID
) : User(
    UProfile(firstName, secondName, lastName, age),
    Auth(
        login,
        TestCryptWrapper.digest((login + password + firstName).encodeToByteArray())
    ),
    id
)

open class BaseTestUserWithoutID(
    firstName: String,
    secondName: String?,
    lastName: String?,
    age: Int?,
    login: String,
    password: String,
) : BaseTestUserWithID(firstName, secondName, lastName, age, login, password, UUID.randomUUID())

class TestUserWithRestoredId : BaseTestUserWithID(
    "VovanWithGlasses",
    "Biba",
    "Popovich",
    22,
    "Popovka",
    "PopovkaTheBestPipka",
    UUID.fromString("1c05c129-bfa2-466c-9b4c-896237749385")
)


class TestUserWithAllFields : BaseTestUserWithoutID(
    "Vovan",
    "Sidorov",
    "Petrovich",
    22,
    "sidoroff",
    "SecurePassword"
)

class TestUserWithNullSecondName : BaseTestUserWithoutID(
    "Petr",
    null,
    "Vladimirovich",
    23,
    "petrr",
    "SecurePasswordVovan"
)

class TestUserWithNullLastName : BaseTestUserWithoutID(
    "Pavel",
    "Grafanya",
    null,
    28,
    "pavelDurovYa",
    "SecurePasswordPetr"
)

class TestUserWithNullAge : BaseTestUserWithoutID(
    "Purp",
    "Jebko",
    "Valentinovich",
    null,
    "JebkoTheBest",
    "SecurePasswordJebko"
)

class TestUserWithNullAllFields : BaseTestUserWithoutID(
    "Vanya",
    null,
    null,
    null,
    "VanyaPupZemli",
    "SecurePasswordVanyaPupZemli"
)

class TestUserWithUpperLogin : BaseTestUserWithoutID(
    "Vanya",
    "Carlos",
    "Bombovich",
    22,
    "UPPERCASELOGIN",
    "SecurePasswordUpperCase"
)

