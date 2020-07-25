package de.schnettler.repo

import com.dropbox.android.external.store4.Fetcher
import com.dropbox.android.external.store4.SourceOfTruth
import com.dropbox.android.external.store4.StoreBuilder
import com.dropbox.android.external.store4.StoreRequest
import com.dropbox.android.external.store4.StoreResponse
import de.schnettler.database.daos.UserDao
import de.schnettler.database.models.Session
import de.schnettler.database.models.Track
import de.schnettler.database.models.User
import de.schnettler.lastfm.api.lastfm.LastFmService
import de.schnettler.repo.authentication.provider.LastFmAuthProvider
import de.schnettler.repo.mapping.UserMapper
import de.schnettler.repo.mapping.map
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val userDao: UserDao,
    private val service: LastFmService,
    private val authProvider: LastFmAuthProvider
) {
    private val userStore = StoreBuilder.from(
        fetcher = Fetcher.of { session: Session ->
            UserMapper.map(service.getUserInfo(session.key))
        },
        sourceOfTruth = SourceOfTruth.of(
            reader = { session: Session ->
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
        return userStore.stream(StoreRequest.cached(authProvider.session!!, true))
    }

    fun getUserLovedTracks(): Flow<StoreResponse<List<Track>>> {
        return StoreBuilder.from(
            fetcher = Fetcher.of { _: String ->
                val session = authProvider.session!!
                val result = service.getUserLikedTracks(session.key)
                userDao.updateLovedTracksCount(session.name, result.info.total)
                result.track.map { it.map() }
            }
        ).build().stream(StoreRequest.cached("", true))
    }
}