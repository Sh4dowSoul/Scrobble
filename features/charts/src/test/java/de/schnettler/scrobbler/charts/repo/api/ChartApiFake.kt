package de.schnettler.scrobbler.charts.repo.api

import de.schnettler.scrobbler.charts.api.ChartApi
import de.schnettler.scrobbler.charts.model.ChartArtistResponse
import de.schnettler.scrobbler.charts.model.ChartTrackResponse
import de.schnettler.scrobbler.charts.repo.tools.ApiTestUtils
import retrofit2.http.Query

class ChartApiFake : ChartApi, ApiTestUtils() {

    override suspend fun getTopArtists(
        @Query(value = "page") page: Int,
        @Query(value = "limit") limit: Int
    ): List<ChartArtistResponse> {
        return moshi.parseJsonList(getJsonStringFromFile("topArtists")) ?: emptyList()
    }

    override suspend fun getTopTracks(
        @Query(value = "page") page: Int,
        @Query(value = "limit") limit: Int
    ): List<ChartTrackResponse> {
        TODO("Not yet implemented")
    }
}