// Copyright 2026, AsteriskMETA contributors
// SPDX-License-Identifier: GPL-3.0

package ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cc.wuersan008.smartisanx.core.interaction.smartisanClickable
import cc.wuersan008.smartisanx.core.theme.LocalSmartisanColors
import cc.wuersan008.smartisanx.core.theme.LocalSmartisanShapes
import cc.wuersan008.smartisanx.core.theme.LocalSmartisanTypography
import cc.wuersan008.smartisanx.core.utils.smartisanDrawableBackground
import cc.wuersan008.smartisanx.ui.asset.SmartisanDrawables
import cc.wuersan008.smartisanx.ui.basic.SmartisanText
import cc.wuersan008.smartisanx.ui.layout.SmartisanGroup
import cc.wuersan008.smartisanx.ui.layout.SmartisanGroupRowPosition
import cc.wuersan008.smartisanx.ui.overlay.SmartisanModal

@Composable
internal fun AsteriskPageCard(
    modifier: Modifier = Modifier,
    @Suppress("UNUSED_PARAMETER") selected: Boolean = false,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    // Use the original Smartisan group-card texture (content 9-patch + outward shadow).
    SmartisanGroup(
        modifier = modifier.then(
            if (onClick != null) Modifier.smartisanClickable(onClick = onClick) else Modifier,
        ),
        position = SmartisanGroupRowPosition.Single,
    ) {
        content()
    }
}

/**
 * Bottom sheet rebuilt on the Smartisan modal window: the original `menu_dialog` background,
 * a square-cornered panel anchored to the bottom, and the existing header/action slots.
 */
@Composable
internal fun AsteriskModalBottomSheet(
    show: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    dismissEnabled: Boolean = true,
    title: String? = null,
    startAction: @Composable () -> Unit = EmptySheetAction,
    endAction: @Composable () -> Unit = EmptySheetAction,
    content: @Composable ColumnScope.() -> Unit,
) {
    if (!show) return
    val typography = LocalSmartisanTypography.current
    val colors = LocalSmartisanColors.current
    val shapes = LocalSmartisanShapes.current
    SmartisanModal(
        onDismissRequest = { if (dismissEnabled) onDismissRequest() },
        modifier = modifier,
        bottom = true,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(shapes.sheet)
                .smartisanDrawableBackground(SmartisanDrawables.MenuDialogBackground),
        ) {
            if (title != null || startAction !== EmptySheetAction || endAction !== EmptySheetAction) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 56.dp)
                        .padding(start = 6.dp, end = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                        startAction()
                    }
                    Box(modifier = Modifier.weight(1.4f), contentAlignment = Alignment.Center) {
                        title?.let { sheetTitle ->
                            SmartisanText(
                                text = sheetTitle,
                                style = typography.dialogTitle,
                                color = colors.textSecondary,
                                textAlign = TextAlign.Center,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterEnd) {
                        endAction()
                    }
                }
            }
            content()
        }
    }
}

private val EmptySheetAction: @Composable () -> Unit = {}
