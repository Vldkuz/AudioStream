package kt.main.infra.repositories

import kt.main.core.Auth
import kt.main.core.UProfile
import kt.main.core.User
import kt.main.infra.repositories.UserRepository.UsersTable.idUser
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.util.*

class UserRepository(database: Database) : RepositoryBase<User>(database, UserProfilesTable, AuthTable, UsersTable) {
    object UserProfilesTable : Table("userProfiles") {
        val idProfile = integer("idProfile").autoIncrement()
        val firstName = varchar("firstName", 50)
        val secondName = varchar("secondName", 50).nullable()
        val lastName = varchar("lastName", 50).nullable()
        val age = integer("age").nullable()

        override val primaryKey = PrimaryKey(idProfile, name = "PK_UserProfile")
    }

    object AuthTable : Table("auth") {
        val idAuth = integer("id").autoIncrement()
        val login = varchar("login", 20).uniqueIndex()
        val hashPass = varchar("hashPass", 128)

        override val primaryKey = PrimaryKey(idAuth, name = "PK_Auth")
    }

    object UsersTable : Table("users") {
        val idUser = uuid("idUser")
        val idProfile = reference("idProfile", UserProfilesTable.idProfile, onDelete = ReferenceOption.CASCADE)
        val idAuth = reference("idAuth", AuthTable.idAuth, onDelete = ReferenceOption.CASCADE)
    }

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
            (UsersTable innerJoin UserProfilesTable)
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

        return User(authProfile.second, authProfile.first)
    }

    override suspend fun update(entity: User) {
        dbQuery {
            val idAuthProfile = getAuthProfileId(entity)
            UserProfilesTable.update({ UserProfilesTable.idProfile eq idAuthProfile.first }) {
                it[firstName] = entity.uProfile.firstName
                it[secondName] = entity.uProfile.secondName
                it[lastName] = entity.uProfile.lastName
                it[age] = entity.uProfile.age
            }

            AuthTable.update({ AuthTable.idAuth eq idAuthProfile.second }) {
                it[login] = entity.auth.login
                it[hashPass] = entity.auth.hashPass
            }
        }
    }

    override suspend fun remove(entity: User) {
        val idAuthProfile = getAuthProfileId(entity)
        AuthTable.deleteWhere { idAuth eq idAuthProfile.second }
        UserProfilesTable.deleteWhere { idProfile eq idAuthProfile.first }
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

    private suspend fun getAuthProfileId(entity: User): Pair<Int, Int> {
        val resultRow = dbQuery {
            val resultRow = UsersTable
                .select(UsersTable.idProfile, UsersTable.idAuth)
                .where(idUser eq entity.id)


            val idAuth = resultRow.map { it[UsersTable.idAuth] }.single()
            val idProfile = resultRow.map { it[UsersTable.idProfile] }.single()
            Pair(idProfile, idAuth)
        }

        return resultRow
    }
}