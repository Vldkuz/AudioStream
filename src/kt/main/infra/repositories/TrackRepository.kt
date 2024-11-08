package kt.main.infra.repositories

import kt.main.core.Track
import kt.main.infra.TrackProfilesTable
import kt.main.infra.TrackTable
import java.util.*

class TrackRepository(dbConfig: DBConfig) : RepositoryBase<Track>(dbConfig, TrackTable, TrackProfilesTable) {
    override suspend fun getAll(): List<Track> {
        TODO("Not yet implemented")
    }

    override suspend fun getByName(name: String): Track? {
        TODO("Not yet implemented")
    }

    override suspend fun getById(id: UUID): Track? {
        TODO("Not yet implemented")
    }

    override suspend fun update(entity: Track) {
        TODO("Not yet implemented")
    }

    override suspend fun remove(entity: Track) {
        TODO("Not yet implemented")
    }

    override suspend fun add(entity: Track) {
        TODO("Not yet implemented")
    }
}