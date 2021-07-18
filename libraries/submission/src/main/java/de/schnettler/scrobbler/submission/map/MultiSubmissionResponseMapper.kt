package de.schnettler.scrobbler.submission.map

import de.schnettler.lastfm.models.LastFmResponse
import de.schnettler.scrobbler.core.map.Mapper
import de.schnettler.scrobbler.submission.model.MultiScrobbleResponse
import de.schnettler.scrobbler.submission.model.SubmissionResult
import javax.inject.Inject

class MultiSubmissionResponseMapper @Inject constructor() :
    Mapper<LastFmResponse<MultiScrobbleResponse>, SubmissionResult> {
    override suspend fun map(from: LastFmResponse<MultiScrobbleResponse>): SubmissionResult {
        return when (from) {
            is LastFmResponse.ERROR -> SubmissionResult.Error(
                message = from.error?.description.orEmpty(),
                recoverable = from.error?.isRecoverable() == true
            )
            is LastFmResponse.EXCEPTION -> SubmissionResult.Error(from.exception.localizedMessage.orEmpty())
            is LastFmResponse.SUCCESS -> {
                from.data?.let { responseData ->
                    val (accepted, ignored) = responseData.scrobble.partition { it.accepted() }
                    SubmissionResult.Success(accepted = accepted, ignored = ignored)
                } ?: SubmissionResult.Success(emptyList(), emptyList())
            }
        }
    }
}