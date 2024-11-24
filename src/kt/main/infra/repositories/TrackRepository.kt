package kt.main.infra.repositories

import io.ktor.utils.io.*
import kt.main.core.TProfile
import kt.main.core.Track
import kt.main.core.User
import kt.main.infra.*
import kt.main.infra.TrackTable.idProfile
import kt.main.infra.TrackTable.idTrack
import kt.main.infra.TrackTable.path2Track
import kt.webDav.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.util.*

class TrackRepository(database: Database, private val webDavImpl: IFileManager, private val userRepos: UserRepository) :
    RepositoryBase<Track>(database, TrackTable, TrackProfilesTable) {

    override suspend fun getAll(): List<Track> {
        return dbQuery { internalJoin().map { row -> createTrackFromRow(row) }}
    }

    override suspend fun getByName(name: String): List<Track> {
        return dbQuery { internalJoin().where { TrackProfilesTable.nameTrack eq name } .map { row -> createTrackFromRow(row) }}
    }

    override suspend fun getById(id: UUID): Track? {
        return dbQuery {internalJoin().where { idTrack eq id} .map { row -> createTrackFromRow(row) } .singleOrNull()}
    }

    override suspend fun update(entity: Track) {
        try {
            webDavImpl.delete("${entity.id}")
            webDavImpl.upload("${entity.id}", ByteReadChannel(entity.data))
        } catch (e: WebDavExceptions) {
            throw UpdateDbError("Can not upload or delete entity with ID ${entity.id}\n Exception: ${e.message}")
        }

        val idProfile = dbQuery {
            TrackTable
                .select(idProfile)
                .where { idTrack eq entity.id }
                .map { it[idProfile] }
                .singleOrNull()
        }

        if (idProfile == null) {
            throw InsertDbError("Can not update unknown track ${entity.id}")
        }

        dbQuery {
            TrackProfilesTable.update({ TrackProfilesTable.idProfile eq idProfile}) {
                it[author] = entity.tProfile.author
                it[nameTrack] = entity.tProfile.name
                it[uploader] = entity.tProfile.uploader.id
                it[uploadDate] = entity.tProfile.uploadDate
                it[genre] = entity.tProfile.genre
                it[duration] = entity.tProfile.duration
            }
        }
    }

    override suspend fun remove(entity: Track) {
        try {
            webDavImpl.delete("${entity.id}")
        } catch (e: DeleteError) {
            throw DeleteDbError("Can not delete entity with ID ${entity.id}\n Exception: ${e.message}")
        }

        dbQuery {
            TrackTable.deleteWhere { idTrack eq entity.id }
        }
    }

    override suspend fun add(entity: Track) {
        val path = "${entity.id}"

        try {
            webDavImpl.upload(path, ByteReadChannel(entity.data))
        } catch (e: UploadError) {
            throw InsertDbError("Can not upload unknown track ${entity.id} Exception: ${e.message}")
        }


        val idRProfile = dbQuery {
            TrackProfilesTable.insert {
                it[nameTrack] = entity.tProfile.name
                it[author] = entity.tProfile.author
                it[uploader] = entity.tProfile.uploader.id
                it[uploadDate] = entity.tProfile.uploadDate
                it[genre] = entity.tProfile.genre
                it[duration] = entity.tProfile.duration
            } get TrackProfilesTable.idProfile
        }

        dbQuery {
            TrackTable.insert {
                it[idTrack] = entity.id
                it[idProfile] = idRProfile
                it[path2Track] = path
            }
        }
    }

    private fun internalJoin(): Query {
        return (TrackTable innerJoin TrackProfilesTable)
                .select(
                    idTrack, idProfile, path2Track,
                    TrackProfilesTable.nameTrack, TrackProfilesTable.author, TrackProfilesTable.uploader,
                    TrackProfilesTable.uploadDate, TrackProfilesTable.genre, TrackProfilesTable.duration)
    }

    private suspend fun createTrackFromRow(row: ResultRow): Track {
        val profile = TProfile(
            row[TrackProfilesTable.nameTrack],
            row[TrackProfilesTable.author],
            userRepos.getById(row[TrackProfilesTable.uploader]) as User,
            row[TrackProfilesTable.uploadDate],
            row[TrackProfilesTable.genre],
            row[TrackProfilesTable.duration]
        )

        val data = try {
            webDavImpl.download(row[path2Track])
        } catch (e: DownloadError) {
            throw DownloadDbError("Can not download track with id ${row[idTrack]}\n Exception: ${e.message}")
        }

        return Track(profile, data, row[idTrack])
    }
}