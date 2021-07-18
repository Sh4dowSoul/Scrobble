package de.schnettler.scrobbler.submission.api

import com.serjltt.moshi.adapters.Wrapped
import de.schnettler.scrobbler.network.common.annotation.tag.SessionAuthentication
import de.schnettler.scrobbler.network.common.annotation.tag.SignatureAuthentication
import de.schnettler.scrobbler.submission.model.MultiScrobbleResponse
import de.schnettler.scrobbler.submission.model.NowPlayingResponse
import de.schnettler.scrobbler.submission.model.SingleScrobbleResponse
import retrofit2.Response
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.QueryMap

@SessionAuthentication
@SignatureAuthentication
interface SubmissionApi {
    @POST("?method=track.scrobble")
    @Wrapped(path = ["scrobbles"])
    suspend fun submitScrobble(
        @Query("track") track: String,
        @Query("artist") artist: String,
        @Query("album") album: String,
        @Query("duration") duration: String,
        @Query("timestamp") timestamp: String,
    ): Response<SingleScrobbleResponse>

    @POST("?method=track.updateNowPlaying")
    @Wrapped(path = ["nowplaying"])
    suspend fun submitNowPlaying(
        @Query("track") track: String,
        @Query("artist") artist: String,
        @Query("album") album: String,
        @Query("duration") duration: String,
    ): Response<NowPlayingResponse>

    @POST("?method=track.scrobble")
    @Wrapped(path = ["scrobbles"])
    suspend fun submitMultipleScrobbles(
        @QueryMap body: Map<String, String>
    ): Response<MultiScrobbleResponse>
}