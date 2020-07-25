package de.schnettler.scrobbler.components

import androidx.compose.Composable
import androidx.ui.foundation.Icon
import androidx.ui.foundation.Text
import androidx.ui.material.BottomNavigation
import androidx.ui.material.BottomNavigationItem
import androidx.ui.material.MaterialTheme
import androidx.ui.res.vectorResource
import de.schnettler.scrobbler.AppRoute

@Composable
fun BottomNavigationBar(
    items: List<AppRoute>,
    currentScreen: AppRoute,
    onDestinationSelected: (AppRoute) -> Unit
) {
    if (currentScreen !is AppRoute.DetailRoute) {
        BottomNavigation(backgroundColor = MaterialTheme.colors.surface) {
            items.forEach { screen ->
                BottomNavigationItem(
                    icon = {
                        Icon(asset = vectorResource(id = screen.icon))
                    },
                    text = {
                        Text(text = screen.title)
                    },
                    selected = currentScreen::class == screen::class,
                    onSelected = { onDestinationSelected.invoke(screen) }
                )
            }
        }
    }
}