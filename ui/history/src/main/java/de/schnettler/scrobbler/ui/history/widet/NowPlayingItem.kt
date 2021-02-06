package de.schnettler.scrobbler.ui.history

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.ListItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.schnettler.scrobbler.ui.common.compose.widget.PlainListIconBackground

@Composable
fun NowPlayingItem(name: String, artist: String, onClick: () -> Unit) {
    Card(modifier = Modifier.padding(16.dp)) {
        ListItem(
            text = { Text(name) },
            secondaryText = { Text(artist) },
            icon = {
                PlainListIconBackground(MaterialTheme.colors.secondary) {
                    Icon(Icons.Rounded.MusicNote, null)
                }
            },
            modifier = Modifier.clickable(onClick = onClick)
        )
    }
}