package de.schnettler.scrobbler.model

import android.media.MediaMetadata
import android.text.format.DateUtils
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import kotlin.math.roundToInt
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.toDuration

@OptIn(ExperimentalTime::class)
@Suppress("TooManyFunctions")
@Entity(tableName = "localTracks")
data class Scrobble(
    val name: String,
    val artist: String,
    val album: String,
    val duration: Long,

    @PrimaryKey val timestamp: Long = System.currentTimeMillis() / 1000,
    val endTime: Long = System.currentTimeMillis(),
    var amountPlayed: Long = 0,
    val playedBy: String,
    var status: ScrobbleStatus = ScrobbleStatus.VOLATILE,
    var trackingStart: Long = timestamp
) {
    @Ignore
    val id: String = name.lowercase()
    @Ignore
    val url: String = "https://www.last.fm/music/$artist/_/$name"

    val playDuration: Duration
        get() = amountPlayed.toDuration(DurationUnit.MILLISECONDS)
    val runtimeDuration: Duration
        get() = duration.toDuration(DurationUnit.MILLISECONDS)
    val playPercent: Int
        get() = (amountPlayed.toFloat() / duration * 100).roundToInt()

    private fun playedEnough(threshold: Float) = amountPlayed >= (duration * threshold)
    fun readyToScrobble(threshold: Float) = canBeScrobbled() && playedEnough(threshold) // threshold between 0.5..1
    fun timeStampString() = timestamp.toString()
    fun durationUnix() = (duration / 1000).toString()
    fun isPlaying() = status == ScrobbleStatus.PLAYING
    fun isLocal() = isCached() || status == ScrobbleStatus.SCROBBLED
    fun isCached() = status == ScrobbleStatus.LOCAL
    fun timestampToRelativeTime() =
        if (timestamp > 0) {
            DateUtils.getRelativeTimeSpanString(
                timestamp * 1000, System.currentTimeMillis(), DateUtils
                    .MINUTE_IN_MILLIS, DateUtils.FORMAT_ABBREV_ALL
            ).toString()
        } else null

    fun isTheSameAs(track: Scrobble?) = name == track?.name && artist == track.artist
    private fun canBeScrobbled() = duration > 30000

    fun pause() {
        updateAmountPlayed()
        status = ScrobbleStatus.PAUSED
    }

    private fun updateAmountPlayed() {
        if (!isPlaying()) return
        val now = System.currentTimeMillis()
        amountPlayed += now - trackingStart
        trackingStart = now
    }

    fun play() {
        if (!isPlaying()) trackingStart = System.currentTimeMillis()
        status = ScrobbleStatus.PLAYING
    }

    fun asLastFmTrack() = LastFmEntity.Track(
        name = name,
        url = url,
        artist = artist,
        album = album
    )

    fun getArtistOrPlaceholder() = if (artist.isBlank()) "unknown" else artist
    fun getNameOrPlaceholder() = if (name.isBlank()) "unknown" else name

    companion object {
        fun fromMetadata(metadata: MediaMetadata, packageName: String): Scrobble {
            val title = (metadata.getText(MediaMetadata.METADATA_KEY_TITLE) ?: "").toString()
            val artist = ((metadata.getText(MediaMetadata.METADATA_KEY_ARTIST) ?: metadata.getText(
                MediaMetadata
                    .METADATA_KEY_ALBUM_ARTIST
            )) ?: "").toString()
            val album = (metadata.getText(MediaMetadata.METADATA_KEY_ALBUM) ?: "").toString()
            val duration = metadata.getLong(MediaMetadata.METADATA_KEY_DURATION)
            return Scrobble(
                name = title,
                artist = artist,
                album = album,
                duration = duration,
                playedBy = packageName
            )
        }
    }
}

enum class ScrobbleStatus {
    LOCAL,
    PLAYING,
    PAUSED,
    SCROBBLED,
    SUBMISSION_FAILED,
    VOLATILE,
    EXTERNAL
}