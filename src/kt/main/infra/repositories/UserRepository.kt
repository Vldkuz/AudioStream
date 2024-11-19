package kt.main.infra.repositories

import kt.main.core.Auth
import kt.main.core.UProfile
import kt.main.core.User
import kt.main.infra.AuthTable
import kt.main.infra.InsertDbError
import kt.main.infra.UserProfilesTable
import kt.main.infra.UsersTable
import kt.main.infra.UsersTable.idUser
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.update
import java.util.*

class UserRepository(database: Database) : RepositoryBase<User>(database, UserProfilesTable, AuthTable, UsersTable) {
    override suspend fun getAll(): List<User> {
        val allUsers = dbQuery {
            (UsersTable innerJoin UserProfilesTable innerJoin AuthTable)
                .select(
                    idUser, UserProfilesTable.firstName,
                    UserProfilesTable.secondName, UserProfilesTable.lastName,
                    UserProfilesTable.age, AuthTable.login, AuthTable.hashPass
                )
                .map { row ->
                    val auth = Auth(row[AuthTable.login], row[AuthTable.hashPass])
                    val uProfile = UProfile(
                        row[UserProfilesTable.firstName],
                        row[UserProfilesTable.secondName],
                        row[UserProfilesTable.lastName],
                        row[UserProfilesTable.age]
                    )
                    User(uProfile, auth, row[idUser])
                }
        }

        return allUsers
    }

    override suspend fun getByName(name: String): List<User> {
        val usersByName = dbQuery {
            (UsersTable innerJoin UserProfilesTable innerJoin AuthTable)
                .select(
                    idUser, UserProfilesTable.firstName,
                    UserProfilesTable.secondName, UserProfilesTable.lastName,
                    UserProfilesTable.age, AuthTable.login, AuthTable.hashPass
                )
                .where(UserProfilesTable.firstName eq name)
                .map { row ->
                    val auth = Auth(row[AuthTable.login], row[AuthTable.hashPass])
                    val uProfile = UProfile(
                        row[UserProfilesTable.firstName],
                        row[UserProfilesTable.secondName],
                        row[UserProfilesTable.lastName],
                        row[UserProfilesTable.age]
                    )
                    User(uProfile, auth, row[idUser])
                }
        }
        return usersByName
    }

    override suspend fun getById(id: UUID): User? {
        println(id)
        val idS = dbQuery {
            UsersTable
                .select(idUser, UsersTable.idAuth, UsersTable.idProfile)
                .where { idUser eq id }
                .map { row -> Pair(row[UsersTable.idProfile], row[UsersTable.idAuth]) }
                .singleOrNull()
        }

        if (idS == null) {
            return null
        }

        val authProfile = dbQuery {
            val auth = AuthTable
                .select(AuthTable.idAuth, AuthTable.login, AuthTable.hashPass)
                .where { AuthTable.idAuth eq idS.second }
                .map { row -> Auth(row[AuthTable.login], row[AuthTable.hashPass]) }
                .single()

            val uProfile = UserProfilesTable
                .select(
                    UserProfilesTable.idProfile,
                    UserProfilesTable.firstName, UserProfilesTable.secondName,
                    UserProfilesTable.lastName, UserProfilesTable.age
                )
                .where { UserProfilesTable.idProfile eq idS.first }
                .map { row ->
                    UProfile(
                        row[UserProfilesTable.firstName],
                        row[UserProfilesTable.secondName],
                        row[UserProfilesTable.lastName],
                        row[UserProfilesTable.age]
                    )
                }
                .single()

            Pair(auth, uProfile)
        }


        return User(authProfile.second, authProfile.first, id)
    }

    override suspend fun update(entity: User) {
        val result = dbQuery {
            UsersTable
                .select(UsersTable.idProfile, UsersTable.idAuth)
                .where(idUser eq entity.id)
                .map { row -> Pair(row[UsersTable.idProfile],row[UsersTable.idAuth])}
                .singleOrNull()
        }

        if (result == null) {
            throw InsertDbError("Can not update unknown user with id ${entity.id}")
        }

        dbQuery {
            UserProfilesTable.update({ UserProfilesTable.idProfile eq result.first }) {
                it[firstName] = entity.uProfile.firstName
                it[secondName] = entity.uProfile.secondName
                it[lastName] = entity.uProfile.lastName
                it[age] = entity.uProfile.age
            }

            AuthTable.update({ AuthTable.idAuth eq result.second }) {
                it[login] = entity.auth.login
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
                it[login] = entity.auth.login
                it[hashPass] = entity.auth.hashPass
            } get AuthTable.idAuth

            UsersTable.insert {
                it[idUser] = entity.id
                it[idAuth] = authId
                it[idProfile] = profileId
            }
        }
    }
}