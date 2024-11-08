package kt.main.infra.repositories

import kt.main.core.Room
import kt.main.infra.RoomProfilesTable
import kt.main.infra.RoomTable
import org.jetbrains.exposed.sql.insert
import java.util.*

class RoomRepository(dbConfig: DBConfig) : RepositoryBase<Room>(dbConfig, RoomProfilesTable, RoomTable) {
    override suspend fun getAll(): List<Room> {
        TODO("Not Yet")
    }

    override suspend fun getByName(name: String): Room? {
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