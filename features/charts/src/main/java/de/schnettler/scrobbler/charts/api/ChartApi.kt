package de.schnettler.scrobbler.charts.api

import com.serjltt.moshi.adapters.Wrapped
import de.schnettler.scrobbler.charts.model.ChartArtistResponse
import de.schnettler.scrobbler.charts.model.ChartTrackResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ChartApi {
    @GET("?method=chart.gettopartists")
    @Wrapped(path = ["artists", "artist"])
    suspend fun getTopArtists(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 50,
    ): List<ChartArtistResponse>

    @GET("?method=chart.gettoptracks")
    @Wrapped(path = ["tracks", "track"])
    suspend fun getTopTracks(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 50,
    ): List<ChartTrackResponse>
}