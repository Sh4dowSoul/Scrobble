package de.schnettler.scrobbler.ktx

import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import de.schnettler.scrobbler.compose.navigation.Screen

fun NavBackStackEntry.route() = destination.hierarchy.firstOrNull()?.route

fun NavGraphBuilder.destination(screen: Screen, content: @Composable (List<String>) -> Unit) {
    composable(
        route = screen.argRoute,
        arguments = screen.navArgs,
    ) { entry ->
        val args = screen.args.mapNotNull { entry.arguments?.getString(it.name) }
        content(args)
    }
}