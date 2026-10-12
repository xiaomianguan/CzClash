// Copyright 2026, AsteriskMETA contributors
// SPDX-License-Identifier: GPL-3.0

package ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import cc.wuersan008.smartisanx.core.theme.LocalSmartisanColors
import cc.wuersan008.smartisanx.ui.basic.SmartisanIcon
import cc.wuersan008.smartisanx.ui.basic.SmartisanText
import ui.icons.AsteriskIcons as Icons

/** Place inside the clickable trigger so the menu follows its arrow, not its label. */
@Composable
internal fun AsteriskDropdownAnchor(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    menuModifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Box(modifier = modifier.size(24.dp), contentAlignment = Alignment.Center) {
        AsteriskIcon(Icons.Rounded.ExpandMore, contentDescription = null)
    }
    AsteriskPopupMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        modifier = menuModifier,
        content = content,
    )
}

@Composable
internal fun AsteriskDropdownMenuItem(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    enabled: Boolean = true,
) {
    val colors = LocalSmartisanColors.current
    AsteriskPopupMenuItem(
        onClick = onClick,
        enabled = enabled,
        leadingIcon = {
            if (selected) {
                AsteriskIcon(
                    res = Icons.Rounded.Check,
                    contentDescription = null,
                    tint = colors.accent,
                )
            }
        },
        text = {
            SmartisanText(
                text = text,
                color = if (selected) colors.accent else colors.textPrimary,
                modifier = Modifier.semantics { this.selected = selected },
            )
        },
    )
}
