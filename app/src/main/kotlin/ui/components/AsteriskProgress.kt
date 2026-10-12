// Copyright 2026, AsteriskMETA contributors
// SPDX-License-Identifier: GPL-3.0

package ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cc.wuersan008.smartisanx.core.theme.LocalSmartisanColors
import cc.wuersan008.smartisanx.ui.control.SmartisanCircleProgressIndeterminate
import cc.wuersan008.smartisanx.ui.control.SmartisanProgressBar

/**
 * Smartisan indeterminate spinner, sized explicitly (the underlying component sizes its
 * own canvas, so the size must be passed rather than applied through a modifier).
 */
@Composable
internal fun AsteriskSpinner(
    modifier: Modifier = Modifier,
    size: Dp = 24.dp,
    color: Color = Color.Unspecified,
) {
    SmartisanCircleProgressIndeterminate(
        modifier = modifier,
        size = size,
        arcColor = if (color == Color.Unspecified) LocalSmartisanColors.current.accent else color,
    )
}

/** Smartisan horizontal progress bar (thin variant). */
@Composable
internal fun AsteriskProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
) {
    SmartisanProgressBar(
        progress = progress,
        modifier = modifier,
        thin = true,
    )
}
