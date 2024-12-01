package kt.main.core

import kotlinx.serialization.Required
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kt.main.infra.UUIDSerializer
import java.util.*

@Serializable
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

// Сделано для сериализации, но при передаче в Auth null выплюнет исключение, таким образом избавляемся от возможных бекдоров, где можно отдать хеш пароля и сохраняем сериализацию

// Здесь нужен свой сериализатор, поскольку нужно не дать сериализовать объект auth, но десериализовывать его нужно всегда.
@Serializable
open class User(
    val uProfile: UProfile,
    @Required @Transient val auth: Auth? = null,
    @Serializable(with = UUIDSerializer::class) final override val id: UUID = UUID.randomUUID(),
) : Entity()
{
    init {
        if (auth == null) {
           throw AuthNullError("Auth field cannot be null: Object_id: $id\nAuthField: $auth\nAuthField: $auth")
        }
    }
}
