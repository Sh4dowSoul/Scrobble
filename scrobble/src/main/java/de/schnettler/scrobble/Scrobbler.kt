package de.schnettler.scrobble

import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import de.schnettler.database.models.LocalTrack
import de.schnettler.database.models.ScrobbleStatus
import de.schnettler.repo.ScrobbleRepository
import de.schnettler.repo.ServiceCoroutineScope
import de.schnettler.repo.authentication.provider.LastFmAuthProvider
import de.schnettler.scrobble.work.SUBMIT_CACHED_SCROBBLES_WORK
import de.schnettler.scrobble.work.ScrobbleWorker
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class Scrobbler @Inject constructor(
    private val workManager: WorkManager,
    private val notificationManager: ScrobbleNotificationManager,
    private val repo: ScrobbleRepository,
    private val scope: ServiceCoroutineScope,
    private val authProvider: LastFmAuthProvider
) {
    fun submitScrobble(track: LocalTrack) {
        if (track.readyToScrobble()) {
            // 1. Cache Scrobble
            Timber.d("[Cache] $track")
            repo.saveTrack(track.copy(status = ScrobbleStatus.LOCAL))

            // 2. Schedule Workmanager Work
            val id = scheduleScrobble()

            // 3. Observe for result
            workManager.getWorkInfoByIdLiveData(id).observeForever {info ->
                if (info != null && info.state.isFinished) {
                    Timber.d("Worker finished")
                    val count = info.outputData.getInt("count", -1)
                    val content = info.outputData.keyValueMap.filter { it.key.startsWith("track") }.values.filterIsInstance(String::class.java)
                    val description = info.outputData.getString("description") ?: ""
                    if (count > 0) {
                        notificationManager.scrobbledNotification(
                            content,
                            count,
                            description
                        )
                    }
                }
            }
        } else {
            Timber.d("[Skip] $track")
        }
    }

    private fun scheduleScrobble(): UUID {
        val request = OneTimeWorkRequestBuilder<ScrobbleWorker>()
            .build()
        workManager.enqueueUniqueWork(
            SUBMIT_CACHED_SCROBBLES_WORK,
            ExistingWorkPolicy.KEEP,
            request
        )
        return request.id
    }

    fun notifyNowPlaying(track: LocalTrack?) {
        updateNowPlayingNotification(track)
        Timber.d("[New] $track")
        track?.let {
            scope.launch {
                if (authProvider.loggedIn()) {
                    val result = repo.submitNowPlaying(track)
                    result.printResult()
                }
            }
        }
    }


    fun updateNowPlayingNotification(current: LocalTrack?) {
        if (current == null) {
            notificationManager.cancelNotifications(NOW_PLAYING_ID)
        } else {
            notificationManager.updateNowPlayingNotification(current)
        }
    }
}