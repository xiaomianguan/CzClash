// Copyright 2026, AsteriskMETA contributors
// SPDX-License-Identifier: GPL-3.0

package ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cc.wuersan008.smartisanx.core.interaction.smartisanClickable
import cc.wuersan008.smartisanx.core.theme.LocalSmartisanColors
import cc.wuersan008.smartisanx.core.theme.LocalSmartisanContentColor

/**
 * Text button with an arbitrary content row, rendered in the Smartisan link style
 * (accent-colored content, no ripple, press via `smartisanClickable`).
 *
 * Replaces the Material `TextButton` without changing call sites that build their own
 * icon + label content.
 */
@Composable
internal fun AsteriskLinkButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit,
) {
    val colors = LocalSmartisanColors.current
    CompositionLocalProvider(
        LocalSmartisanContentColor provides if (enabled) colors.accent else colors.textDisabled,
    ) {
        Row(
            modifier = modifier
                .defaultMinSize(minHeight = 44.dp)
                .smartisanClickable(enabled = enabled, onClick = onClick)
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            content = content,
        )
    }
}
