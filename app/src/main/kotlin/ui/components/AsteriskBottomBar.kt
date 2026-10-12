// Copyright 2026, AsteriskMETA contributors
// SPDX-License-Identifier: GPL-3.0

package ui.components

import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.foundation.Image
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import cc.wuersan008.smartisanx.ui.basic.SmartisanText
import ui.isInDarkTheme

/**
 * Original system tab-bar grey pair, measured from the ROM's own tab glyphs
 * (`icon_nav_*` normal `#B4B4B4` / pressed `#5C5C5C`): inactive tabs are light grey,
 * the active tab is the darkened glyph.
 */
private val AsteriskBottomBarSelectedTint = Color(0xFF5C5C5C)
private val AsteriskBottomBarUnselectedTint = Color(0xFFB4B4B4)

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
    val pressScale by animateFloatAsState(
        targetValue = if (pressed) 0.94f else 1f,
        animationSpec = SmartisanMotion.PressSpring,
        label = "asterisk bottom bar press",
    )
    // Original behaviour: the active tab's glyph is tinted darker and grows slightly.
    val iconScale by animateFloatAsState(
        targetValue = if (selected) 1.12f else 1f,
        animationSpec = tween(durationMillis = SmartisanMotion.DurationShort, easing = SmartisanMotion.EaseInOut),
        label = "asterisk bottom bar icon scale",
    )
    // Original system tab bar: unselected glyphs are light grey, the active one darker grey.
    val tint = if (isInDarkTheme()) {
        if (selected) colors.textPrimary else colors.textTertiary
    } else {
        if (selected) AsteriskBottomBarSelectedTint else AsteriskBottomBarUnselectedTint
    }
    val painter = rememberSmartisanDrawablePainter(item.iconRes)
    Column(
        modifier = modifier
            .graphicsLayer {
                scaleX = pressScale
                scaleY = pressScale
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
        Image(
            painter = painter,
            contentDescription = item.label,
            modifier = Modifier
                .size(SmartisanDimens.BottomBarIconSize)
                .graphicsLayer {
                    scaleX = iconScale
                    scaleY = iconScale
                },
            colorFilter = ColorFilter.tint(tint),
            contentScale = ContentScale.Fit,
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
