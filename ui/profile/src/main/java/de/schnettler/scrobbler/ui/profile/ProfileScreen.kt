package de.schnettler.scrobbler.ui.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExtendedFloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.ListItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.PlayCircleOutline
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.schnettler.database.models.TopListAlbum
import de.schnettler.database.models.TopListArtist
import de.schnettler.database.models.TopListTrack
import de.schnettler.database.models.User
import de.schnettler.scrobbler.ui.common.compose.model.MediaCardSize
import de.schnettler.scrobbler.ui.common.compose.navigation.UIAction
import de.schnettler.scrobbler.ui.common.compose.navigation.UIError
import de.schnettler.scrobbler.ui.common.compose.theme.AppColor
import de.schnettler.scrobbler.ui.common.compose.widget.Carousel
import de.schnettler.scrobbler.ui.common.compose.widget.FullScreenLoading
import de.schnettler.scrobbler.ui.common.compose.widget.LoadingContent
import de.schnettler.scrobbler.ui.common.compose.widget.PlainListIconBackground
import de.schnettler.scrobbler.ui.common.compose.widget.Spacer
import de.schnettler.scrobbler.ui.common.compose.widget.StatsRow
import de.schnettler.scrobbler.ui.common.compose.widget.TopListCarousel
import de.schnettler.scrobbler.ui.common.util.abbreviate
import de.schnettler.scrobbler.ui.common.util.firstLetter
import de.schnettler.scrobbler.ui.common.util.toFlagEmoji
import dev.chrisbanes.accompanist.coil.CoilImage
import dev.chrisbanes.accompanist.insets.statusBarsHeight
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    actionHandler: (UIAction) -> Unit,
    errorHandler: @Composable (UIError) -> Unit,
    modifier: Modifier = Modifier
) {
    val userState by viewModel.userState.collectAsState()
    val artistState by viewModel.artistState.collectAsState()
    val albumState by viewModel.albumState.collectAsState()
    val trackState by viewModel.trackState.collectAsState()
    val states = listOf(userState, artistState, albumState, trackState)

    val mediaCardSize by viewModel.mediaCardSize.collectAsState(initial = MediaCardSize.MEDIUM.size)

    val timePeriod by viewModel.timePeriod.collectAsState()
    val showDialog by viewModel.showFilterDialog.collectAsState()

    if (showDialog) {
        PeriodSelectDialog(onSelect = { newTimePeriod ->
            viewModel.updatePeriod(newTimePeriod)
            viewModel.showDialog(false)
        }, onDismiss = {
            viewModel.showDialog(false)
        }, model = viewModel)
    }

    if (states.any { it.isError }) {
        errorHandler(
            UIError.ShowErrorSnackbar(
                state = states.firstOrNull { it.isError },
                fallbackMessage = stringResource(id = R.string.error_profile),
                onAction = viewModel::refresh
            )
        )
    }

    LoadingContent(
        empty = viewModel.userState.value.isInitialLoading,
        emptyContent = { FullScreenLoading() },
        loading = states.any { it.isRefreshLoading },
        onRefresh = viewModel::refresh) {
            userState.currentData?.let {
                ProfileContent(
                    modifier = modifier,
                    user = userState.currentData,
                    artists = artistState.currentData,
                    albums = albumState.currentData,
                    tracks = trackState.currentData,
                    timePeriod = timePeriod,
                    cardSize = mediaCardSize,
                    onFabClicked = { viewModel.showDialog(true) },
                    actioner = actionHandler,
                )
            } ?: LoginScreen()
    }
}

