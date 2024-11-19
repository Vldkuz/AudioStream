package kt.main.infra.repositories

import io.ktor.http.*
import io.ktor.utils.io.*
import kt.main.core.TProfile
import kt.main.core.Track
import kt.main.core.User
import kt.main.infra.DownloadError
import kt.main.infra.InsertDbError
import kt.main.infra.TrackProfilesTable
import kt.main.infra.TrackProfilesTable.nameTrack
import kt.main.infra.TrackTable
import kt.main.infra.TrackTable.idProfile
import kt.main.infra.TrackTable.idTrack
import kt.main.infra.TrackTable.path2Track
import kt.webDav.IFileManager
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.util.*

class TrackRepository(database: Database, private val webDavImpl: IFileManager, private val userRepos: UserRepository) :
    RepositoryBase<Track>(database, TrackTable, TrackProfilesTable) {
    override suspend fun getAll(): List<Track> {
        val tracks = dbQuery {
            (TrackTable innerJoin TrackProfilesTable)
                .select(
                    idTrack, idProfile, path2Track,
                    nameTrack, TrackProfilesTable.author, TrackProfilesTable.uploader,
                    TrackProfilesTable.uploadDate, TrackProfilesTable.genre, TrackProfilesTable.duration
                )
                .map { row -> createTrackFromRow(row) }
        }

        return tracks
    }

    override suspend fun getByName(name: String): List<Track> {
        val tracks = dbQuery {
            (TrackTable innerJoin TrackProfilesTable)
                .select(
                    idTrack, idProfile, path2Track,
                    nameTrack, TrackProfilesTable.author, TrackProfilesTable.uploader,
                    TrackProfilesTable.uploadDate, TrackProfilesTable.genre, TrackProfilesTable.duration
                )
                .where { nameTrack eq name }
                .map { row -> createTrackFromRow(row) }
        }

        return tracks
    }

    override suspend fun getById(id: UUID): Track? {
        val track = dbQuery {
            (TrackTable innerJoin TrackProfilesTable)
                .select(
                    idTrack, idProfile, path2Track,
                    nameTrack, TrackProfilesTable.author, TrackProfilesTable.uploader,
                    TrackProfilesTable.uploadDate, TrackProfilesTable.genre, TrackProfilesTable.duration
                )
                .where { idTrack eq idTrack }
                .map { row -> createTrackFromRow(row) }
                .singleOrNull()
        }

        return track
    }

    override suspend fun update(entity: Track) {
        webDavImpl.delete("${entity.id}")
        webDavImpl.upload("${entity.id}", ByteReadChannel(entity.data))

        val result = dbQuery {
            TrackTable
                .select(idProfile)
                .where { idTrack eq entity.id }
                .singleOrNull()
        }

        if (result == null) {
            throw InsertDbError("Can not update unknown track ${entity.id}")
        }

        dbQuery {
            TrackProfilesTable.update({ TrackProfilesTable.idProfile eq result[idProfile] }) {
                it[nameTrack] = entity.tProfile.name
                it[author] = entity.tProfile.author
                it[uploader] = entity.tProfile.uploader.id
                it[uploadDate] = entity.tProfile.uploadDate
                it[genre] = entity.tProfile.genre
                it[duration] = entity.tProfile.duration
            }
        }

    }

    override suspend fun remove(entity: Track) {
        webDavImpl.delete("${entity.id}")

        dbQuery {
            TrackTable.deleteWhere { idTrack eq entity.id }
        }
    }

    override suspend fun add(entity: Track) {
        val path = "${entity.id}"
        webDavImpl.upload(path, ByteReadChannel(entity.data))

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

    private suspend fun createTrackFromRow(row: ResultRow): Track {
        val profile = TProfile(
            row[nameTrack],
            row[TrackProfilesTable.author],
            userRepos.getById(row[TrackProfilesTable.uploader]) as User,
            row[TrackProfilesTable.uploadDate],
            row[TrackProfilesTable.genre],
            row[TrackProfilesTable.duration]
        )

        val httpResp = webDavImpl.download(row[path2Track])

        if (httpResp.first.status != HttpStatusCode.OK) {
            throw DownloadError("Can not upload track with ${row[idTrack]}")
        }

        return Track(profile, httpResp.second, row[idTrack])
    }
}