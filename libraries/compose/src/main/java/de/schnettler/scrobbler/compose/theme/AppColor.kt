package de.schnettler.scrobbler.compose.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

object AppColor {
    val Blue400 = Color(0xFF7E8ACD)
    val Blue200 = Color(0xFFA4B0D9)
    val Jaguar = Color(0xFF202030)
    val Error = Color(0xFFEC979A)
    val Divider: Color
        @Composable
        get() = BackgroundElevated
    val BackgroundElevated: Color
        @Composable
        get() = MaterialTheme.colors.onBackground.copy(0.05F)
}
