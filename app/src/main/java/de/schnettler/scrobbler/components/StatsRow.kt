package de.schnettler.scrobbler.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.rounded.Hearing
import androidx.compose.material.icons.rounded.PlayCircleOutline
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import de.schnettler.database.models.Stats
import de.schnettler.scrobbler.util.abbreviate

@Composable
fun StatsRow(
    items: List<Pair<ImageVector, Long>>
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp, top = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        items.filter { it.second >= 0 }.forEach {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(imageVector = it.first.copy(defaultHeight = 28.dp, defaultWidth = 28.dp))
                Text(text = it.second.abbreviate())
            }
        }
    }
}

@Composable
fun ListeningStats(item: Stats?) {
    item?.let { stat ->
        StatsRow(
            items = listOf(
                Icons.Rounded.PlayCircleOutline to stat.plays,
                Icons.Rounded.Hearing to stat.userPlays,
                Icons.Outlined.AccountCircle to stat.listeners
            )
        )
    }
}