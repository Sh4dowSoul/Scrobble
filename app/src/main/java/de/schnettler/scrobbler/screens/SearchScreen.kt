package de.schnettler.scrobbler.screens

import androidx.compose.foundation.Box
import androidx.compose.foundation.Icon
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Stack
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.material.ListItem
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Album
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.state
import androidx.compose.runtime.stateFor
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import de.schnettler.database.models.BaseEntity
import de.schnettler.database.models.EntityWithStats
import de.schnettler.database.models.LastFmEntity.Album
import de.schnettler.database.models.LastFmEntity.Track
import de.schnettler.scrobbler.UIAction
import de.schnettler.scrobbler.UIAction.ListingSelected
import de.schnettler.scrobbler.components.CustomDivider
import de.schnettler.scrobbler.components.ErrorSnackbar
import de.schnettler.scrobbler.components.PlainListIconBackground
import de.schnettler.scrobbler.components.SelectableChipRow
import de.schnettler.scrobbler.theme.AppColor
import de.schnettler.scrobbler.util.RefreshableUiState
import de.schnettler.scrobbler.util.abbreviate
import de.schnettler.scrobbler.viewmodels.SearchViewModel

@Composable
fun SearchScreen(model: SearchViewModel, actionHandler: (UIAction) -> Unit) {
    val searchResult by model.state.collectAsState()
    val searchQuery by model.searchQuery.collectAsState()
    val searchInputState = state { TextFieldValue(searchQuery.query) }
    val (showSnackbarError, updateShowSnackbarError) = stateFor(searchResult) {
        searchResult is RefreshableUiState.Error
    }

    Stack(modifier = Modifier.padding(bottom = 56.dp).fillMaxSize()) {
        Column {
            Box(modifier = Modifier.padding(16.dp)) {
                TextField(
                    value = searchInputState.value,
                    onValueChange = {
                        searchInputState.value = it
                        model.updateQuery(it.text)
                    },
                    label = { Text("Search") },
                    modifier = Modifier.fillMaxWidth(),
                    imeAction = ImeAction.Search,
                    onImeActionPerformed = { _, controller ->
                        controller?.hideSoftwareKeyboard()
                    },
                    backgroundColor = AppColor.BackgroundElevated
                )
            }
            SelectableChipRow(
                items = listOf("Alles", "Artist", "Album", "Track"),
                selectedIndex = searchQuery.filter
            ) {
                model.updateFilter(it)
            }

            searchResult.currentData?.let { results ->
                SearchResults(results = results, actionHandler = actionHandler)
            }
        }
        ErrorSnackbar(
            showError = showSnackbarError,
            onErrorAction = { },
            state = searchResult,
            fallBackMessage = "Unable to load search results",
            onDismiss = { updateShowSnackbarError(false) },
            modifier = Modifier.gravity(Alignment.BottomCenter)
        )
    }
}

@Composable
fun SearchResults(results: List<BaseEntity>, actionHandler: (UIAction) -> Unit) {
    LazyColumnFor(items = results) {
        when (it) {
            is EntityWithStats -> {
                ListItem(
                    text = { Text(it.entity.name) },
                    secondaryText = {
                        Text("${it.stats.listeners.abbreviate()} Listeners")
                    },
                    icon = {
                        PlainListIconBackground {
                            Icon(Icons.Outlined.Face)
                        }
                    },
                    onClick = { actionHandler(ListingSelected(it.entity)) }
                )
            }
            is Album -> {
                ListItem(
                    text = { Text(it.name) },
                    secondaryText = {
                        Text(it.artist)
                    },
                    icon = {
                        PlainListIconBackground {
                            Icon(Icons.Outlined.Album)
                        }
                    },
                    onClick = { actionHandler(ListingSelected(it)) }
                )
            }
            is Track -> {
                ListItem(
                    text = { Text(it.name) },
                    secondaryText = {
                        Text(it.artist)
                    },
                    icon = {
                        PlainListIconBackground {
                            Icon(Icons.Rounded.MusicNote)
                        }
                    },
                    onClick = { actionHandler(ListingSelected(it)) }
                )
            }
        }
        CustomDivider(startIndent = 72.dp)
    }
}