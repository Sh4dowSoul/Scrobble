package de.schnettler.scrobbler.details.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import de.schnettler.scrobbler.compose.navigation.UIAction
import de.schnettler.scrobbler.compose.navigation.UIAction.ListingSelected
import de.schnettler.scrobbler.compose.navigation.UIError
import de.schnettler.scrobbler.compose.theme.AppColor
import de.schnettler.scrobbler.compose.widget.Header
import de.schnettler.scrobbler.core.ui.viewmodel.RefreshableStateViewModel2
import de.schnettler.scrobbler.details.R
import de.schnettler.scrobbler.details.model.AlbumDetailEntity
import de.schnettler.scrobbler.details.model.ArtistDetailEntity
import de.schnettler.scrobbler.details.model.TrackDetailEntity
import de.schnettler.scrobbler.details.ui.album.AlbumDetailScreen
import de.schnettler.scrobbler.details.ui.artist.ArtistDetailScreen
import de.schnettler.scrobbler.details.ui.track.TrackDetailScreen
import de.schnettler.scrobbler.details.ui.track.TrackViewModel
import de.schnettler.scrobbler.details.ui.widget.RefreshableScreen
import de.schnettler.scrobbler.model.LastFmEntity

@Composable
fun <Key : Any, StateType : Any, Output : StateType> DetailScreen(
    viewModel: RefreshableStateViewModel2<Key, StateType, Output>,
    actioner: (UIAction) -> Unit,
    errorer: @Composable (UIError) -> Unit,
) {
    val state by viewModel.state.collectAsState()
    RefreshableScreen(
        state = state,
        refresh = viewModel::refresh,
        errorer = errorer,
        errorId = R.string.error_details
    ) { details ->
        when (details) {
            is ArtistDetailEntity -> ArtistDetailScreen(info = details, actioner = actioner)
            is AlbumDetailEntity -> AlbumDetailScreen(details = details, actioner = actioner)
            is TrackDetailEntity ->
                TrackDetailScreen(viewModel = viewModel as TrackViewModel, details = details, actioner = actioner)
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AlbumCategory(
    album: LastFmEntity.Album?,
    artistPlaceholder: String,
    actionHandler: (UIAction) -> Unit
) {
    Header(title = stringResource(id = R.string.track_sourcealbum))
    ListItem(
        text = {
            Text(album?.name ?: stringResource(id = R.string.track_unknownalbum))
        },
        secondaryText = {
            Text(album?.artist ?: artistPlaceholder)
        },
        icon = {
            Surface(
                color = AppColor.BackgroundElevated,
                shape = RoundedCornerShape(8.dp)
            ) {
                Box(modifier = Modifier.size(60.dp)) {
                    Image(
                        painter = rememberImagePainter(data = album?.imageUrl, builder = { crossfade(true) }),
                        contentDescription = null,
                        contentScale = ContentScale.Crop
                    )
                }
            }
        },
        modifier = Modifier.clickable(onClick = {
            actionHandler(ListingSelected(album ?: LastFmEntity.Artist(name = artistPlaceholder, url = "")))
        })
    )
}