// Copyright 2026, AsteriskMETA contributors
// SPDX-License-Identifier: GPL-3.0

package ui.components

import cc.wuersan008.smartisanx.core.theme.LocalSmartisanTypography
import cc.wuersan008.smartisanx.core.theme.LocalSmartisanColors
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import cc.wuersan008.smartisanx.ui.basic.SmartisanText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
internal fun AsteriskValueRow(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    titleMinWidth: Dp = 96.dp,
    valueMaxLines: Int = 2,
    verticalAlignment: Alignment.Vertical = Alignment.Top,
    trailing: (@Composable () -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp)
            .padding(vertical = 10.dp),
        verticalAlignment = verticalAlignment,
    ) {
        SmartisanText(
            text = title,
            modifier = Modifier.widthIn(min = titleMinWidth, max = titleMinWidth),
            style = LocalSmartisanTypography.current.body,
            color = LocalSmartisanColors.current.textSecondary,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(Modifier.width(12.dp))
        SmartisanText(
            text = value,
            modifier = Modifier.weight(1f),
            style = LocalSmartisanTypography.current.body,
            color = LocalSmartisanColors.current.textPrimary,
            textAlign = TextAlign.End,
            maxLines = valueMaxLines,
            overflow = TextOverflow.Ellipsis,
        )
        trailing?.let { content ->
            Spacer(Modifier.width(8.dp))
            content()
        }
    }
}
