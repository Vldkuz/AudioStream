package kt.main.infra.repositories

import kt.main.core.Track
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.duration
import org.jetbrains.exposed.sql.jodatime.datetime
import java.util.*

class TrackRepository(database: Database) : RepositoryBase<Track>(database, TrackTable, TrackProfilesTable) {
    object TrackProfilesTable : Table("TrackProfiles") {
        val idProfile = integer("idProfile").autoIncrement()
        val nameTrack = varchar("nameTrack", 30)
        val author = varchar("author", 50).nullable()
        val uploader = uuid("uploader")
        var uploadDate = datetime("uploadDate")
        var genre = varchar("genre", 50)
        var duration = duration("durationTrack")

        override val primaryKey = PrimaryKey(idProfile, name = "PK_ProfileTrack")
    }

    object TrackTable : Table("Tracks") {
        val idTrack = uuid("idTrack")
        val idProfile = (integer("idProfile") references TrackProfilesTable.idProfile)
        val path2Track = varchar("path2Track", 100)
    }

    override suspend fun getAll(): List<Track> {
        TODO("Not yet implemented")
    }

    override suspend fun getByName(name: String): List<Track> {
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