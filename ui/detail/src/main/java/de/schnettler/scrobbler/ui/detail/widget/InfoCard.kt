package de.schnettler.scrobbler.ui.detail.widget

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AmbientContentAlpha
import androidx.compose.material.Card
import androidx.compose.material.ContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Providers
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import de.schnettler.scrobbler.ui.common.compose.theme.AppColor
import de.schnettler.scrobbler.ui.detail.R

@Composable
fun ExpandingInfoCard(info: String?) {
    if (!info.isNullOrBlank()) {
        Card(
            modifier = Modifier.padding(16.dp).fillMaxSize(),
            elevation = 0.dp,
            border = BorderStroke(1.dp, AppColor.Divider)
        ) {
            ExpandingSummary(info, modifier = Modifier.padding(16.dp))
        }
    }
}

@Composable
private fun ExpandingSummary(
    text: String?,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = MaterialTheme.typography.body2,
    expandable: Boolean = true,
    collapsedMaxLines: Int = 4,
    expandedMaxLines: Int = Int.MAX_VALUE,
) {
    var expanded by mutableStateOf(false)
    Box(modifier = Modifier.clickable(onClick = { expanded = !expanded }, enabled = expandable)) {
        Providers(AmbientContentAlpha provides ContentAlpha.high) {
            Text(
                text = text ?: stringResource(id = R.string.bio_unavailable),
                style = textStyle,
                overflow = TextOverflow.Ellipsis,
                maxLines = if (expanded) expandedMaxLines else collapsedMaxLines,
                modifier = modifier
            )
        }
    }
}