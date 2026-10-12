// Copyright 2026, AsteriskMETA contributors
// SPDX-License-Identifier: GPL-3.0

package ui.components

import cc.wuersan008.smartisanx.core.theme.LocalSmartisanTypography
import cc.wuersan008.smartisanx.core.theme.LocalSmartisanColors
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import cc.wuersan008.smartisanx.ui.basic.SmartisanText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
internal fun AsteriskContentHeader(
    modifier: Modifier = Modifier,
    status: String? = null,
    controls: @Composable RowScope.() -> Unit = EmptyHeaderControls,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        content()
        if (status != null || controls !== EmptyHeaderControls) {
            Row(
                modifier = Modifier.fillMaxWidth().heightIn(min = 40.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (status == null) {
                    Spacer(Modifier.weight(1f))
                } else {
                    SmartisanText(
                        text = status,
                        modifier = Modifier.weight(1f),
                        style = LocalSmartisanTypography.current.listItemSecondary,
                        color = LocalSmartisanColors.current.textSecondary,
                    )
                }
                controls()
            }
        }
    }
}

private val EmptyHeaderControls: @Composable RowScope.() -> Unit = {}
