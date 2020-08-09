package de.schnettler.scrobbler.screens.details

import androidx.compose.foundation.Border
import androidx.compose.foundation.Box
import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.Text
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayout
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.preferredHeight
import androidx.compose.foundation.layout.preferredWidth
import androidx.compose.material.Card
import androidx.compose.material.ListItem
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import de.schnettler.database.models.EntityWithStatsAndInfo.AlbumWithStatsAndInfo
import de.schnettler.database.models.LastFmEntity
import de.schnettler.scrobbler.components.ChipRow
import de.schnettler.scrobbler.components.ExpandingSummary
import de.schnettler.scrobbler.components.ListeningStats
import de.schnettler.scrobbler.components.PlainListIconBackground
import de.schnettler.scrobbler.theme.AppColor
import de.schnettler.scrobbler.util.fromHtmlLastFm
import dev.chrisbanes.accompanist.coil.CoilImage

@OptIn(ExperimentalLayout::class)
@Composable
fun AlbumDetailScreen(
    albumDetails: AlbumWithStatsAndInfo,
    onListingSelected: (LastFmEntity) -> Unit,
    onTagClicked: (String) -> Unit
) {
    val (album, stats, info) = albumDetails
    ScrollableColumn {
        Row(modifier = Modifier.padding(16.dp)) {
            AlbumArtwork(url = album.imageUrl)
            Spacer(modifier = Modifier.preferredWidth(16.dp))
            AlbumInfo(
                name = album.name,
                artist = album.artist,
                tracks = albumDetails.tracks.size,
                duration = albumDetails.getLength(),
                onArtistSelected = onListingSelected
            )
        }
        ChipRow(items = albumDetails.info.tags, onChipClicked = onTagClicked)
        Spacer(modifier = Modifier.preferredHeight(16.dp))
        ListeningStats(item = stats)
        AlbumDescription(info.wiki?.fromHtmlLastFm())
        Spacer(modifier = Modifier.preferredHeight(16.dp))
        TrackList(
            tracks = albumDetails.tracks.map { it.entity },
            onListingSelected = onListingSelected
        )
    }
}

@Composable
fun AlbumArtwork(url: String?) {
    Card(
        modifier = Modifier.preferredWidth(182.dp).aspectRatio(1F)
    ) {
        Box(
            modifier = Modifier.fillMaxSize().background(AppColor.BackgroundElevated)
        ) {
            url?.let { url ->
                CoilImage(data = url, modifier = Modifier.fillMaxSize())
            }
        }
    }
}

@Composable
fun TrackList(tracks: List<LastFmEntity.Track>, onListingSelected: (LastFmEntity) -> Unit) {
    tracks.forEachIndexed { index, track ->
        ListItem(
            text = { Text(track.name) },
            icon = { PlainListIconBackground { Text(text = "${index + 1}") } },
            onClick = { onListingSelected.invoke(track) }
        )
    }
}

@Composable
fun AlbumInfo(
    name: String,
    artist: String,
    tracks: Int,
    duration: Long,
    onArtistSelected: (LastFmEntity.Artist) -> Unit
) {
    Column(Modifier.padding(top = 8.dp)) {
        Text(
            text = name,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.h5
        )
        Text(
            text = "von $artist",
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.subtitle1,
            modifier = Modifier.clickable(onClick = {
                onArtistSelected(LastFmEntity.Artist(name = artist, url = ""))
            })
        )
        Text(
            text = "$tracks Songs ⦁ $duration Minuten",
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.subtitle2
        )
    }
}

@Composable
fun AlbumDescription(description: String?) {
    if (!description.isNullOrBlank()) {
        Card(
            modifier = Modifier.padding(16.dp).fillMaxSize(),
            elevation = 0.dp,
            border = Border(1.dp, AppColor.Divider)
        ) {
            ExpandingSummary(description, modifier = Modifier.padding(16.dp))
        }
    }
}