package Core

import java.util.*

abstract class Entity {
    private val id: UUID = UUID.randomUUID()

    override fun equals(other: Any?): Boolean {
        return other is Entity && id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}




