package de.schnettler.scrobbler.charts.repo.db

import androidx.paging.PagingSource
import androidx.paging.PagingState
import de.schnettler.scrobbler.model.TopListArtist

class FakeArtistChartPagingSource : PagingSource<Int, TopListArtist>() {
    override fun getRefreshKey(state: PagingState<Int, TopListArtist>): Int? {
        TODO("Not yet implemented")
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, TopListArtist> {
        return LoadResult.Page(emptyList(), null, null)
    }
}