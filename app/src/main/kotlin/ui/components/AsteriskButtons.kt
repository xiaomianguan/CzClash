// Copyright 2026, AsteriskMETA contributors
// SPDX-License-Identifier: GPL-3.0

package ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cc.wuersan008.smartisanx.ui.control.SmartisanButton
import cc.wuersan008.smartisanx.ui.control.SmartisanButtonStyle
import cc.wuersan008.smartisanx.ui.control.SmartisanTextButton

/**
 * Buttons backed by the Smartisan button component (original nine-patch selectors,
 * press shrink, no Material ripple).
 *
 * The icon and colors parameters are kept for call-site compatibility; the Smartisan
 * buttons use their original artwork and the theme's link/accent colors instead.
 */
@Composable
internal fun AsteriskTonalButton(
    text: String,
    icon: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    SmartisanButton(
        text = text,
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        style = SmartisanButtonStyle.Neutral,
    )
}

@Composable
internal fun AsteriskActionButton(
    text: String,
    icon: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    SmartisanTextButton(
        text = text,
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
    )
}

@Composable
internal fun AsteriskExtendedFab(
    text: String,
    icon: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    SmartisanButton(
        text = text,
        onClick = onClick,
        modifier = modifier,
        style = SmartisanButtonStyle.Accent,
    )
}
