// Copyright 2026, AsteriskMETA contributors
// SPDX-License-Identifier: GPL-3.0

package ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cc.wuersan008.smartisanx.core.utils.smartisanColor
import cc.wuersan008.smartisanx.ui.overlay.SmartisanDialog
import cc.wuersan008.smartisanx.ui.basic.SmartisanText
import cc.wuersan008.smartisanx.ui.R as SmartisanR

/**
 * Two-action confirmation in the Smartisan dialog style. The dialog content area uses the
 * original always-light `smartisan_modal_content_background`, so the text color is fixed
 * to the original primary text color rather than the (possibly dark) theme color.
 */
@Composable
internal fun WarningConfirmDialog(
    show: Boolean,
    title: String,
    summary: String,
    dismissText: String,
    confirmText: String,
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
    detailsMaxHeight: Dp = 240.dp,
    details: (@Composable ColumnScope.() -> Unit)? = null,
) {
    if (!show) return
    val contentColor = smartisanColor(SmartisanR.color.smartisan_text_primary)
    SmartisanDialog(
        onDismissRequest = onDismissRequest,
        title = title,
        confirmText = confirmText,
        dismissText = dismissText,
        onConfirm = onConfirm,
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            SmartisanText(
                text = summary,
                modifier = Modifier.fillMaxWidth(),
                color = contentColor,
                textAlign = TextAlign.Center,
            )
            if (details != null) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                        .heightIn(max = detailsMaxHeight)
                        .verticalScroll(rememberScrollState()),
                    content = details,
                )
            }
        }
    }
}
