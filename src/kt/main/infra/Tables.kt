package kt.main.infra

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.duration
import org.jetbrains.exposed.sql.jodatime.datetime
import java.util.*

object RoomProfilesTable : Table("RoomProfiles") {
    val idProfile = integer("id").autoIncrement()
    val roomName = varchar("roomName", 30)
    val description = varchar("description", 50).nullable()
    override val primaryKey = PrimaryKey(idProfile, name = "PK_ID_PROFILE")
}

object RoomTable : Table("Rooms") {
    val idRoom = uuid("idRoom")
    val idProfile = reference("idProfile", RoomProfilesTable.idProfile, onDelete = ReferenceOption.CASCADE)
    val idParticipants = array<UUID>("idParticipants")
    val idTracks = array<UUID>("idTracks")
}

object TrackProfilesTable : Table("TrackProfiles") {
    val idProfile = integer("idProfile").autoIncrement()
    val nameTrack = varchar("nameTrack", 30)
    val author = varchar("author", 50).nullable()
    val uploader = reference("uploader", UsersTable.idUser)
    var uploadDate = datetime("uploadDate")
    var genre = varchar("genre", 50).nullable()
    var duration = duration("durationTrack")

    override val primaryKey = PrimaryKey(idProfile, name = "PK_ProfileTrack")
}

object TrackTable : Table("Tracks") {
    val idTrack = uuid("idTrack")
    val idProfile = reference("idProfile", TrackProfilesTable.idProfile, onDelete = ReferenceOption.CASCADE)
    val path2Track = varchar("path2Track", 100)
}

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