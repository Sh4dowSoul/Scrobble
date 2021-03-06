package de.schnettler.scrobbler.compose.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Event
import androidx.compose.material.icons.rounded.OpenInBrowser
import androidx.compose.ui.graphics.vector.ImageVector

sealed class MenuAction(val icon: ImageVector, val action: UIAction?) {
    object Period : MenuAction(Icons.Rounded.Event, null)
    class OpenInBrowser(url: String) :
        MenuAction(Icons.Rounded.OpenInBrowser, UIAction.OpenInBrowser(url))
}