package de.schnettler.scrobbler.ui.detail.viewmodel

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.schnettler.database.models.EntityInfo
import de.schnettler.database.models.EntityWithStatsAndInfo.TrackWithStatsAndInfo
import de.schnettler.database.models.LastFmEntity
import de.schnettler.repo.DetailRepository
import de.schnettler.scrobbler.ui.common.compose.RefreshableStateViewModel2
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrackViewModel @Inject constructor(
    private val repo: DetailRepository
) : RefreshableStateViewModel2<LastFmEntity.Track, TrackWithStatsAndInfo, TrackWithStatsAndInfo>(repo.trackStore) {
    fun onToggleLoveTrackClicked(track: LastFmEntity.Track, info: EntityInfo) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.toggleTrackLikeStatus(track, info)
        }
    }
}