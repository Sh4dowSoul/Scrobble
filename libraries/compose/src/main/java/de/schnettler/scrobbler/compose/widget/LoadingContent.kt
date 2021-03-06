package de.schnettler.scrobbler.compose.widget

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@Composable
fun LoadingContent(
    empty: Boolean,
    emptyContent: @Composable () -> Unit = { FullScreenLoading() },
    loading: Boolean,
    onRefresh: () -> Unit,
    addStatusBarOffset: Boolean = false,
    content: @Composable () -> Unit
) {
    if (empty) {
        emptyContent()
    } else {
        val additionalRefreshingOffset = if (addStatusBarOffset) {
            val insets = LocalWindowInsets.current
            with(LocalDensity.current) { insets.statusBars.top.toDp() }
        } else {
            0.dp
        }
        SwipeRefresh(
            state = rememberSwipeRefreshState(isRefreshing = loading),
            onRefresh = onRefresh,
            content = content,
            indicator = { state, trigger ->
                SwipeRefreshIndicator(
                    state = state,
                    refreshTriggerDistance = trigger,
                    refreshingOffset = 16.dp + additionalRefreshingOffset,
                    scale = true,
                )
            }
        )
    }
}