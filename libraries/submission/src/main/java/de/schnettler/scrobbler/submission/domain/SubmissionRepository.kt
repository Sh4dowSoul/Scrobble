package de.schnettler.scrobbler.submission.domain

import androidx.work.Constraints.Builder
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import de.schnettler.datastore.manager.DataStoreManager
import de.schnettler.lastfm.map.ResponseToLastFmResponseMapper
import de.schnettler.lastfm.models.LastFmResponse
import de.schnettler.scrobbler.core.map.forLists
import de.schnettler.scrobbler.model.Scrobble
import de.schnettler.scrobbler.model.ScrobbleStatus
import de.schnettler.scrobbler.persistence.PreferenceRequestStore
import de.schnettler.scrobbler.persistence.PreferenceRequestStore.SCROBBLE_CONSTRAINTS_BATTERY
import de.schnettler.scrobbler.persistence.PreferenceRequestStore.SCROBBLE_CONSTRAINTS_NETWORK
import de.schnettler.scrobbler.submission.api.SubmissionApi
import de.schnettler.scrobbler.submission.db.SubmissionFailureDao
import de.schnettler.scrobbler.submission.map.MultiSubmissionResponseMapper
import de.schnettler.scrobbler.submission.map.ScrobbleResponseToSubmissionFailureEntityMapper
import de.schnettler.scrobbler.submission.map.SingleSubmissionResponseMapper
import de.schnettler.scrobbler.submission.model.NowPlayingResponse
import de.schnettler.scrobbler.submission.model.SubmissionResult
import de.schnettler.scrobbler.submission.model.SubmissionResults
import de.schnettler.scrobbler.submission.safePost
import timber.log.Timber
import javax.inject.Inject

class SubmissionRepository @Inject constructor(
    private val submissionDao: SubmissionDao,
    private val submissionApi: SubmissionApi,
    private val workManager: WorkManager,
    private val dataStoreManager: DataStoreManager,
    private val failureMapper: ScrobbleResponseToSubmissionFailureEntityMapper,
    private val submissionFailureDao: SubmissionFailureDao,
    private val multiSubmissionResponseMapper: MultiSubmissionResponseMapper,
    private val singleSubmissionResponseMapper: SingleSubmissionResponseMapper,
    private val submissionRemoteDataSource: SubmissionRemoteDataSource,
) {
    suspend fun saveTrack(track: Scrobble) = submissionDao.forceInsert(track)

    suspend fun submitCachedScrobbles(): SubmissionResults {
        Timber.d("[Scrobble] Started cached scrobble submission")
        val cachedTracks = submissionDao.getCachedTracks()
        Timber.d("[Scrobble] Found ${cachedTracks.size} cached scrobbles")

        val results = if (cachedTracks.size == 1) {
            // 1. Submit single scrobble
            submitScrobble(cachedTracks.first())
        } else {
            // 2. Submit multiple scrobbles
            submitScrobbles(cachedTracks)
        }

        val successes = results.filterIsInstance<SubmissionResult.Success>()
        return SubmissionResults(
            accepted = successes.flatMap { it.accepted },
            ignored = successes.flatMap { it.ignored },
            errors = results.filterIsInstance<SubmissionResult.Error>(),
        )
    }

    private suspend fun submitScrobbles(scrobbles: List<Scrobble>): List<SubmissionResult> {
        return scrobbles.chunked(MAX_SCROBBLE_BATCH_SIZE).map { chunk ->
            val response = submissionRemoteDataSource.submitScrobbleChunk(chunk)
            val result = multiSubmissionResponseMapper.map(response)
            handleSubmissionResult(result)
            return@map result
        }
    }

    suspend fun submitScrobble(scrobble: Scrobble): List<SubmissionResult> {
        val response = submissionRemoteDataSource.submitScrobble(scrobble)
        val result = singleSubmissionResponseMapper.map(response)
        handleSubmissionResult(result)
        return listOf(result)
    }

    private suspend fun handleSubmissionResult(submissionResult: SubmissionResult) {
        if (submissionResult is SubmissionResult.Success) {
            // 1. Handle accepted Scrobbles
            submissionResult.accepted.let { accepted ->
                val acceptedTimestamps = accepted.map { it.timestamp }

                // Mark scrobble as submitted
                submissionDao.updateScrobbleStatus(acceptedTimestamps, ScrobbleStatus.SCROBBLED)

                // Remove scrobble from failed_scrobbles table
                submissionFailureDao.deleteEntries(acceptedTimestamps)
            }

            // 2. Handle ignored Scrobbles
            submissionResult.ignored.let { ignored ->
                // Update ScrobbleStatus
                submissionDao.updateScrobbleStatus(ignored.map { it.timestamp }, ScrobbleStatus.SUBMISSION_FAILED)

                // Add to failureDb
                val mapped = failureMapper.forLists().invoke(ignored)
                submissionFailureDao.insertAll(mapped)
            }
        }
    }

    suspend fun submitNowPlaying(track: Scrobble): LastFmResponse<NowPlayingResponse> {
        return safePost {
            ResponseToLastFmResponseMapper.map(
                submissionApi.submitNowPlaying(
                    artist = track.artist,
                    track = track.name,
                    album = track.album,
                    duration = track.durationUnix(),
                )
            )
        }
    }

    suspend fun deleteScrobble(scrobble: Scrobble) {
        submissionDao.delete(scrobble)
        submissionFailureDao.deleteEntries(listOf(scrobble.timestamp))
    }

    suspend fun scheduleScrobble() {
        val constraints = Builder().apply {
            val constraintSet = dataStoreManager.getPreference(PreferenceRequestStore.scrobbleConstraints)
            if (constraintSet.contains(SCROBBLE_CONSTRAINTS_BATTERY)) setRequiresBatteryNotLow(true)
            if (constraintSet.contains(SCROBBLE_CONSTRAINTS_NETWORK)) setRequiredNetworkType(NetworkType.UNMETERED)
        }.build()
        val request = OneTimeWorkRequestBuilder<ScrobbleWorker>().setConstraints(constraints).build()
        workManager.enqueueUniqueWork(
            SUBMIT_CACHED_SCROBBLES_WORK,
            ExistingWorkPolicy.REPLACE,
            request
        )
    }

    suspend fun submitScrobbleEdit(scrobble: Scrobble) {
        submissionDao.updateScrobbleData(scrobble)
    }
}