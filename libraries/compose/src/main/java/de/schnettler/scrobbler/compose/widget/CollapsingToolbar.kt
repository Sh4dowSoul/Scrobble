package de.schnettler.scrobbler.compose.widget

import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.statusBarsHeight
import com.google.accompanist.insets.ui.TopAppBar
import de.schnettler.scrobbler.compose.navigation.MenuAction
import de.schnettler.scrobbler.compose.navigation.UIAction

@Composable
fun CollapsingToolbar(
    title: String,
    imageUrl: String?,
    menuActions: List<MenuAction> = emptyList(),
    actioner: (UIAction) -> Unit = {},
    content: LazyListScope.() -> Unit,
) = Box(modifier = Modifier.fillMaxSize()) {
    val listState = rememberLazyListState()
    var backdropHeight by remember { mutableStateOf(0) }

    Surface(Modifier.fillMaxSize()) {
        LazyColumn(state = listState) {
            // Backdrop
            item {
                BackdropImage(
                    backdropImage = imageUrl,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 10)
                        .onSizeChanged { backdropHeight = it.height }
                        .clipToBounds()
                        .offset {
                            IntOffset(
                                x = 0,
                                y = if (listState.firstVisibleItemIndex == 0) {
                                    listState.firstVisibleItemScrollOffset / 2
                                } else 0
                            )
                        }
                )
            }

            // Toolbar
            item {
                AppBarWithoutElevation(
                    title = title,
                    backgroundColor = Color.Transparent,
                    actioner = actioner,
                    menuActions = menuActions
                )
            }

            // Content
            content()
        }
    }

    val trigger = backdropHeight - LocalWindowInsets.current.statusBars.top

    OverlaidStatusBarAppBar(
        showAppBar = { listState.firstVisibleItemIndex > 0 || listState.firstVisibleItemScrollOffset >= trigger },
        modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.TopCenter)
    ) {
        AppBarWithoutElevation(
            title = title,
            backgroundColor = Color.Transparent,
            actioner = actioner,
            menuActions = menuActions
        )
    }
}

@Composable
private fun OverlaidStatusBarAppBar(
    showAppBar: () -> Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val transition = updateOverlaidStatusBarAppBarTransition(showAppBar())

    Surface(
        color = Color.Transparent,
        elevation = transition.elevation,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsHeight()
                    .graphicsLayer {
                        alpha = transition.alpha
                        translationY = transition.offset
                    }
                    .background(MaterialTheme.colors.surface),
                content = {}
            )

            if (showAppBar()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colors.surface),
                ) {
                    content()
                }
            }
        }
    }
}

@Composable
private fun AppBarWithoutElevation(
    title: String,
    backgroundColor: Color,
    actioner: (UIAction) -> Unit,
    modifier: Modifier = Modifier,
    menuActions: List<MenuAction> = emptyList(),
) {

    TopAppBar(
        title = { Text(text = title, maxLines = 1, overflow = TextOverflow.Ellipsis) },
        navigationIcon = {
            IconButton(onClick = { actioner(UIAction.NavigateUp) }) {
                Icon(Icons.Default.ArrowBack, null)
            }
        },
        actions = {
            menuActions.forEach { menuItem ->
                IconButton(onClick = {
                    menuItem.action?.let { actioner(it) }
                }) {
                    Icon(menuItem.icon, null)
                }
            }
        },
        elevation = 0.dp,
        backgroundColor = backgroundColor,
        modifier = modifier
    )
}

@Composable
private fun updateOverlaidStatusBarAppBarTransition(
    showAppBar: Boolean
): OverlaidStatusBarAppBarTransition {
    val transition = updateTransition(showAppBar)

    val elevation = transition.animateDp { show -> if (show) 2.dp else 0.dp }

    val alpha = transition.animateFloat(
        transitionSpec = {
            when {
                false isTransitioningTo true -> snap()
                else -> tween(durationMillis = 300)
            }
        }
    ) { show ->
        if (show) 1f else 0f
    }

    val offset = transition.animateFloat(
        transitionSpec = {
            when {
                false isTransitioningTo true -> spring()
                // This is a bit of a hack. We don't actually want an offset transition
                // on exit, so we just run a snap AFTER the alpha animation
                // has finished (with some buffer)
                else -> snap(delayMillis = 320)
            }
        }
    ) { show ->
        if (show) 0f else LocalWindowInsets.current.statusBars.top.toFloat()
    }

    return remember(transition) {
        OverlaidStatusBarAppBarTransition(elevation, alpha, offset)
    }
}

@Composable
private fun BackdropImage(
    backdropImage: String?,
    modifier: Modifier = Modifier
) {
    Surface(modifier = modifier) {
        if (backdropImage != null) {
            Image(
                painter = rememberImagePainter(
                    data = backdropImage,
                    builder = {
                        crossfade(true)
                    },
                ),
                contentScale = ContentScale.Crop,
                contentDescription = null,
            )
        }
        // TODO show a placeholder if null
    }
}

@Stable
class OverlaidStatusBarAppBarTransition(
    elevation: State<Dp>,
    alpha: State<Float>,
    offset: State<Float>,
) {
    val elevation: Dp by elevation
    val alpha: Float by alpha
    val offset: Float by offset
}