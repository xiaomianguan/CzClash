// Copyright 2026, AsteriskMETA contributors
// SPDX-License-Identifier: GPL-3.0

package ui.components

import cc.wuersan008.smartisanx.ui.layout.SmartisanSectionTitle
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cc.wuersan008.smartisanx.core.theme.LocalSmartisanColors
import cc.wuersan008.smartisanx.core.theme.LocalSmartisanShapes
import cc.wuersan008.smartisanx.core.theme.LocalSmartisanTypography
import cc.wuersan008.smartisanx.ui.basic.SmartisanIcon
import cc.wuersan008.smartisanx.ui.basic.SmartisanText
import cc.wuersan008.smartisanx.ui.layout.SmartisanGroup
import cc.wuersan008.smartisanx.ui.layout.SmartisanGroupRowPosition
import cc.wuersan008.smartisanx.ui.layout.SmartisanListItem

private val EmptyTrailingContent: @Composable RowScope.() -> Unit = {}
private val EmptySectionAction: @Composable RowScope.() -> Unit = {}

@Composable
internal fun AsteriskSection(
    modifier: Modifier = Modifier,
    title: String? = null,
    action: @Composable RowScope.() -> Unit = EmptySectionAction,
    content: @Composable ColumnScope.() -> Unit,
) {
    val colors = LocalSmartisanColors.current
    val typography = LocalSmartisanTypography.current
    Column(modifier = modifier) {
        if (title != null) {
            if (action === EmptySectionAction) {
                // Library group title (grey, canonical rhythm).
                SmartisanSectionTitle(text = title)
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 18.dp, end = 18.dp, top = 19.5.dp, bottom = 5.5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    SmartisanText(
                        text = title,
                        modifier = Modifier.weight(1f),
                        style = typography.sectionTitle,
                        color = colors.textTertiary,
                        maxLines = 1,
                    )
                    action()
                }
            }
        }
        SmartisanGroup(position = SmartisanGroupRowPosition.Single) { Column(content = content) }
    }
}

@Composable
internal fun AsteriskListRow(
    title: String,
    modifier: Modifier = Modifier,
    summary: String? = null,
    status: String? = null,
    leadingIcon: Int? = null,
    leadingContentDescription: String? = null,
    enabled: Boolean = true,
    selected: Boolean? = null,
    role: Role? = null,
    onClick: (() -> Unit)? = null,
    horizontalPadding: Dp = 16.dp,
    trailingContent: @Composable RowScope.() -> Unit = EmptyTrailingContent,
) {
    val colors = LocalSmartisanColors.current
    val typography = LocalSmartisanTypography.current
    val hasTrailing = status != null || trailingContent !== EmptyTrailingContent
    SmartisanListItem(
        title = title,
        modifier = modifier,
        summary = summary,
        leading = leadingIcon?.let { icon ->
            {
                AsteriskIcon(
                    res = icon,
                    contentDescription = leadingContentDescription,
                    tint = if (selected == true) colors.accent else colors.textSecondary,
                    size = 24.dp,
                )
            }
        },
        trailing = if (hasTrailing) {
            {
                if (status != null) {
                    SmartisanText(
                        text = status,
                        modifier = Modifier.width(88.dp),
                        style = typography.listItemSecondary,
                        color = colors.textTertiary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    trailingContent()
                }
            }
        } else {
            null
        },
        enabled = enabled,
        selected = selected == true,
        contentPadding = horizontalPadding,
        onClick = onClick,
    )
}
