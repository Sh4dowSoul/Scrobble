package de.schnettler.scrobbler.submission.domain

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import de.schnettler.scrobbler.model.Scrobble
import de.schnettler.scrobbler.model.ScrobbleStatus
import de.schnettler.scrobbler.persistence.dao.BaseDao

@Dao
abstract class SubmissionDao : BaseDao<Scrobble> {
    @Query("SELECT * FROM localTracks WHERE status = :status ORDER BY timestamp DESC")
    abstract suspend fun getCachedTracks(status: ScrobbleStatus = ScrobbleStatus.LOCAL): List<Scrobble>

    @Query("UPDATE localTracks SET album = :album, artist = :artist, name = :track WHERE timestamp = :timestamp")
    abstract suspend fun updateTrackData(timestamp: Long, track: String, artist: String, album: String)

    @Query("UPDATE localTracks SET status = :status WHERE timestamp in (:timestamps)")
    abstract suspend fun updateScrobbleStatus(timestamps: List<Long>, status: ScrobbleStatus)

    @Transaction
    open suspend fun updateScrobbleData(scrobble: Scrobble) {
        updateTrackData(scrobble.timestamp, scrobble.name, scrobble.artist, scrobble.album)
        updateScrobbleStatus(listOf(scrobble.timestamp), ScrobbleStatus.LOCAL)
    }
}