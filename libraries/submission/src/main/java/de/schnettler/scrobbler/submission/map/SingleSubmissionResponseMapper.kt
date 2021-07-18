package de.schnettler.scrobbler.submission.map

import de.schnettler.lastfm.models.LastFmResponse
import de.schnettler.scrobbler.core.map.Mapper
import de.schnettler.scrobbler.submission.model.ScrobbleResponse
import de.schnettler.scrobbler.submission.model.SingleScrobbleResponse
import de.schnettler.scrobbler.submission.model.SubmissionResult
import javax.inject.Inject

class SingleSubmissionResponseMapper @Inject constructor() :
    Mapper<LastFmResponse<SingleScrobbleResponse>, SubmissionResult> {
    override suspend fun map(from: LastFmResponse<SingleScrobbleResponse>): SubmissionResult {
        return when (from) {
            is LastFmResponse.ERROR -> SubmissionResult.Error(
                message = from.error?.description.orEmpty(),
                recoverable = from.error?.isRecoverable() == true
            )
            is LastFmResponse.EXCEPTION -> SubmissionResult.Error(from.exception.localizedMessage.orEmpty())
            is LastFmResponse.SUCCESS -> {
                from.data?.scrobble?.let { response ->
                    val accepted = mutableListOf<ScrobbleResponse>()
                    val ignored = mutableListOf<ScrobbleResponse>()

                    if (response.accepted()) {
                        accepted.add(response)
                    } else {
                        ignored.add(response)
                    }

                    SubmissionResult.Success(accepted = accepted, ignored = ignored)
                } ?: SubmissionResult.Success(emptyList(), emptyList())
            }
        }
    }
}