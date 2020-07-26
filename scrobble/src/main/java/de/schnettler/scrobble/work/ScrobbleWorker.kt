package de.schnettler.scrobble.work

import android.content.Context
import androidx.hilt.Assisted
import androidx.hilt.work.WorkerInject
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import de.schnettler.database.models.LocalTrack
import de.schnettler.database.models.ScrobbleStatus
import de.schnettler.lastfm.models.Errors
import de.schnettler.lastfm.models.GeneralScrobbleResponse
import de.schnettler.repo.ScrobbleRepository
import de.schnettler.repo.mapping.LastFmResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import kotlin.math.min

const val SUBMIT_CACHED_SCROBBLES_WORK = "submit_cached_scrobbles"
const val MAX_SCROBBLE_BATCH_SIZE = 50
const val RESULT_COUNT = "count"
const val RESULT_DESCRIPTION = "description"
const val RESULT_TRACKS = "tracks"

class ScrobbleWorker @WorkerInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val repo: ScrobbleRepository
) : CoroutineWorker(appContext, workerParams) {

    private val scrobbledTracks = mutableListOf<LocalTrack>()

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        scrobbledTracks.clear()

        Timber.d("[Scrobble] Started cached scrobble submission")
        val cachedTracks = repo.getCachedTracks()
        Timber.d("[Scrobble] Found ${cachedTracks.size} cached scrobbles")

        when (cachedTracks.size == 1) {
            true -> {
                val input = cachedTracks.first()
                val result = handleResponse(listOf(input), repo.createAndSubmitScrobble(input))
                if (result !is Result.Success) return@withContext result
            }
            false -> {
                cachedTracks.chunked(MAX_SCROBBLE_BATCH_SIZE).forEach { input ->
                    val result = handleResponse(input, repo.submitScrobbles(input))
                    if (result !is Result.Success) return@withContext result
                }
            }
        }

        // Result has to be Success here
        val max = min(5, scrobbledTracks.size)
        val scrobbles = scrobbledTracks
            .subList(0, max).map { "${it.artist} ⦁ ${it.name}" }
            .toTypedArray()
        val data = Data.Builder()
            .putStringArray(RESULT_TRACKS, scrobbles)
            .putInt(RESULT_COUNT, scrobbledTracks.size)
            .putString(RESULT_DESCRIPTION, scrobbledTracks.first().name)
            .build()
        return@withContext Result.success(data)
    }

    private fun handleError(error: Errors?): Result {
        return when (error) {
            Errors.OFFLINE, Errors.UNAVAILABLE -> {
                // Cache Scrobble
                Timber.d("Scrobble failed. Service offline")
                Result.retry()
            }
            Errors.SESSION -> {
                // Reauth and retry
                Timber.d("Scrobble failed. Unauthorized")
                Result.failure()
            }
            else -> {
                // Skip this Scrobble
                Result.failure()
            }
        }
    }

    private fun markTracksAsSubmitted(tracks: List<LocalTrack>) {
        scrobbledTracks.addAll(tracks)
        tracks.forEach {
            repo.saveTrack(it.copy(status = ScrobbleStatus.SCROBBLED))
        }
    }

    private fun <T : GeneralScrobbleResponse> handleResponse(
        input: List<LocalTrack>,
        response: LastFmResponse<T>
    ): Result {
        return when (response) {
            is LastFmResponse.SUCCESS -> {
                val accepted = response.data?.status?.accepted ?: 0
                Timber.d("[Scrobble] Accepted ${accepted / input.size * 100} %")
                if (accepted == input.size) {
                    markTracksAsSubmitted(input)
                } else {
                    // Filter accepted tracks, ignore other
                }
                Result.success()
            }
            is LastFmResponse.ERROR -> {
                handleError(response.error)
            }
        }
    }
}