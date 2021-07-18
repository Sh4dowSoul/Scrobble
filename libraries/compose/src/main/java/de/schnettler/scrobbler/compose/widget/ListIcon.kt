package de.schnettler.scrobbler.compose.widget

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.schnettler.scrobbler.compose.theme.AppColor
import de.schnettler.scrobbler.compose.theme.ThemedPreview
import de.schnettler.scrobbler.core.ktx.firstLetter

@Composable
fun NameListIcon(title: String) {
    PlainListIconBackground {
        try {
            title.firstLetter()
        } catch (e: NoSuchElementException) {
            title.firstOrNull()?.toString() ?: "?"
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
        modifier = Modifier.size(40.dp)
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

// Previews

@Preview
@Composable
fun NameListIconPreview() = ThemedPreview {
    NameListIcon(title = "Titel")
}

@Preview
@Composable
fun NameListIconErrorPreview() = ThemedPreview {
    NameListIcon(title = "")
}

@Preview
@Composable
fun PlainListIconBackgroundPreview() = ThemedPreview {
    PlainListIconBackground { }
}

@Preview
@Composable
fun IndexListIconBackgroundPreview() = ThemedPreview {
    IndexListIconBackground(0)
}