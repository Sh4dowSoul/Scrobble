package de.schnettler.scrobbler.charts.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import de.schnettler.scrobbler.charts.repo.ChartRepository
import de.schnettler.scrobbler.model.TopListArtist
import de.schnettler.scrobbler.model.TopListTrack
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class ChartViewModel @Inject constructor(repo: ChartRepository) : ViewModel() {
    val artistState: Flow<PagingData<TopListArtist>> = repo.chartArtistPager.cachedIn(viewModelScope)

    val trackState: Flow<PagingData<TopListTrack>> = repo.chartTrackPager.cachedIn(viewModelScope)
}