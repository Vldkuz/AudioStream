package kt.main.infra.repositories

import kt.main.core.Room
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import java.util.*

class RoomRepository(database: Database) : RepositoryBase<Room>(database, RoomProfilesTable, RoomTable) {
    object RoomProfilesTable : Table("RoomProfiles") {
        val idRoom = integer("id").autoIncrement()
        val roomName = varchar("roomName", 30)
        val description = varchar("description", 50).nullable()
        override val primaryKey = PrimaryKey(idRoom, name = "PK_ID_PROFILE")
    }

    object RoomTable : Table("Rooms") {
        val idRoom = uuid("idRoom")
        val idProfile = (integer("idProfile") references RoomProfilesTable.idRoom)
        val idParticipants = array<UUID>("idParticipants")
        val idTracks = array<UUID>("idTracks")
    }

    override suspend fun getAll(): List<Room> {
        TODO("Not Yet")
    }

    override suspend fun getByName(name: String): List<Room> {
        TODO("Not yet implemented")
    }

    override suspend fun getById(id: UUID): Room? {
        TODO("Not yet implemented")
    }

    override suspend fun update(entity: Room) {
        TODO("Not yet implemented")
    }

    override suspend fun remove(entity: Room) {
        TODO("Not yet implemented")
    }

    override suspend fun add(entity: Room) {
        RoomTable.insert {
            it[idRoom] = entity.id
            // Остановился здесь, нужно будет сначала добавить записи в RoomProfiles.
        }
    }
}