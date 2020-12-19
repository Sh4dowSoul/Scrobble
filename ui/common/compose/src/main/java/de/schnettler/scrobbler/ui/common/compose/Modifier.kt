package de.schnettler.scrobbler.ui.common.compose

import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.LayoutModifier
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntSize
import kotlin.math.roundToInt

fun Modifier.offset(getOffset: (IntSize) -> Offset) = then(OffsetModifier(getOffset))

private data class OffsetModifier(
    private val getOffset: (IntSize) -> Offset
) : LayoutModifier {
    override fun MeasureScope.measure(
        measurable: Measurable,
        constraints: Constraints
    ): MeasureResult {
        val placeable = measurable.measure(constraints)
        return layout(placeable.width, placeable.height) {
            val offset = getOffset(IntSize(placeable.width, placeable.height))
            placeable.place(offset.x.roundToInt(), offset.y.roundToInt())
        }
    }
}

fun lerp(
    startValue: Float,
    endValue: Float,
    fraction: Float
) = startValue + fraction * (endValue - startValue)