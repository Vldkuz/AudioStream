package kt.main.core


data class UProfile(
    val firstName: String,
    val secondName: String?,
    val lastName: String?,
    val age: Int?
)


data class Auth(
    val login: String,
    val hashPass: String
)


class User(
    val uProfile: UProfile,
    val auth: Auth
) : Entity()
