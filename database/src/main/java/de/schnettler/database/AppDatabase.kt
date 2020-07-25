package de.schnettler.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import de.schnettler.database.daos.AlbumDao
import de.schnettler.database.daos.ArtistDao
import de.schnettler.database.daos.AuthDao
import de.schnettler.database.daos.ChartDao
import de.schnettler.database.daos.LocalTrackDao
import de.schnettler.database.daos.RelationshipDao
import de.schnettler.database.daos.TrackDao
import de.schnettler.database.daos.UserDao
import de.schnettler.database.models.Album
import de.schnettler.database.models.Artist
import de.schnettler.database.models.AuthToken
import de.schnettler.database.models.LocalTrack
import de.schnettler.database.models.RelationEntity
import de.schnettler.database.models.Session
import de.schnettler.database.models.TopListEntry
import de.schnettler.database.models.Track
import de.schnettler.database.models.User

@Database(
    entities = [
        Session::class,
        Artist::class,
        Album::class,
        Track::class,
        TopListEntry::class,
        AuthToken::class,
        RelationEntity::class,
        User::class,
        LocalTrack::class
    ], version = 38
)
@TypeConverters(TypeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun authDao(): AuthDao
    abstract fun chartDao(): ChartDao
    abstract fun artistDao(): ArtistDao
    abstract fun relationshipDao(): RelationshipDao
    abstract fun albumDao(): AlbumDao
    abstract fun trackDao(): TrackDao
    abstract fun userDao(): UserDao
    abstract fun localTrackDao(): LocalTrackDao
}

fun provideDatabase(context: Context) = Room.databaseBuilder(
    context,
    AppDatabase::class.java,
    "lastfm"
).fallbackToDestructiveMigration().build()