// Copyright 2026, AsteriskMETA contributors
// SPDX-License-Identifier: GPL-3.0

package ui.components

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cc.wuersan008.smartisanx.core.anim.SmartisanMotion
import cc.wuersan008.smartisanx.core.interaction.collectSmartisanPressedAsState
import cc.wuersan008.smartisanx.core.interaction.rememberSmartisanInteractionSource
import cc.wuersan008.smartisanx.core.interaction.smartisanClick
import cc.wuersan008.smartisanx.core.theme.LocalSmartisanColors
import cc.wuersan008.smartisanx.core.theme.LocalSmartisanTypography
import cc.wuersan008.smartisanx.core.theme.SmartisanDimens
import cc.wuersan008.smartisanx.core.utils.rememberSmartisanDrawablePainter
import cc.wuersan008.smartisanx.core.utils.smartisanDrawableBackground
import cc.wuersan008.smartisanx.ui.asset.SmartisanDrawables
import cc.wuersan008.smartisanx.ui.basic.SmartisanIcon
import cc.wuersan008.smartisanx.ui.basic.SmartisanText

/**
 * One tab in [AsteriskBottomBar].
 *
 * Uses the original monochrome tab glyph; selection is conveyed by the label/icon
 * becoming darker (no accent colour in the tab bar, matching Smartisan's system bars).
 */
@Immutable
internal data class AsteriskBottomBarItem(
    @DrawableRes val iconRes: Int,
    val label: String,
)

/**
 * Smartisan bottom tab bar: original `sb_tab_bar` background, 54dp bar, navigation-bar
 * inset eaten by the bar, monochrome selection.
 *
 * The `tab_bar_shadow` is **drawn above the bar, over the content** (the same technique the
 * library's title bar uses), so it does not reserve a band between the content and the bar.
 */
@Composable
internal fun AsteriskBottomBar(
    items: List<AsteriskBottomBarItem>,
    selectedIndex: Int,
    onSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    includeNavigationBar: Boolean = true,
) {
    val shadowHeight =
        with(LocalDensity.current) {
            rememberSmartisanDrawablePainter(SmartisanDrawables.BottomBarShadow)
                .intrinsicSize.height.toDp()
        }
    Box(modifier.fillMaxWidth()) {
        Box(
            Modifier
                .fillMaxWidth()
                .smartisanDrawableBackground(SmartisanDrawables.BottomBarBackground),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .then(
                        if (includeNavigationBar) {
                            Modifier.windowInsetsPadding(WindowInsets.navigationBars)
                        } else {
                            Modifier
                        },
                    )
                    .height(SmartisanDimens.BottomBarHeight),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                items.forEachIndexed { index, item ->
                    AsteriskBottomBarItemView(
                        item = item,
                        selected = index == selectedIndex,
                        modifier = Modifier.weight(1f),
                        onClick = { onSelected(index) },
                    )
                }
            }
        }
        // Shadow drawn outside the bar, over the content (does not occupy layout space).
        Box(
            Modifier
                .align(Alignment.TopStart)
                .fillMaxWidth()
                .offset(y = -shadowHeight)
                .height(shadowHeight)
                .smartisanDrawableBackground(SmartisanDrawables.BottomBarShadow),
        )
    }
}

@Composable
private fun AsteriskBottomBarItemView(
    item: AsteriskBottomBarItem,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val colors = LocalSmartisanColors.current
    val typography = LocalSmartisanTypography.current
    val interaction = rememberSmartisanInteractionSource()
    val pressed by interaction.collectSmartisanPressedAsState()
    val click = smartisanClick(onClick)
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.94f else 1f,
        animationSpec = SmartisanMotion.PressSpring,
        label = "asterisk bottom bar press",
    )
    val tint = if (selected) colors.textPrimary else colors.textTertiary
    Column(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable(
                interactionSource = interaction,
                indication = null,
                role = Role.Tab,
                onClick = click,
            )
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        SmartisanIcon(
            res = item.iconRes,
            contentDescription = item.label,
            modifier = Modifier.height(SmartisanDimens.BottomBarIconSize),
        )
        SmartisanText(
            text = item.label,
            modifier = Modifier.padding(top = 1.dp),
            style = typography.caption.copy(fontSize = 10.sp),
            color = tint,
            maxLines = 1,
        )
    }
}
