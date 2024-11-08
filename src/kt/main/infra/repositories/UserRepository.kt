package kt.main.infra.repositories

import kt.main.core.User
import kt.main.infra.AuthTable
import kt.main.infra.UserProfilesTable
import kt.main.infra.UsersTable
import java.util.*

class UserRepository(dbConfig: DBConfig) : RepositoryBase<User>(dbConfig, UserProfilesTable, AuthTable, UsersTable) {
    override suspend fun getAll(): List<User> {
        TODO("Not yet implemented")
    }

    override suspend fun getByName(name: String): User? {
        TODO("Not yet implemented")
    }

    override suspend fun getById(id: UUID): User? {
        TODO("Not yet implemented")
    }

    override suspend fun update(entity: User) {
        TODO("Not yet implemented")
    }

    override suspend fun remove(entity: User) {
        TODO("Not yet implemented")
    }

    override suspend fun add(entity: User) {
        TODO("Not yet implemented")
    }
}