@Composable
private fun ProfileContent(
    modifier: Modifier,
    user: User?,
    artists: List<TopListArtist>?,
    albums: List<TopListAlbum>?,
    tracks: List<TopListTrack>?,
    timePeriod: UITimePeriod,
    cardSize: Dp,
    onFabClicked: () -> Unit,
    actioner: (UIAction) -> Unit,
) {

    Box {
        LazyColumn(modifier = modifier) {
            item { Spacer(modifier = Modifier.statusBarsHeight()) }
            item { user?.let { UserInfo(it) } }
            item {
                TopListCarousel(
                    topList = artists,
                    actionHandler = actioner,
                    titleRes = R.string.header_topartists,
                    itemSize = cardSize
                )
            }
            item {
                TopListCarousel(
                    topList = albums,
                    actionHandler = actioner,
                    titleRes = R.string.header_topalbums,
                    itemSize = cardSize
                )
            }
            item {
                Carousel(
                    items = tracks?.chunked(5),
                    titleRes = R.string.header_toptracks,
                ) { topTracks ->
                    TopTracksChunkedList(list = topTracks, actioner = actioner)
                }
            }
            item { Spacer(size = 16.dp + 56.dp + 16.dp) }
        }

        ExtendedFloatingActionButton(
            text = { Text(text = stringResource(id = timePeriod.shortTitleRes)) },
            onClick = onFabClicked,
            icon = { Icon(Icons.Outlined.Event, null) },
            contentColor = Color.White,
            modifier = modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 16.dp)
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun TopTracksChunkedList(list: List<TopListTrack>, actioner: (UIAction) -> Unit) {
    Column {
        list.forEach { (top, track) ->
            ListItem(
                text = { Text(track.name, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                secondaryText = {
                    Text(track.artist)
                },
                icon = {
                    PlainListIconBackground {
                        track.imageUrl?.let {
                            CoilImage(data = it, null)
                        } ?: Text(text = track.name.firstLetter())
                    }
                },
                trailing = { Text(text = top.count.abbreviate()) },
                modifier = Modifier
                    .width(300.dp)
                    .clickable(onClick = { actioner(UIAction.ListingSelected(track)) })
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun UserInfo(user: User) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        val date = remember {
            Instant.ofEpochSecond(user.registerDate).atZone(ZoneId.systemDefault()).toLocalDateTime()
        }

        Column {
            ListItem(
                text = {
                    Text(text = "${user.name} ${user.countryCode.toFlagEmoji()}")
                },
                secondaryText = {
                    Column {
                        Text(text = user.realname)
                        Text(
                            text = "${stringResource(id = R.string.profile_scrobblingsince)} ${
                                DateTimeFormatter.ofLocalizedDate(
                                    FormatStyle.LONG
                                ).format(date)
                            }"
                        )
                    }
                },
                icon = {
                    Surface(color = AppColor.BackgroundElevated, shape = CircleShape) {
                        Box(Modifier.size(56.dp)) {
                            if (user.imageUrl.isNotEmpty()) {
                                CoilImage(data = user.imageUrl, null, modifier = Modifier.fillMaxSize())
                            }
                        }
                    }
                }
            )

            StatsRow(
                items = listOf(
                    Icons.Rounded.PlayCircleOutline to user.playcount,
                    Icons.Outlined.Face to user.artistCount,
                    Icons.Rounded.FavoriteBorder to user.lovedTracksCount
                ),
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Composable
private fun PeriodSelectDialog(
    onSelect: (selected: UITimePeriod) -> Unit,
    onDismiss: () -> Unit,
    model: ProfileViewModel
) {
    var selected by mutableStateOf(model.timePeriod.value)
    val radioGroupOptions = UITimePeriod.values().asList()
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text(text = stringResource(id = R.string.profile_perioddialog_title)) },
        text = {
            Column {
                radioGroupOptions.forEach { current ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .selectable(selected = (current == selected), onClick = { selected = current })
                            .padding(16.dp)
                    ) {
                        RadioButton(
                            selected = (current == selected),
                            onClick = { selected = current }
                        )
                        Text(
                            text = stringResource(id = current.titleRes),
                            style = MaterialTheme.typography.body1.merge(),
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onSelect(selected) },
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colors.secondary),
            ) {
                Text(text = stringResource(id = R.string.profile_perioddialog_select))
            }
        }
    )
}