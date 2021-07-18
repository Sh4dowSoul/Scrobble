package de.schnettler.scrobbler.search.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.ListItem
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Album
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.statusBarsHeight
import de.schnettler.scrobbler.compose.model.NavigationEvent
import de.schnettler.scrobbler.compose.model.NavigationEvent.OpenScreen.OpenAlbumDetails
import de.schnettler.scrobbler.compose.model.NavigationEvent.OpenScreen.OpenArtistDetails
import de.schnettler.scrobbler.compose.model.NavigationEvent.OpenScreen.OpenTrackDetails
import de.schnettler.scrobbler.compose.navigation.UIError
import de.schnettler.scrobbler.compose.theme.AppColor
import de.schnettler.scrobbler.compose.widget.CustomDivider
import de.schnettler.scrobbler.compose.widget.PlainListIconBackground
import de.schnettler.scrobbler.core.ktx.abbreviate
import de.schnettler.scrobbler.search.R
import de.schnettler.scrobbler.search.model.SearchResult
import de.schnettler.scrobbler.search.ui.widget.SelectableChipRow

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    navigator: (NavigationEvent) -> Unit,
    errorHandler: @Composable (UIError) -> Unit,
    modifier: Modifier = Modifier
) {
    val searchResult by viewModel.state.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchInputState = remember { mutableStateOf(TextFieldValue(searchQuery.query)) }
    if (searchResult.isError) {
        errorHandler(
            UIError.ShowErrorSnackbar(
                state = searchResult,
                fallbackMessage = stringResource(id = R.string.error_search)
            )
        )
    }

    Column(modifier = modifier) {
        Spacer(modifier = Modifier.statusBarsHeight())
        val softKeyboard = LocalSoftwareKeyboardController.current
        Box(modifier = Modifier.padding(16.dp)) {
            TextField(
                value = searchInputState.value,
                onValueChange = {
                    searchInputState.value = it
                    viewModel.updateQuery(it.text)
                },
                label = { Text(text = stringResource(id = R.string.search_hint)) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                colors = TextFieldDefaults.textFieldColors(backgroundColor = AppColor.BackgroundElevated),
                keyboardActions = KeyboardActions { softKeyboard?.hide() }
            )
        }
        SelectableChipRow(
            items = stringArrayResource(id = R.array.search_filter),
            selectedIndex = searchQuery.filter
        ) {
            viewModel.updateFilter(it)
        }

        searchResult.currentData?.let { results ->
            SearchResults(results = results, navigator = navigator)
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun SearchResults(results: List<SearchResult>, navigator: (NavigationEvent) -> Unit) {
    LazyColumn {
        items(items = results) {
            when (it) {
                is SearchResult.ArtistResult -> {
                    ListItem(
                        text = { Text(it.name) },
                        secondaryText = {
                            Text("${it.listeners.abbreviate()} ${stringResource(id = R.string.search_listeners)}")
                        },
                        icon = {
                            PlainListIconBackground {
                                Icon(Icons.Outlined.Face, null)
                            }
                        },
                        modifier = Modifier.clickable(onClick = {
                            navigator.invoke(OpenArtistDetails(it.name))
                        })
                    )
                }
                is SearchResult.AlbumResult -> {
                    ListItem(
                        text = { Text(it.name) },
                        secondaryText = {
                            Text(it.artistName)
                        },
                        icon = {
                            PlainListIconBackground {
                                Icon(Icons.Outlined.Album, null)
                            }
                        },
                        modifier = Modifier.clickable(onClick = {
                            navigator.invoke(OpenAlbumDetails(it.name, it.artistName))
                        })
                    )
                }
                is SearchResult.TrackResult -> {
                    ListItem(
                        text = { Text(it.name) },
                        secondaryText = {
                            Text(it.artistName)
                        },
                        icon = {
                            PlainListIconBackground {
                                Icon(Icons.Rounded.MusicNote, null)
                            }
                        },
                        modifier = Modifier.clickable(onClick = {
                            navigator.invoke(OpenTrackDetails(it.name, it.artistName))
                        })
                    )
                }
            }
            CustomDivider(startIndent = 72.dp)
        }
    }
}