package de.schnettler.scrobbler.components

import androidx.compose.Composable
import androidx.ui.core.Alignment
import androidx.ui.core.Modifier
import androidx.ui.foundation.Box
import androidx.ui.foundation.Text
import androidx.ui.foundation.clickable
import androidx.ui.foundation.drawBorder
import androidx.ui.foundation.shape.corner.RoundedCornerShape
import androidx.ui.graphics.Color
import androidx.ui.layout.ExperimentalLayout
import androidx.ui.layout.FlowRow
import androidx.ui.layout.Row
import androidx.ui.layout.padding
import androidx.ui.layout.preferredHeight
import androidx.ui.material.MaterialTheme
import androidx.ui.material.Surface
import androidx.ui.text.style.TextOverflow
import androidx.ui.unit.dp
import de.schnettler.scrobbler.util.CHIP_CORNER_RADIUS
import de.schnettler.scrobbler.util.COLOR_ACTIVATED_ALPHA
import de.schnettler.scrobbler.util.COLOR_NORMAL_ALPHA
import de.schnettler.scrobbler.util.PADDING_8
import de.schnettler.scrobbler.util.DIVIDER_SIZE
import de.schnettler.scrobbler.util.PADDING_16

@OptIn(ExperimentalLayout::class)
@Composable
fun ChipRow(items: List<String>, onChipClicked: (String) -> Unit = {}) {
    Box(modifier = Modifier.padding(horizontal = PADDING_16.dp)) {
        FlowRow(mainAxisSpacing = PADDING_8.dp, crossAxisSpacing = PADDING_16.dp) {
            items.forEach {
                Chip(text = it, onSelected = { onChipClicked(it) })
            }
        }
    }
}

@OptIn(ExperimentalLayout::class)
@Composable
fun SelectableChipRow(items: List<String>, selectedIndex: Int, onSelectionChanged: (Int) -> Unit) {
    Box(
        modifier = Modifier.padding(horizontal = PADDING_16.dp).drawBorder(
            size = DIVIDER_SIZE.dp,
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.12f),
            shape = RoundedCornerShape(CHIP_CORNER_RADIUS.dp)
        )
    ) {
        Row() {
            items.forEachIndexed { i, text ->
                Row(Modifier.preferredHeight(32.dp)) {
                    Chip(text = text, selected = i == selectedIndex, onSelected = {
                        onSelectionChanged(i)
                    }, colorNormal = Color.Transparent)
                    if (i < items.size - 1) {
                        Divider(vertical = true, startIndent = 8.dp)
                    }
                }
            }
        }
    }
}

@Composable
fun Chip(
    text: String,
    selected: Boolean = false,
    colorSelected: Color = MaterialTheme.colors.secondary.copy(COLOR_ACTIVATED_ALPHA),
    colorNormal: Color = MaterialTheme.colors.onSurface.copy(COLOR_NORMAL_ALPHA),
    onSelected: () -> Unit
) {

    Surface(
        shape = RoundedCornerShape(CHIP_CORNER_RADIUS.dp),
        color = if (selected) colorSelected else colorNormal
    ) {
        Box(
            gravity = Alignment.Center,
            modifier = Modifier.clickable(onClick = { onSelected() }).preferredHeight(32.dp)
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.body2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}