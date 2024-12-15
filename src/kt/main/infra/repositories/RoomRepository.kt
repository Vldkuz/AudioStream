package kt.main.infra.repositories

import kt.main.core.RProfile
import kt.main.core.Room
import kt.main.core.Track
import kt.main.core.User
import kt.main.infra.*
import kt.main.infra.RoomProfilesTable.description
import kt.main.infra.RoomProfilesTable.roomName
import kt.main.infra.RoomTable.idParticipants
import kt.main.infra.RoomTable.idProfile
import kt.main.infra.RoomTable.idRoom
import kt.main.infra.RoomTable.idTracks
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.util.*

class RoomRepository(
    database: Database,
    private val trackRepository: IDataRepository<Track>,
    private val userRepository: IDataRepository<User>
) : RepositoryBase<Room>(database, RoomProfilesTable, RoomTable) {

    override suspend fun getAll(): List<Room> {
        return dbQuery {internalJoin().map { row -> createRoomFromResultRow(row)}}
    }

    override suspend fun getByName(name: String): List<Room> {
        return dbQuery { internalJoin().where { roomName eq name }.map { row -> createRoomFromResultRow(row) }}
    }

    override suspend fun getById(id: UUID): Room? {
        return dbQuery { internalJoin().where {idRoom eq id}.map {row -> createRoomFromResultRow(row)}.singleOrNull() }
    }


    override suspend fun update(entity: Room) {
        val idRProfile = dbQuery {
            RoomTable.select(idProfile)
                .where { idRoom eq entity.id }
                .map { row -> row[idProfile]}
                .firstOrNull()
        }

        if (idRProfile == null) {
            throw UpdateDbError("Can not update Room with unknown id ${entity.id}")
        }

        dbQuery {
            RoomProfilesTable.update({ idProfile eq idRProfile }) {
                it[roomName] = entity.rProfile.name
                it[description] = entity.rProfile.description
            }
        }

        dbQuery {
            RoomTable.update({ idRoom eq entity.id }) { it ->
                it[idParticipants] = entity.participants.map { it.id }
                it[idTracks] = entity.trackQueue.map { it.id }
            }
        }
    }

    override suspend fun remove(entity: Room) {
        dbQuery {
            RoomTable.deleteWhere { idRoom eq entity.id }
        }
    }

    override suspend fun add(entity: Room) {
        val idProfileGet = dbQuery {
            RoomProfilesTable.insert {
                it[roomName] = entity.rProfile.name
                it[description] = entity.rProfile.description
            } get RoomProfilesTable.idProfile
        }

        val hasUnknownId = dbQuery {
            entity.participants
                .map { userRepository.getById(it.id) }
        }

        if (hasUnknownId.contains(null)) {
            throw InsertDbError("Can not add unknown user with id ${hasUnknownId.indexOf(null)}")
        }

        dbQuery {
            RoomTable.insert { it ->
                it[idRoom] = entity.id
                it[idParticipants] = entity.participants.map { it.id }
                it[idProfile] = idProfileGet
                it[idTracks] = entity.trackQueue.map { it.id }
            }
        }
    }

    private fun internalJoin(): Query {
        return (RoomTable innerJoin RoomProfilesTable)
            .select(
                idRoom, idProfile,
                idTracks, idParticipants, roomName,
                description
            )
    }

    private suspend fun createRoomFromResultRow(row: ResultRow): Room {
        val profile = RProfile(row[roomName], row[description])
        val participants = row[idParticipants].map { userRepository.getById(it) as User }.toMutableSet()
        val tracks = row[idTracks]
            .map { trackRepository.getById(it) as Track }
            .toCollection(LinkedList())
        return Room(profile, participants, tracks, row[idRoom])
    }
}