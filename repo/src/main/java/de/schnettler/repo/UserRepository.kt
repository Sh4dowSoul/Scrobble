package de.schnettler.repo

import com.dropbox.android.external.store4.*
import de.schnettler.database.daos.ArtistDao
import de.schnettler.database.daos.UserDao
import de.schnettler.database.models.*
import de.schnettler.lastfm.api.lastfm.LastFmService
import de.schnettler.repo.authentication.provider.LastFmAuthProvider
import de.schnettler.repo.mapping.UserMapper
import de.schnettler.repo.mapping.map
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val userDao: UserDao,
    private val artistDao: ArtistDao,
    private val service: LastFmService,
    private val authProvider: LastFmAuthProvider
) {
    private val userStore = StoreBuilder.from(
        fetcher = nonFlowValueFetcher {session: Session ->
            UserMapper.map(service.getUserInfo(session.key))
        },
        sourceOfTruth = SourceOfTruth.from(
            reader = {session: Session ->
                userDao.getUser(session.name)
            },
            writer = { session: Session, user: User ->
                val oldUser = userDao.getUserOnce(session.name)
                oldUser?.let {
                    user.artistCount = it.artistCount
                    user.lovedTracksCount = it.lovedTracksCount
                }
                userDao.forceInsert(user)
            }
        )
    ).build()

    fun getUserInfo(): Flow<StoreResponse<User>> {
        return userStore.stream(StoreRequest.cached(authProvider.session!!,true))
    }

    fun getUserLovedTracks(): Flow<StoreResponse<List<Track>>> {
        return StoreBuilder.from(
            fetcher = nonFlowValueFetcher {_: String ->
                val session = authProvider.session!!
                val result = service.getUserLikedTracks(session.key)
                userDao.updateLovedTracksCount(session.name, result.info.total)
                result.track.map { it.map() }
            }
        ).build().stream(StoreRequest.cached("",true))
    }

    fun getUserRecentTrack(): Flow<StoreResponse<List<Track>>> {
        val userInfoStore = StoreBuilder.from<String, List<Track>>(
            fetcher = nonFlowValueFetcher {
                service.getUserRecentTrack(authProvider.session!!.key).map { it.map() }
            }
        ).build()
        return userInfoStore.stream(StoreRequest.fresh(""))
    }
}