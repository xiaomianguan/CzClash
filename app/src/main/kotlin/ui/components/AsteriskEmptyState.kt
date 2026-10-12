// Copyright 2026, AsteriskMETA contributors
// SPDX-License-Identifier: GPL-3.0

package ui.components

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cc.wuersan008.smartisanx.ui.asset.SmartisanOriginalIcons
import cc.wuersan008.smartisanx.ui.layout.SmartisanEmptyHint

/** Smartisan empty state: the original large blank-page illustration + title/summary. */
@Composable
internal fun AsteriskEmptyState(
    title: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    @DrawableRes iconRes: Int = SmartisanOriginalIcons.EmptyPlaylist,
    action: (@Composable () -> Unit)? = null,
) {
    SmartisanEmptyHint(
        title = title,
        modifier = modifier,
        description = description,
        iconRes = iconRes,
        action = action,
    )
}
