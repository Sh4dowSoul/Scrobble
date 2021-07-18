package de.schnettler.scrobbler.submission.domain

import de.schnettler.lastfm.map.ResponseToLastFmResponseMapper
import de.schnettler.lastfm.models.LastFmResponse
import de.schnettler.scrobbler.model.Scrobble
import de.schnettler.scrobbler.submission.api.SubmissionApi
import de.schnettler.scrobbler.submission.model.MultiScrobbleResponse
import de.schnettler.scrobbler.submission.model.SingleScrobbleResponse
import de.schnettler.scrobbler.submission.safePost
import javax.inject.Inject

class SubmissionRemoteDataSource @Inject constructor(
    private val submissionApi: SubmissionApi,
) {
    suspend fun submitScrobbleChunk(tracks: List<Scrobble>): LastFmResponse<MultiScrobbleResponse> {
        val parameters = tracks.mapIndexed { index: Int, scrobble: Scrobble ->
            mapOf(
                "artist[$index]" to scrobble.artist,
                "track[$index]" to scrobble.name,
                "album[$index]" to scrobble.album,
                "duration[$index]" to scrobble.durationUnix(),
                "timestamp[$index]" to scrobble.timeStampString(),
            )
        }.reduce { acc, map -> acc + map }

        return safePost {
            ResponseToLastFmResponseMapper.map(
                submissionApi.submitMultipleScrobbles(
                    parameters
                )
            )
        }
    }

    suspend fun submitScrobble(track: Scrobble): LastFmResponse<SingleScrobbleResponse> {
        return safePost {
            ResponseToLastFmResponseMapper.map(
                submissionApi.submitScrobble(
                    artist = track.artist,
                    track = track.name,
                    timestamp = track.timeStampString(),
                    album = track.album,
                    duration = track.durationUnix(),
                )
            )
        }
    }
}