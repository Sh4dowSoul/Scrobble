package de.schnettler.scrobbler.compose.widget

import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import com.google.accompanist.insets.navigationBarsPadding
import de.schnettler.scrobbler.compose.navigation.Screen

@Composable
fun BottomNavigationBar(
    screens: List<Screen>,
    currentDestination: NavDestination?,
    onClicked: (Screen) -> Unit
) {
    CustomBottomNavigation(
        backgroundColor = MaterialTheme.colors.surface, modifier = Modifier
            .navigationBarsPadding()
    ) {
        screens.forEach { screen ->
            BottomNavigationItem(
                icon = { Icon(screen.icon, null) },
                label = {
                    Text(text = stringResource(id = screen.titleId), maxLines = 1, overflow = TextOverflow.Ellipsis)
                },
                selected = currentDestination?.hierarchy?.any { it.route == screen.routeId } == true,
                onClick = {
                    if (destinationIsCurrentScreen(currentDestination, screen)) return@BottomNavigationItem
                    onClicked(screen)
                }
            )
        }
    }
}

private fun destinationIsCurrentScreen(destination: NavDestination?, screen: Screen)  =
    destination?.hierarchy?.first()?.route == screen.routeId