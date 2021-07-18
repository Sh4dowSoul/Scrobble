package de.schnettler.scrobbler.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import de.schnettler.scrobbler.authentication.db.AuthDao
import de.schnettler.scrobbler.authentication.db.SessionDao
import de.schnettler.scrobbler.authentication.model.AuthToken
import de.schnettler.scrobbler.authentication.model.Session
import de.schnettler.scrobbler.charts.dao.ChartDao
import de.schnettler.scrobbler.details.db.AlbumDetailDao
import de.schnettler.scrobbler.details.db.ArtistDetailDao
import de.schnettler.scrobbler.details.db.ArtistRelationDao
import de.schnettler.scrobbler.details.db.EntityInfoDao
import de.schnettler.scrobbler.details.db.StatsDao
import de.schnettler.scrobbler.details.db.TrackDetailDao
import de.schnettler.scrobbler.details.model.RelatedArtistEntry
import de.schnettler.scrobbler.history.domain.HistoryDao
import de.schnettler.scrobbler.image.db.ImageDao
import de.schnettler.scrobbler.model.EntityInfo
import de.schnettler.scrobbler.model.LastFmEntity
import de.schnettler.scrobbler.model.Scrobble
import de.schnettler.scrobbler.model.Stats
import de.schnettler.scrobbler.model.TopListEntry
import de.schnettler.scrobbler.model.User
import de.schnettler.scrobbler.persistence.TypeConverter
import de.schnettler.scrobbler.persistence.dao.AlbumDao
import de.schnettler.scrobbler.persistence.dao.ArtistDao
import de.schnettler.scrobbler.persistence.dao.TrackDao
import de.schnettler.scrobbler.profile.db.ToplistDao
import de.schnettler.scrobbler.profile.db.UserDao
import de.schnettler.scrobbler.submission.db.SubmissionFailureDao
import de.schnettler.scrobbler.submission.domain.SubmissionDao
import de.schnettler.scrobbler.submission.model.SubmissionFailureEntity
import dev.matrix.roomigrant.GenerateRoomMigrations

@Database(
    entities = [
        Session::class,
        LastFmEntity.Artist::class,
        LastFmEntity.Album::class,
        LastFmEntity.Track::class,
        TopListEntry::class,
        AuthToken::class,
        User::class,
        Scrobble::class,
        RelatedArtistEntry::class,
        Stats::class,
        EntityInfo::class,
        SubmissionFailureEntity::class
    ], version = 55
)
@Suppress("TooManyFunctions")
@TypeConverters(TypeConverter::class)
@GenerateRoomMigrations
abstract class AppDatabase : RoomDatabase() {
    abstract fun albumDao(): AlbumDao
    abstract fun trackDao(): TrackDao
    abstract fun artistDao(): ArtistDao

    abstract fun albumDetailDao(): AlbumDetailDao
    abstract fun trackDetailDao(): TrackDetailDao
    abstract fun artistDetailDao(): ArtistDetailDao

    abstract fun authDao(): AuthDao
    abstract fun chartDao(): ChartDao
    abstract fun userDao(): UserDao
    abstract fun submissionDao(): SubmissionDao
    abstract fun statDao(): StatsDao
    abstract fun infoDao(): EntityInfoDao
    abstract fun relationDao(): ArtistRelationDao
    abstract fun sessionDao(): SessionDao
    abstract fun historyDao(): HistoryDao
    abstract fun imageDao(): ImageDao
    abstract fun toplistDao(): ToplistDao
    abstract fun submissionFailureDao(): SubmissionFailureDao
}

@Suppress("SpreadOperator")
fun getDatabase(context: Context) = Room
    .databaseBuilder(context, AppDatabase::class.java, "lastfm")
    .addMigrations(*AppDatabase_Migrations.build())
    .build()