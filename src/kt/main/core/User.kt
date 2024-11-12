package kt.main.core

import java.util.*


data class UProfile(
    val firstName: String,
    val secondName: String? = null,
    val lastName: String? = null,
    val age: Int? = null
)


data class Auth(
    val login: String,
    val hashPass: String
)


open class User(
    val uProfile: UProfile,
    val auth: Auth,
    id: UUID? = null
) : Entity(id)
