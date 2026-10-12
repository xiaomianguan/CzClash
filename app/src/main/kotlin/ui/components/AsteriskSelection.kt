// Copyright 2026, AsteriskMETA contributors
// SPDX-License-Identifier: GPL-3.0

package ui.components

import cc.wuersan008.smartisanx.core.theme.LocalSmartisanColors
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
internal fun AsteriskSelectionCard(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    containerColor: Color = LocalSmartisanColors.current.surface,
    selectedContainerColor: Color = LocalSmartisanColors.current.surfaceRaised,
    content: @Composable ColumnScope.() -> Unit,
) {
    AsteriskExpressiveCard(
        modifier = modifier,
        selected = selected,
        enabled = enabled,
        onClick = onClick,
        containerColor = containerColor,
        selectedContainerColor = selectedContainerColor,
        content = content,
    )
}
