package de.schnettler.scrobbler.history.domain

import com.dropbox.android.external.store4.Fetcher
import com.dropbox.android.external.store4.SourceOfTruth
import com.dropbox.android.external.store4.StoreBuilder
import de.schnettler.scrobbler.core.map.forLists
import de.schnettler.scrobbler.history.api.HistoryApi
import de.schnettler.scrobbler.model.Scrobble
import de.schnettler.scrobbler.model.ScrobbleStatus
import de.schnettler.scrobbler.submission.db.SubmissionFailureDao
import javax.inject.Inject

class LocalRepository @Inject constructor(
    private val historyDao: HistoryDao,
    private val submissionFailureDao: SubmissionFailureDao,
    private val historyApi: HistoryApi
) {
    val recentTracksStore = StoreBuilder.from(
        fetcher = Fetcher.of { ScrobbleMapper.forLists()(historyApi.getUserRecentTrack()) },
        sourceOfTruth = SourceOfTruth.of(
            reader = { historyDao.getListeningHistory() },
            writer = { _: String, scrobbles: List<Scrobble> ->
                // Update local scrobbles with remote data
                val changedRows = historyDao.insertAll(scrobbles)
                scrobbles.filterIndexed { index, _ ->
                    changedRows[index] == -1L
                }.forEach { historyDao.updateTrackData(it.timestamp, it.name, it.artist, it.album) }

                // Update nowPlaying a) Replace b) Remove
                scrobbles.firstOrNull { it.status == ScrobbleStatus.PLAYING }?.let { nowPlaying ->
                    historyDao.forceInsert(nowPlaying)
                } ?: historyDao.deleteByStatus(ScrobbleStatus.PLAYING)
            }
        )
    ).build()

    fun getNumberOfCachedScrobbles() = historyDao.getNumberOfCachedScrobbles()

    fun getNumberOfIgnoredScrobbles() = submissionFailureDao.getNumberOfIgnoredScrobbles()

    suspend fun getScrobblesById(ids: List<Long>) = historyDao.getScrobblesWithTimestamp(ids)
}