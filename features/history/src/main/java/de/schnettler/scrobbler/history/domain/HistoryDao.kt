package de.schnettler.scrobbler.history.domain

import androidx.room.Dao
import androidx.room.Query
import de.schnettler.scrobbler.model.Scrobble
import de.schnettler.scrobbler.model.ScrobbleStatus
import de.schnettler.scrobbler.persistence.dao.BaseDao
import kotlinx.coroutines.flow.Flow

@Dao
abstract class HistoryDao : BaseDao<Scrobble> {
    @Query("SELECT * FROM localTracks ORDER BY timestamp DESC LIMIT :limit")
    abstract fun getListeningHistory(
        limit: Int = 50
    ): Flow<List<Scrobble>>

    @Query("SELECT COUNT(timestamp) FROM localTracks WHERE status = 'LOCAL'")
    abstract fun getNumberOfCachedScrobbles(): Flow<Int>

    @Query("DELETE FROM localTracks WHERE status = :include")
    abstract fun deleteByStatus(include: ScrobbleStatus = ScrobbleStatus.PLAYING)

    @Query("UPDATE localTracks SET album = :album, artist = :artist, name = :track WHERE timestamp = :timestamp")
    abstract suspend fun updateTrackData(timestamp: Long, track: String, artist: String, album: String)

    @Query("SELECT * FROM localTracks WHERE timestamp in (:timestamps)")
    abstract suspend fun getScrobblesWithTimestamp(timestamps: List<Long>): List<Scrobble>
}