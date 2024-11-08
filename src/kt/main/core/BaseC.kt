package kt.main.core

import java.util.*

// В остальном доменная область будет допиливаться по необходимости

abstract class Entity {
    val id: UUID = UUID.randomUUID()

    override fun equals(other: Any?): Boolean {
        return other is Entity && id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}




