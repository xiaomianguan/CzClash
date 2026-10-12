// Copyright 2026, AsteriskMETA contributors
// SPDX-License-Identifier: GPL-3.0

package ui.components

import cc.wuersan008.smartisanx.ui.layout.SmartisanGroupRowPosition
import cc.wuersan008.smartisanx.ui.layout.SmartisanGroup
import cc.wuersan008.smartisanx.core.utils.smartisanProjectedShadow
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.heightIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import cc.wuersan008.smartisanx.core.interaction.smartisanClickable
import cc.wuersan008.smartisanx.core.theme.LocalSmartisanColors
import cc.wuersan008.smartisanx.ui.layout.SmartisanCard
import ui.theme.ExpressiveInteractionState
import ui.theme.ExpressiveShapeRole
import ui.theme.expressiveShape

@Composable
internal fun rememberExpressiveShape(
    role: ExpressiveShapeRole,
    @Suppress("UNUSED_PARAMETER") state: ExpressiveInteractionState,
): Shape = remember(role) { expressiveShape(role) }

@Composable
internal fun AsteriskExpressiveCard(
    modifier: Modifier = Modifier,
    role: ExpressiveShapeRole = ExpressiveShapeRole.ContentCard,
    selected: Boolean = false,
    expanded: Boolean = false,
    enabled: Boolean = true,
    onClick: (() -> Unit)? = null,
    containerColor: Color = Color.Unspecified,
    selectedContainerColor: Color = Color.Unspecified,
    @Suppress("UNUSED_PARAMETER") border: BorderStroke? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val colors = LocalSmartisanColors.current
    val interactionState = when {
        !enabled -> ExpressiveInteractionState.Disabled
        expanded -> ExpressiveInteractionState.Expanded
        selected -> ExpressiveInteractionState.Selected
        else -> ExpressiveInteractionState.Rest
    }
    // Same group-card texture + 12dp margin + outward shadow as the rest of the app.
    val cardModifier = if (onClick == null) {
        modifier
    } else {
        modifier
            .heightIn(min = 48.dp)
            .smartisanClickable(enabled = enabled, onClick = onClick)
    }
    SmartisanGroup(
        modifier = cardModifier,
        position = SmartisanGroupRowPosition.Single,
    ) {
        content()
    }
}
