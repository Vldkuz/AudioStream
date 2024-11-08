package kt.main.infra

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.duration
import org.jetbrains.exposed.sql.jodatime.datetime
import java.util.*

object UserProfilesTable : Table("UserProfiles") {
    val idProfile = integer("idProfile").autoIncrement()
    val firstName = varchar("firstName", 50)
    val secondName = varchar("secondName", 50).nullable()
    val lastName = varchar("lastName", 50).nullable()
    val age = integer("age").nullable()

    override val primaryKey = PrimaryKey(idProfile, name = "PK_UserProfile")
}

object AuthTable : Table("AuthTable") {
    val idAuth = integer("id").autoIncrement()
    val login = varchar("login", 20)
    val hashPass = byte("hashPass").nullable()

    override val primaryKey = PrimaryKey(idAuth, name = "PK_Auth")
}

object UsersTable : Table("Users") {
    val idUser = uuid("idUser")
    val idProfile = (integer("idProfile") references UserProfilesTable.idProfile)
    val idAuth = (integer("idAuth") references AuthTable.idAuth)
}


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