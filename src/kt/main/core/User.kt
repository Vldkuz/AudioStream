package kt.main.core

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

@Serializable
open class User(
    val uProfile: UProfile,
    @Transient val auth: Auth? = null, //TODO("Подумать, как сделать, чтобы нельзя было передавать null, но сохранить сериализацию (нужно дефолтное значение)")
    @Serializable(with = UUIDSerializer::class) override val id: UUID = UUID.randomUUID(),
) : Entity()
