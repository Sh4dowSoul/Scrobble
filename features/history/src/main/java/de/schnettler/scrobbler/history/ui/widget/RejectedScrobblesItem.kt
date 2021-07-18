package de.schnettler.scrobbler.history.ui.widget

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.ListItem
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import de.schnettler.scrobbler.compose.theme.AppColor
import de.schnettler.scrobbler.compose.widget.PlainListIconBackground
import de.schnettler.scrobbler.history.R

@ExperimentalMaterialApi
@Composable
internal fun RejectedScrobblesItem(ignoredCount: Int) {
    Card(modifier = Modifier.padding(16.dp)) {
        ListItem(
            text = {
                Text(text = stringResource(id = R.string.scrobbles_rejected_title))
            },
            secondaryText = {
                Text(text = "$ignoredCount " + stringResource(id = R.string.scrobbles_rejected_description))
            },
            icon = {
                PlainListIconBackground(
                    color = AppColor.Error.copy(0.4f)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Warning,
                        contentDescription = null,
                        tint = AppColor.Error,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        )
    }
}