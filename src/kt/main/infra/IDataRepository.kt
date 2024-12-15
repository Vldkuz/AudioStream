package kt.main.infra

import kt.main.core.Auth
import java.util.*


interface IDataRepository<T> {
    suspend fun getAll(): List<T>
    suspend fun getByName(name: String): List<T>
    suspend fun getById(id: UUID): T?
    suspend fun add(entity: T)
    suspend fun remove(entity: T)
    suspend fun update(entity: T)
}

interface IAuthCheck {
    suspend fun checkAuth(auth: Auth): Boolean
}

