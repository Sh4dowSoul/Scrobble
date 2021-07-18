package de.schnettler.scrobbler.charts.preview

import androidx.paging.PagingData
import de.schnettler.scrobbler.model.EntityType
import de.schnettler.scrobbler.model.LastFmEntity
import de.schnettler.scrobbler.model.ListType
import de.schnettler.scrobbler.model.TopListArtist
import de.schnettler.scrobbler.model.TopListEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

internal object PreviewUtils {
    fun generateFakeArtistChartsFlow(number: Int): Flow<PagingData<TopListArtist>> {
        val data = List(number) { index ->
            TopListArtist(
                TopListEntry(
                    "",
                    EntityType.ARTIST,
                    ListType.CHART,
                    index,
                    10
                ),
                LastFmEntity.Artist("Artist $index")
            )
        }
        return flowOf(PagingData.from(data))
    }
}