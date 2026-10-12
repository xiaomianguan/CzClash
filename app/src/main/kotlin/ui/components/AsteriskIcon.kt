// Copyright 2026, AsteriskMETA contributors
// SPDX-License-Identifier: GPL-3.0

package ui.components

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import cc.wuersan008.smartisanx.ui.basic.SmartisanIcon

/**
 * Renders an original Smartisan (smartisanx) icon resource.
 *
 * `tint` is accepted for call-site compatibility but intentionally ignored: the original
 * selectors carry their own normal / pressed / disabled colours, matching Smartisan OS.
 */
@Composable
internal fun AsteriskIcon(
    @DrawableRes res: Int,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    pressed: Boolean = false,
    selected: Boolean = false,
    checked: Boolean = false,
    size: Dp? = null,
    contentScale: ContentScale = ContentScale.Fit,
    @Suppress("UNUSED_PARAMETER") tint: Color = Color.Unspecified,
) {
    SmartisanIcon(
        res = res,
        contentDescription = contentDescription,
        modifier = modifier,
        enabled = enabled,
        pressed = pressed,
        selected = selected,
        checked = checked,
        size = size,
        contentScale = contentScale,
    )
}
