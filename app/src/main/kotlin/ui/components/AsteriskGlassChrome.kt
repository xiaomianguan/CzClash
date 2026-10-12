// Copyright 2026, AsteriskMETA contributors
// SPDX-License-Identifier: GPL-3.0

package ui.components

import cc.wuersan008.smartisanx.core.theme.SmartisanTypography
import androidx.compose.runtime.remember
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import cc.wuersan008.smartisanx.core.theme.LocalSmartisanContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cc.wuersan008.smartisanx.core.interaction.collectSmartisanPressedAsState
import cc.wuersan008.smartisanx.core.interaction.rememberSmartisanInteractionSource
import cc.wuersan008.smartisanx.core.interaction.smartisanClickable
import cc.wuersan008.smartisanx.core.theme.LocalSmartisanColors
import cc.wuersan008.smartisanx.core.theme.LocalSmartisanTypography
import cc.wuersan008.smartisanx.core.utils.smartisanDrawableBackground
import cc.wuersan008.smartisanx.ui.asset.SmartisanDrawables
import cc.wuersan008.smartisanx.ui.basic.SmartisanIcon
import cc.wuersan008.smartisanx.ui.basic.SmartisanText
import cc.wuersan008.smartisanx.ui.layout.SmartisanScaffold
import cc.wuersan008.smartisanx.ui.layout.SmartisanTitleBarSurface

private val EmptyChromeContent: @Composable () -> Unit = {}

/**
 * Page skeleton on the Smartisan scaffold: the original striped page background, the
 * title bar with its outward shadow, and the bottom bar, all from `smartisanx`.
 */
@Composable
internal fun AsteriskScaffold(
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit = EmptyChromeContent,
    bottomBar: @Composable () -> Unit = EmptyChromeContent,
    snackbarHost: @Composable () -> Unit = EmptyChromeContent,
    floatingActionButton: @Composable () -> Unit = EmptyChromeContent,
    @Suppress("UNUSED_PARAMETER") contentWindowInsets: WindowInsets = WindowInsets(0, 0, 0, 0),
    content: @Composable (PaddingValues) -> Unit,
) {
    SmartisanScaffold(
        modifier = modifier,
        titleBar = if (topBar !== EmptyChromeContent) topBar else null,
        bottomBar = if (bottomBar !== EmptyChromeContent) bottomBar else null,
        applyNavigationBarInset = false,
        contentScrollable = true,
    ) {
        Box(Modifier.fillMaxSize()) {
            content(PaddingValues(0.dp))
            snackbarHost()
            floatingActionButton()
        }
    }
}

/**
 * Title bar in the Smartisan style: original `titlebar_bg` background, a shadow drawn
 * outside its bounds over the content, and a centered title flanked by navigation and
 * action slots (kept as composable lambdas so existing call sites keep working).
 */
@Composable
internal fun AsteriskTopAppBar(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
) {
    // Match the library title bar, but one weight lighter (Medium, not Bold): the
    // Smartisan Compact CNS font ships a real Medium face, and Bold reads too heavy here.
    val typography = LocalSmartisanTypography.current
    val titleTypography = remember(typography) { SmartisanTypography(body = typography.title) }
    SmartisanTitleBarSurface(modifier = modifier) {
        Box(Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.align(Alignment.CenterStart).padding(start = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                navigationIcon()
            }
            Row(
                modifier = Modifier.align(Alignment.CenterEnd).padding(end = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                content = actions,
            )
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(horizontal = 72.dp),
                contentAlignment = Alignment.Center,
            ) {
                CompositionLocalProvider(LocalSmartisanTypography provides titleTypography) {
                    title()
                }
            }
        }
    }
}

/** Round action button used beside the bottom navigation bar (e.g. the service power toggle). */
@Composable
internal fun AsteriskFloatingNavigationAction(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable BoxScope.() -> Unit,
) {
    val colors = LocalSmartisanColors.current
    val interaction = rememberSmartisanInteractionSource()
    val pressed by interaction.collectSmartisanPressedAsState()
    Box(
        modifier = modifier
            .size(56.dp)
            .smartisanDrawableBackground(
                drawableRes = SmartisanDrawables.BottomBarBackground,
                enabled = enabled,
                pressed = pressed && enabled,
                activated = selected,
            )
            .smartisanClickable(
                interactionSource = interaction,
                enabled = enabled,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        CompositionLocalProvider(
            LocalSmartisanContentColor provides
                if (selected) colors.accent else colors.textSecondary,
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
                content = content,
            )
        }
    }
}

/**
 * Compact bottom navigation bar in the original Smartisan tab-bar style: the
 * `bottom_bar` nine-patch background, square corners, accent-colored selected tab.
 */
@Composable
internal fun AsteriskFloatingNavigationBar(
    modifier: Modifier = Modifier,
    trailingAction: (@Composable () -> Unit)? = null,
    content: @Composable RowScope.() -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .smartisanDrawableBackground(SmartisanDrawables.BottomBarBackground)
            .padding(horizontal = 6.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            content = content,
        )
        trailingAction?.let { action ->
            Box(Modifier.padding(start = 6.dp)) { action() }
        }
    }
}

@Composable
internal fun RowScope.AsteriskFloatingNavigationItem(
    selected: Boolean,
    onClick: () -> Unit,
    icon: Int,
    label: String,
) {
    val colors = LocalSmartisanColors.current
    val typography = LocalSmartisanTypography.current
    val tint = if (selected) colors.accent else colors.textSecondary
    Column(
        modifier = Modifier
            .weight(1f)
            .smartisanClickable(onClick = onClick)
            .padding(horizontal = 2.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp, Alignment.CenterVertically),
    ) {
        AsteriskIcon(
            res = icon,
            contentDescription = null,
            tint = tint,
            size = 22.dp,
        )
        SmartisanText(
            text = label,
            style = typography.listItemCaptionSmall,
            color = tint,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
        )
    }
}
