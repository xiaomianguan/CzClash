// Copyright 2026, AsteriskMETA contributors
// SPDX-License-Identifier: GPL-3.0

package ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cc.wuersan008.smartisanx.ui.control.SmartisanCheckbox

/** Smartisan checkbox (original `selector_check_box_red` bitmaps, no ripple). */
@Composable
internal fun AsteriskCheckbox(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    SmartisanCheckbox(
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = modifier,
        enabled = enabled,
    )
}
