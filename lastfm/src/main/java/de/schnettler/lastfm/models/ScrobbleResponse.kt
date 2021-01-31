package de.schnettler.lastfm.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

interface GeneralScrobbleResponse {
    val status: StatusResponse
}

@JsonClass(generateAdapter = true)
data class MutlipleScrobblesResponse(
    @Json(name = "@attr") override val status: StatusResponse,
    val scrobble: List<ScrobbleResponse>
) : GeneralScrobbleResponse

@JsonClass(generateAdapter = true)
data class SingleScrobbleResponse(
    @Json(name = "@attr") override val status: StatusResponse,
    val scrobble: ScrobbleResponse
) : GeneralScrobbleResponse

@JsonClass(generateAdapter = true)
data class ScrobbleResponse(
    val timestamp: Long,
    val artist: CorrectionResponse,
    val album: CorrectionResponse,
    val albumArtist: CorrectionResponse,
    val track: CorrectionResponse,
    val ignoredMessage: IgnoredResponse
)