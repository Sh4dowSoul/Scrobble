package de.schnettler.scrobbler.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.preferredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.Surface
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.swipeable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.onCommit
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.gesture.scrollorientationlocking.Orientation
import androidx.compose.ui.platform.AmbientDensity
import androidx.compose.ui.unit.dp

private val RefreshDistance = 80.dp

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SwipeToRefreshLayout(
    refreshingState: Boolean,
    onRefresh: () -> Unit,
    refreshIndicator: @Composable () -> Unit = { SwipeRefreshProgressIndicator() },
    content: @Composable () -> Unit
) {
    val refreshDistance = with(AmbientDensity.current) { RefreshDistance.toPx() }
    val state = rememberSwipeableState(refreshingState) { newValue ->
        // compare both copies of the swipe state before calling onRefresh(). This is a workaround.
        if (newValue && !refreshingState) onRefresh()
        true
    }

    Box(
        modifier = Modifier.swipeable(
            state = state,
            anchors = mapOf(
                -refreshDistance to false,
                refreshDistance to true
            ),
            thresholds = { _, _ -> FractionalThreshold(0.5f) },
            orientation = Orientation.Vertical
        ).fillMaxSize()
    ) {
        content()
        Box(Modifier.align(Alignment.TopCenter).offset(y = { state.offset.value })) {
            if (state.offset.value != -refreshDistance) {
                refreshIndicator()
            }
        }

        // TODO (https://issuetracker.google.com/issues/164113834): This state->event trampoline is a
        //  workaround for a bug in the SwipableState API. Currently, state.value is a duplicated
        //  source of truth of refreshingState.
        onCommit(refreshingState) {
            state.animateTo(refreshingState)
        }
    }
}

@Composable
fun SwipeRefreshProgressIndicator() {
    Surface(elevation = 10.dp, shape = CircleShape, modifier = Modifier.preferredSize(40.dp)) {
        CircularProgressIndicator(
            modifier = Modifier.size(20.dp),
            strokeWidth = 2.5.dp
        )
    }
}