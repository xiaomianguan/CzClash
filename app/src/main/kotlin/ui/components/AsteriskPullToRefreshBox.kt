package ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp

/**
 * Smartisan-styled pull-to-refresh container.
 *
 * The refresh gesture is detected from the nested-scroll overscroll at the top of the
 * content and reported through [onRefresh]; the progress indicator is the Smartisan
 * spinner instead of the Material indicator.
 */
@Composable
internal fun AsteriskPullToRefreshBox(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    indicatorTopPadding: Dp,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    val latestOnRefresh by rememberUpdatedState(onRefresh)
    var armed by remember { mutableStateOf(true) }
    val connection = remember {
        object : NestedScrollConnection {
            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource,
            ): Offset {
                if (source == NestedScrollSource.UserInput && armed && available.y > 8f) {
                    armed = false
                    latestOnRefresh()
                }
                return Offset.Zero
            }

            override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
                armed = true
                return Velocity.Zero
            }
        }
    }
    Box(modifier = modifier.nestedScroll(connection)) {
        if (isRefreshing) {
            AsteriskSpinner(
                modifier = Modifier.align(Alignment.TopCenter).padding(top = indicatorTopPadding),
                size = 28.dp,
            )
        }
        content()
    }
}
