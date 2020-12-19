package de.schnettler.scrobbler.ui.common.compose.widget

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.preferredSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import de.schnettler.scrobbler.ui.common.compose.theme.AppColor
import de.schnettler.scrobbler.ui.common.util.firstLetter

@Composable
fun NameListIcon(title: String) {
    PlainListIconBackground {
        try {
            title.firstLetter()
        } catch (e: NoSuchElementException) {
            title.first().toString()
        }.also {
            Text(text = it)
        }
    }
}

@Composable
fun PlainListIconBackground(
    color: Color = AppColor.BackgroundElevated,
    content: @Composable () -> Unit
) {
    Surface(
        color = color,
        shape = CircleShape,
        modifier = Modifier.preferredSize(40.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            content()
        }
    }
}

@Composable
fun IndexListIconBackground(
    index: Int
) {
    PlainListIconBackground { Text(text = "${index + 1}") }
}