package de.schnettler.lastfm.api

import com.serjltt.moshi.adapters.Wrapped
import de.schnettler.lastfm.models.ArtistDto
import de.schnettler.lastfm.models.SessionDto
import de.schnettler.lastfm.models.UserDto
import retrofit2.http.GET
import retrofit2.http.Query

interface LastFmService {
    companion object {
        const val ENDPOINT = "https://ws.audioscrobbler.com/2.0/"
        const val API_KEY = "***REPLACE_WITH_LASTFM_API_KEY***"
        const val SECRET = "***REPLACE_WITH_LASTFM_SECRET***"

        const val METHOD_AUTH_SESSION = "auth.getSession"
        const val METHOD_USER_INFO = "user.getinfo"
        const val METHOD_USER_ARTISTS = "user.getTopArtists"
    }

    @GET("?method=chart.gettopartists&")
    @Wrapped(path = ["artists", "artist"])
    suspend fun getTopArtists(): List<ArtistDto>


    @GET("?method=$METHOD_AUTH_SESSION")
    @Wrapped(path = ["session"])
    suspend fun getSession(
        @Query("token") token: String,
        @Query("api_sig") signature: String
    ): SessionDto

}