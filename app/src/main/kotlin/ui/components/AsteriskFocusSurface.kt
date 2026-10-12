// Copyright 2026, AsteriskMETA contributors
// SPDX-License-Identifier: GPL-3.0

package ui.components

import cc.wuersan008.smartisanx.ui.layout.SmartisanGroup
import cc.wuersan008.smartisanx.ui.layout.SmartisanGroupRowPosition
import cc.wuersan008.smartisanx.core.interaction.smartisanClickable
import cc.wuersan008.smartisanx.ui.layout.SmartisanCard
import cc.wuersan008.smartisanx.core.theme.LocalSmartisanTypography
import cc.wuersan008.smartisanx.core.theme.LocalSmartisanColors
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import cc.wuersan008.smartisanx.ui.basic.SmartisanIcon
import cc.wuersan008.smartisanx.ui.basic.SmartisanText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ui.theme.ExpressiveInteractionState
import ui.theme.FocusDensity
import ui.theme.FocusTone
import ui.theme.focusShapeRole

private const val PrimaryActionStackFontScale = 1.3f

internal fun shouldStackPrimaryAction(
    fontScale: Float,
    keepPrimaryActionInline: Boolean,
): Boolean = !keepPrimaryActionInline && fontScale >= PrimaryActionStackFontScale

@Composable
internal fun focusAccentColor(tone: FocusTone): Color = when (tone) {
    FocusTone.Primary -> LocalSmartisanColors.current.accent
    FocusTone.Inactive -> LocalSmartisanColors.current.textSecondary
    FocusTone.Warning -> LocalSmartisanColors.current.warning
    FocusTone.Error -> LocalSmartisanColors.current.warning
    FocusTone.ReadOnly -> LocalSmartisanColors.current.success
}

@Composable
private fun RowScope.FocusTitleAndSummary(
    title: String,
    summary: String?,
) {
    Column(
        modifier = Modifier.weight(1f),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        SmartisanText(
            text = title,
            modifier = Modifier.semantics { heading() },
            style = LocalSmartisanTypography.current.title.copy(fontWeight = FontWeight.SemiBold),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        if (summary != null) {
            SmartisanText(
                text = summary,
                style = LocalSmartisanTypography.current.body,
                color = LocalSmartisanColors.current.textSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
internal fun AsteriskFocusSurface(
    title: String,
    modifier: Modifier = Modifier,
    density: FocusDensity = FocusDensity.Medium,
    tone: FocusTone = FocusTone.Primary,
    summary: String? = null,
    stateIcon: Int? = null,
    metrics: (@Composable RowScope.() -> Unit)? = null,
    primaryAction: (@Composable BoxScope.() -> Unit)? = null,
    keepPrimaryActionInline: Boolean = false,
    forcePrimaryActionStacked: Boolean = false,
    content: (@Composable ColumnScope.() -> Unit)? = null,
) {
    val shape = rememberExpressiveShape(
        role = focusShapeRole(density),
        state = ExpressiveInteractionState.Rest,
    )
    val accentColor = focusAccentColor(tone)
    val stackPrimaryAction = forcePrimaryActionStacked || shouldStackPrimaryAction(
        fontScale = LocalDensity.current.fontScale,
        keepPrimaryActionInline = keepPrimaryActionInline,
    )
    val horizontalPadding = when (density) {
        FocusDensity.Large -> 24.dp
        FocusDensity.Medium -> 20.dp
        FocusDensity.Compact -> 16.dp
    }

    SmartisanGroup(
        modifier = modifier.fillMaxWidth(),
        position = SmartisanGroupRowPosition.Single,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (stateIcon != null) {
                        AsteriskIcon(
                            res = stateIcon,
                            contentDescription = null,
                            modifier = Modifier.size(if (density == FocusDensity.Large) 32.dp else 28.dp),
                            tint = accentColor,
                        )
                    }
                    FocusTitleAndSummary(
                        title = title,
                        summary = summary,
                    )
                    if (!stackPrimaryAction && primaryAction != null) {
                        Box(content = primaryAction)
                    }
                }
                if (stackPrimaryAction && primaryAction != null) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.CenterEnd,
                        content = primaryAction,
                    )
                }
            }
            if (metrics != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    content = metrics,
                )
            }
            if (content != null) {
                content()
            }
            if (density == FocusDensity.Large) {
                Spacer(Modifier.height(2.dp))
            }
        }
    }
}
