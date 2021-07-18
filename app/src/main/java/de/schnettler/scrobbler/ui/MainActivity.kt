package de.schnettler.scrobbler.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.SnackbarResult
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.insets.ui.Scaffold
import dagger.hilt.android.AndroidEntryPoint
import de.schnettler.scrobbler.compose.model.NavigationEvent
import de.schnettler.scrobbler.compose.navigation.Screen
import de.schnettler.scrobbler.compose.navigation.UIAction
import de.schnettler.scrobbler.compose.navigation.UIError
import de.schnettler.scrobbler.compose.theme.AppTheme
import de.schnettler.scrobbler.compose.widget.BottomNavigationBar
import de.schnettler.scrobbler.core.ui.state.RefreshableUiState
import de.schnettler.scrobbler.core.util.REDIRECT_URL
import de.schnettler.scrobbler.ktx.openCustomTab
import de.schnettler.scrobbler.ktx.openNotificationListenerSettings
import de.schnettler.scrobbler.ktx.route
import de.schnettler.scrobbler.model.LastFmEntity
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val model: MainViewModel by viewModels()

    private lateinit var onListingClicked: (LastFmEntity) -> Unit
    private lateinit var navigate: (String) -> Unit

    private val mainScreens = listOf(
        Screen.Charts,
        Screen.History,
        Screen.Search,
        Screen.Profile,
        Screen.Settings
    )

    @OptIn(ExperimentalMaterialApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            AppTheme {
                ProvideWindowInsets {
                    val navController = rememberNavController()
                    val snackHost = remember { SnackbarHostState() }
                    onListingClicked = {
                        navController.navigate(
                            when (it) {
                                is LastFmEntity.Artist -> Screen.ArtistDetails.withArg(it.name)
                                is LastFmEntity.Album -> Screen.AlbumDetails.withArgs(listOf(it.artist, it.name))
                                is LastFmEntity.Track -> Screen.TrackDetails.withArgs(listOf(it.artist, it.name))
                            }
                        )
                    }
                    navigate = { navController.navigate(it) }

                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    Scaffold(
                        scaffoldState = rememberScaffoldState(snackbarHostState = snackHost),
                        bottomBar = {
                            // navBackStackEntry == null is needed because otherwise innerPadding stays zero
                            if (mainScreens.map { it.routeId }
                                    .contains(navBackStackEntry?.route()) || navBackStackEntry == null) {
                                BottomNavigationBar(
                                    currentDestination = navBackStackEntry?.destination,
                                    screens = mainScreens,
                                ) { screen ->
                                    navController.navigate(screen.routeId) {
                                        restoreState = true
                                        launchSingleTop = true

                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                    }
                                }
                            }
                        }
                    ) { contentPadding ->
                        Content(controller = navController, host = snackHost, innerPadding = contentPadding)
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    private fun Content(controller: NavHostController, host: SnackbarHostState, innerPadding: PaddingValues) {
        MainRouteContent(
            model = model,
            navController = controller,
            actioner = ::handleAction,
            errorer = { error ->
                when (error) {
                    is UIError.ShowErrorSnackbar -> ErrorSnackbar(host = host, error = error)
                    is UIError.ScrobbleSubmissionResult -> InfoSnackbar(host = host, error = error)
                    is UIError.Snackbar -> ErrorSnackbar(host, error)
                }
            },
            navigator = ::handleNavigationEvent,
            modifier = Modifier.padding(innerPadding)
        )
    }

    private fun handleAction(action: UIAction) {
        when (action) {
            is UIAction.ListingSelected -> onListingClicked(action.listing)
            is UIAction.TagSelected -> openCustomTab("https://www.last.fm/tag/${action.id}")
            is UIAction.NavigateUp -> onBackPressed()
            is UIAction.OpenInBrowser -> openCustomTab(action.url)
            is UIAction.OpenNotificationListenerSettings -> openNotificationListenerSettings()
        }
    }

    private fun handleNavigationEvent(event: NavigationEvent) {
        when (event) {
            is NavigationEvent.OpenScreen -> navigate(event.navAction)
            is NavigationEvent.OpenNotificationListenerSettings -> openNotificationListenerSettings()
            is NavigationEvent.OpenUrlInBrowser -> openCustomTab(event.url)
        }
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun ErrorSnackbar(host: SnackbarHostState, error: UIError.ShowErrorSnackbar) {
        val state = error.state
        if (state is RefreshableUiState.Error) {
            LaunchedEffect(state) {
                val result = host.showSnackbar(
                    message = state.errorMessage
                        ?: state.exception?.message
                        ?: error.fallbackMessage,
                    actionLabel = error.actionMessage
                )
                when (result) {
                    SnackbarResult.ActionPerformed -> error.onAction()
                    SnackbarResult.Dismissed -> error.onDismiss()
                }
            }
        }
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun ErrorSnackbar(host: SnackbarHostState, event: UIError.Snackbar) {
        LaunchedEffect(event) {
            val result = host.showSnackbar(
                message = event.error.localizedMessage ?: event.fallbackMessage,
                actionLabel = event.actionMessage
            )
            when (result) {
                SnackbarResult.ActionPerformed -> event.onAction()
                SnackbarResult.Dismissed -> event.onDismiss()
            }
        }
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun InfoSnackbar(host: SnackbarHostState, error: UIError.ScrobbleSubmissionResult) {
        LaunchedEffect(error) {
            val result = host.showSnackbar(
                message = "Result: ${error.accepted} accepted, ${error.ignored} rejected.",
                actionLabel = error.actionMessage
            )
            when (result) {
                SnackbarResult.ActionPerformed -> error.onAction()
                SnackbarResult.Dismissed -> error.onDismiss()
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if ("${intent?.data?.scheme}://${intent?.data?.host}" == REDIRECT_URL) {
            // AuthResponse
            intent?.data?.getQueryParameter("token")?.let { token ->
                Timber.i("TOKEN: $token")
                model.onTokenReceived(token)
            }
        }
    }
}