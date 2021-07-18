package de.schnettler.scrobbler.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import de.schnettler.scrobbler.charts.ui.ChartScreen
import de.schnettler.scrobbler.charts.ui.ChartViewModel
import de.schnettler.scrobbler.compose.model.NavigationEvent
import de.schnettler.scrobbler.compose.navigation.Screen
import de.schnettler.scrobbler.compose.navigation.UIAction
import de.schnettler.scrobbler.compose.navigation.UIError
import de.schnettler.scrobbler.details.ui.DetailScreen
import de.schnettler.scrobbler.details.ui.album.AlbumViewModel
import de.schnettler.scrobbler.details.ui.artist.ArtistViewModel
import de.schnettler.scrobbler.details.ui.track.TrackViewModel
import de.schnettler.scrobbler.history.ui.HistoryScreen
import de.schnettler.scrobbler.history.ui.LocalViewModel
import de.schnettler.scrobbler.ktx.destination
import de.schnettler.scrobbler.ktx.secondOrNull
import de.schnettler.scrobbler.model.LastFmEntity
import de.schnettler.scrobbler.model.SessionState
import de.schnettler.scrobbler.profile.ui.ProfileScreen
import de.schnettler.scrobbler.profile.ui.ProfileViewModel
import de.schnettler.scrobbler.profile.ui.ProfileViewModelImpl
import de.schnettler.scrobbler.search.ui.SearchScreen
import de.schnettler.scrobbler.search.ui.SearchViewModel
import de.schnettler.scrobbler.search.ui.SearchViewModelImpl
import de.schnettler.scrobbler.ui.settings.SettingsScreen

@Composable
fun MainRouteContent(
    model: MainViewModel,
    navController: NavHostController,
    actioner: (UIAction) -> Unit,
    navigator: (NavigationEvent) -> Unit,
    errorer: @Composable (UIError) -> Unit,
    modifier: Modifier = Modifier,
) {
    val sessionStatus by model.sessionStatus.observeAsState(SessionState.LoggedIn)

    NavHost(navController = navController, startDestination = Screen.History.routeId) {
        destination(Screen.Charts) {
            val viewModel: ChartViewModel = hiltViewModel()
            ChartScreen(viewModel = viewModel, actionHandler = actioner, errorHandler = errorer, modifier = modifier)
        }
        destination(Screen.History) {
            val viewModel: LocalViewModel = hiltViewModel()
            HistoryScreen(
                viewModel = viewModel, actionHandler = actioner, errorHandler = errorer, modifier = modifier,
                loggedIn = sessionStatus is SessionState.LoggedIn
            )
        }
        destination(Screen.Search) {
            val viewModel: SearchViewModel = hiltViewModel<SearchViewModelImpl>()
            SearchScreen(viewModel = viewModel, navigator = navigator, errorHandler = errorer, modifier = modifier)
        }
        destination(Screen.Profile) {
            val viewModel: ProfileViewModel = hiltViewModel<ProfileViewModelImpl>()
            ProfileScreen(
                viewModel = viewModel,
                actionHandler = actioner,
                errorHandler = errorer,
                modifier = modifier
            )
        }
        destination(Screen.Settings) { SettingsScreen(dataStoreManager = model.dataStoreManager, modifier = modifier) }
        destination(Screen.ArtistDetails) { args ->
            val viewModel: ArtistViewModel = hiltViewModel()
            args.firstOrNull()?.let {
                viewModel.updateKey(LastFmEntity.Artist(it))
                DetailScreen(viewModel = viewModel, actioner = actioner, errorer = errorer)
            }
        }
        destination(screen = Screen.AlbumDetails) { args ->
            val viewModel: AlbumViewModel = hiltViewModel()
            val artist = args.firstOrNull()
            val album = args.secondOrNull()
            if (!artist.isNullOrEmpty() && !album.isNullOrEmpty()) {
                viewModel.updateKey(LastFmEntity.Album(name = album, artist = artist))
                DetailScreen(viewModel = viewModel, actioner = actioner, errorer = errorer)
            }
        }
        destination(screen = Screen.TrackDetails) { args ->
            val viewModel: TrackViewModel = hiltViewModel()
            val artist = args.firstOrNull()
            val track = args.secondOrNull()
            if (!artist.isNullOrEmpty() && !track.isNullOrEmpty()) {
                viewModel.updateKey(LastFmEntity.Track(name = track, artist = artist))
                DetailScreen(viewModel = viewModel, actioner = actioner, errorer = errorer)
            }
        }
    }
}