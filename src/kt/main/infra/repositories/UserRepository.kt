package kt.main.infra.repositories

import kt.main.core.Auth
import kt.main.core.UProfile
import kt.main.core.User
import kt.main.infra.AuthTable
import kt.main.infra.InsertDbError
import kt.main.infra.UserProfilesTable
import kt.main.infra.UsersTable
import kt.main.infra.UsersTable.idUser
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.util.*

class UserRepository(database: Database) : RepositoryBase<User>(database, UserProfilesTable, AuthTable, UsersTable) {
    override suspend fun getAll(): List<User> {
        return dbQuery { internalJoin().map { row -> createUserFromRow(row) } }
    }

    override suspend fun getByName(name: String): List<User> {
        return dbQuery {
            internalJoin().where(UserProfilesTable.firstName eq name) .map { row -> createUserFromRow(row) }
        }
    }

    override suspend fun getById(id: UUID): User? {
        return dbQuery { internalJoin().where{ idUser eq id }.map { row -> createUserFromRow(row) }.singleOrNull() }
    }

    override suspend fun update(entity: User) {

        val pairIdProfIdAuth = dbQuery {
            UsersTable
                .select(UsersTable.idProfile, UsersTable.idAuth)
                .where(idUser eq entity.id)
                .map { row -> Pair(row[UsersTable.idProfile],row[UsersTable.idAuth])}
                .singleOrNull()
        }

        if (pairIdProfIdAuth == null) {
            throw InsertDbError("Can not update unknown user with id ${entity.id}")
        }

        dbQuery {
            UserProfilesTable.update({ UserProfilesTable.idProfile eq pairIdProfIdAuth.first }) {
                it[firstName] = entity.uProfile.firstName
                it[secondName] = entity.uProfile.secondName
                it[lastName] = entity.uProfile.lastName
                it[age] = entity.uProfile.age
            }

            AuthTable.update({ AuthTable.idAuth eq pairIdProfIdAuth.second }) {
                it[login] = entity.auth!!.login
                it[hashPass] = entity.auth.hashPass
            }
        }
    }

    override suspend fun remove(entity: User) {
        dbQuery {
            UsersTable.deleteWhere { idUser eq entity.id }
        }
    }

    override suspend fun add(entity: User) {
        dbQuery {
            val profileId = UserProfilesTable.insert {
                it[firstName] = entity.uProfile.firstName
                it[secondName] = entity.uProfile.secondName
                it[lastName] = entity.uProfile.lastName
                it[age] = entity.uProfile.age
            } get UserProfilesTable.idProfile

            val authId = AuthTable.insert {
                it[login] = entity.auth!!.login
                it[hashPass] = entity.auth.hashPass
            } get AuthTable.idAuth

            UsersTable.insert {
                it[idUser] = entity.id
                it[idAuth] = authId
                it[idProfile] = profileId
            }
        }
    }

    private fun internalJoin(): Query {
        return (UsersTable innerJoin UserProfilesTable innerJoin AuthTable)
            .select(
                idUser, UserProfilesTable.firstName,
                UserProfilesTable.secondName, UserProfilesTable.lastName,
                UserProfilesTable.age, AuthTable.login, AuthTable.hashPass)
    }

    private fun createUserFromRow(row: ResultRow): User {
        val auth = Auth(row[AuthTable.login], row[AuthTable.hashPass])

        val uProfile = UProfile(
            row[UserProfilesTable.firstName],
            row[UserProfilesTable.secondName],
            row[UserProfilesTable.lastName],
            row[UserProfilesTable.age]
        )

        return User(uProfile, auth, row[idUser])
    }
}