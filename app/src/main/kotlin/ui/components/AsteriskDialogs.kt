// Copyright 2026, AsteriskMETA contributors
// SPDX-License-Identifier: GPL-3.0

package ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cc.wuersan008.smartisanx.core.theme.LocalSmartisanContentColor
import cc.wuersan008.smartisanx.core.theme.SmartisanDimens
import cc.wuersan008.smartisanx.ui.overlay.SmartisanDialogButton
import cc.wuersan008.smartisanx.ui.overlay.SmartisanDialogTitleBar
import cc.wuersan008.smartisanx.ui.overlay.SmartisanModalWindow

/**
 * The original dialog content area is always light (its background has no night variant),
 * so dialog text keeps the original primary text color regardless of the app theme.
 */
internal val DialogTextColor = Color(0xCC000000)

/**
 * Smartisan-styled dialog with a title bar, free-form content and a caller-supplied button row.
 *
 * Use this when a dialog needs custom content (progress, scrollable details) or a button
 * layout that [cc.wuersan008.smartisanx.ui.overlay.SmartisanDialog] /
 * [cc.wuersan008.smartisanx.ui.overlay.SmartisanConfirmDialog] cannot express.
 */
@Composable
internal fun AsteriskModalDialog(
    title: String,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: androidx.compose.foundation.layout.PaddingValues =
        androidx.compose.foundation.layout.PaddingValues(horizontal = 18.dp, vertical = 16.dp),
    buttons: @Composable RowScope.() -> Unit,
    content: @Composable ColumnScope.() -> Unit,
) {
    SmartisanModalWindow(onDismissRequest = onDismissRequest, modifier = modifier) {
        SmartisanDialogTitleBar(title = title, onDismiss = onDismissRequest)
        CompositionLocalProvider(
            LocalSmartisanContentColor provides DialogTextColor,
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(contentPadding),
                content = content,
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth().height(SmartisanDimens.DialogButtonHeight),
            content = buttons,
        )
    }
}
