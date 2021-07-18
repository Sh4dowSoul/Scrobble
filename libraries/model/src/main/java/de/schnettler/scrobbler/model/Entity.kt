package de.schnettler.scrobbler.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.toDuration

interface BaseEntity

@Entity(tableName = "entity_info")
data class EntityInfo(
    @PrimaryKey val id: String,
    val tags: List<String> = listOf(),
    val durationInSeconds: Long = 0,
    val wiki: String?,
    val loved: Boolean = false
) {
    @OptIn(ExperimentalTime::class)
    val duration: Duration
        get() = durationInSeconds.toDuration(DurationUnit.SECONDS)
}

sealed class LastFmEntity(
    @Ignore open val id: String,
    @Ignore open val name: String,
    @Ignore open val url: String,
    @Ignore open val imageUrl: String?
) : BaseEntity, Parcelable {

    @Parcelize
    @Entity(tableName = "albums")
    data class Album(
        override val name: String,
        override val url: String = "",
        val artist: String,
        @PrimaryKey
        override val id: String = "album_${name.lowercase()}:artist_${artist.lowercase()}",
        val artistId: String = "artist_${artist.lowercase()}",
        override val imageUrl: String? = null
    ) : LastFmEntity(id, name, url, imageUrl)

    @Parcelize
    @Entity(tableName = "artists")
    data class Artist(
        override val name: String,
        override val url: String = "",
        @PrimaryKey override val id: String = generateId(name),
        override val imageUrl: String? = null
    ) : LastFmEntity(id, name, url, imageUrl) {
        companion object {
            fun generateId(name: String) = "artist_${name.lowercase()}"
        }
    }

    @Parcelize
    @Entity(tableName = "tracks")
    data class Track(
        override val name: String,
        override val url: String = "",
        val artist: String,
        val album: String? = null,
        val albumId: String? = null,
        @PrimaryKey
        override val id: String = "track_${name.lowercase()}:artist_${artist.lowercase()}",
        override val imageUrl: String? = null,
    ) : LastFmEntity(id, name, url, imageUrl)
}