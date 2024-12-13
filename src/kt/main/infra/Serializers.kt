package kt.main.infra

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kt.main.core.Auth
import kt.main.core.UProfile
import kt.main.core.User
import org.joda.time.DateTime
import java.time.Duration
import java.util.*

@Serializer(forClass = UUID::class)
object UUIDSerializer : KSerializer<UUID> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("UUID", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): UUID {
        return UUID.fromString(decoder.decodeString())
    }

    override fun serialize(encoder: Encoder, value: UUID) {
        return encoder.encodeString(value.toString())
    }

}

@Serializer(forClass = DateTime::class)
object DateTimeSerializer : KSerializer<DateTime> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("DateTime", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): DateTime {
        return DateTime.parse(decoder.decodeString())
    }

    override fun serialize(encoder: Encoder, value: DateTime) {
        return encoder.encodeString(value.toString())
    }
}

@Serializer(forClass = Duration::class)
object DurationSerializer : KSerializer<Duration> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Duration", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): Duration {
        return Duration.parse(decoder.decodeString())
    }

    override fun serialize(encoder: Encoder, value: Duration) {
        return encoder.encodeString(value.toString())
    }
}

@Serializer(forClass = User::class)
object UserSerializer : KSerializer<User> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("User") {
        element<UProfile>("uProfile")
        element<String>("id", isOptional = true)
        element<Auth>("auth")
    }

    override fun deserialize(decoder: Decoder): User {
        val input = decoder.beginStructure(descriptor)

        var uProfile: UProfile? = null
        var id: UUID? = null
        var auth: Auth? = null

        while (true) {
            when (val index = input.decodeElementIndex(descriptor)) {
                0 -> uProfile = input.decodeSerializableElement(descriptor, 0, UProfile.serializer())
                2 -> auth = input.decodeSerializableElement(descriptor, 2, Auth.serializer())
                CompositeDecoder.DECODE_DONE -> break
            }
        }

        input.endStructure(descriptor)

        return User(
            uProfile ?: throw SerializationException("Missing uProfile value"),
            auth,
        )
    }

    override fun serialize(encoder: Encoder, value: User) {
        val output = encoder.beginStructure(descriptor)

        output.encodeSerializableElement(
            descriptor, 0, UProfile.serializer(), value.uProfile
        )

        output.encodeSerializableElement(
            descriptor, 1, UUIDSerializer, value.id
        )

        output.endStructure(descriptor)
    }
}