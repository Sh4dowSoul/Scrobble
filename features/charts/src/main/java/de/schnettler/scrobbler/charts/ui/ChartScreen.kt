package de.schnettler.scrobbler.charts.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import de.schnettler.scrobbler.charts.R
import de.schnettler.scrobbler.charts.model.ChartTab
import de.schnettler.scrobbler.charts.preview.PreviewUtils
import de.schnettler.scrobbler.compose.navigation.UIAction
import de.schnettler.scrobbler.compose.navigation.UIAction.ListingSelected
import de.schnettler.scrobbler.compose.navigation.UIError
import de.schnettler.scrobbler.compose.theme.ThemedPreview
import de.schnettler.scrobbler.compose.widget.CustomDivider
import de.schnettler.scrobbler.compose.widget.IndexListIconBackground
import de.schnettler.scrobbler.compose.widget.LoadingContent
import de.schnettler.scrobbler.compose.widget.TabbedPager
import de.schnettler.scrobbler.core.ktx.abbreviate
import de.schnettler.scrobbler.model.TopListArtist
import de.schnettler.scrobbler.model.TopListTrack
import de.schnettler.scrobbler.model.Toplist
import timber.log.Timber

@Composable
fun ChartScreen(
    viewModel: ChartViewModel,
    actionHandler: (UIAction) -> Unit,
    errorHandler: @Composable (UIError) -> Unit,
    modifier: Modifier = Modifier,
) {
    TabbedPager(
        pages = ChartTab.values().map { it.text },
        modifier = modifier,
    ) { page ->
        ChartPage(page, viewModel, errorHandler, actionHandler)
    }
}

@Composable
private fun ChartPage(
    pageIndex: Int,
    viewModel: ChartViewModel,
    errorHandler: @Composable (UIError) -> Unit,
    actionHandler: (UIAction) -> Unit
) {
    val currentTab = ChartTab.values()[pageIndex]
    val pagingItems = when (currentTab) {
        ChartTab.Artist -> viewModel.artistState.collectAsLazyPagingItems()
        ChartTab.Track -> viewModel.trackState.collectAsLazyPagingItems()
    }

    val loadStates = pagingItems.loadState

    if (loadStates.refresh is LoadState.Error) {
        val errorState = loadStates.refresh as LoadState.Error
        errorHandler(UIError.Snackbar(
            errorState.error,
            stringResource(id = R.string.error_charts),
            onAction = { pagingItems.refresh() },
        ))
        Timber.e(errorState.error)
    }

    LoadingContent(
        empty = false,
        loading = loadStates.refresh == LoadState.Loading,
        onRefresh = { pagingItems.refresh() }
    ) {
        ChartList(pagingItems, actionHandler)
    }
}

@Composable
private fun ChartList(
    chartData: LazyPagingItems<out Toplist>,
    handler: (UIAction) -> Unit,
    modifier: Modifier = Modifier,
) = LazyColumn(modifier) {

    itemsIndexed(lazyPagingItems = chartData) { index, entry ->
        when (entry) {
            is TopListArtist -> ChartArtistListItem(
                name = entry.value.name,
                listener = entry.listing.count,
                index = index,
                onClicked = { handler(ListingSelected(entry.value)) }
            )
            is TopListTrack -> ChartTrackListItem(
                name = entry.value.name,
                artist = entry.value.artist,
                index = index,
                onClicked = { handler(ListingSelected(entry.value)) }
            )
        }
        CustomDivider()
    }
}

@Composable
private fun ChartArtistListItem(name: String, listener: Long, index: Int, onClicked: () -> Unit) {
    RankingListItem(
        title = name,
        subtitle = "${listener.abbreviate()} ${stringResource(id = R.string.charts_listeners)}",
        index = index,
        onClicked = onClicked
    )
}

@Composable
private fun ChartTrackListItem(name: String, artist: String, index: Int, onClicked: () -> Unit) {
    RankingListItem(title = name, subtitle = artist, index = index, onClicked = onClicked)
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun RankingListItem(title: String, subtitle: String, index: Int, onClicked: () -> Unit) {
    ListItem(
        text = { Text(text = title, maxLines = 1, overflow = TextOverflow.Ellipsis) },
        secondaryText = { Text(text = subtitle, maxLines = 1, overflow = TextOverflow.Ellipsis) },
        icon = { IndexListIconBackground(index = index) },
        modifier = Modifier.clickable(onClick = { onClicked() }),
    )
}

// Preview

@Preview
@Composable
fun ChartListPreview() = ThemedPreview {
    ChartList(chartData = PreviewUtils.generateFakeArtistChartsFlow(5).collectAsLazyPagingItems(), {})
}