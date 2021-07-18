package de.schnettler.scrobbler.submission.domain

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import de.schnettler.scrobbler.submission.model.ScrobbleResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.min

const val SUBMIT_CACHED_SCROBBLES_WORK = "submit_cached_scrobbles"
const val MAX_SCROBBLE_BATCH_SIZE = 50
const val RESULT_COUNT = "count"
const val RESULT_DESCRIPTION = "description"
const val RESULT_TRACKS = "tracks"

@HiltWorker
class ScrobbleWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val repo: SubmissionRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val submissionResults = repo.submitCachedScrobbles()

        return@withContext when {
            submissionResults.accepted.isNotEmpty() -> Result.success(generateSuccessData(submissionResults.accepted))
            submissionResults.errors.any { it.recoverable } -> Result.retry()
            else -> Result.failure()
        }
    }

    private fun generateSuccessData(acceptedTracks: List<ScrobbleResponse>): Data {
        // Result has to be Success here
        val max = min(5, acceptedTracks.size)
        val scrobbles = acceptedTracks
            .subList(0, max).map { "${it.artist.correctValue} ⦁ ${it.track.correctValue}" }
            .toTypedArray()
        return Data.Builder()
            .putStringArray(RESULT_TRACKS, scrobbles)
            .putInt(RESULT_COUNT, acceptedTracks.size)
            .putString(RESULT_DESCRIPTION, acceptedTracks.first().track.correctValue)
            .build()
    }
}