// Copyright 2026, AsteriskMETA contributors
// SPDX-License-Identifier: GPL-3.0

package ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import app.R
import cc.wuersan008.smartisanx.ui.overlay.SmartisanMenuDialog
import cc.wuersan008.smartisanx.ui.overlay.SmartisanMenuItem
import ui.clipboard.ClipboardImportMode

@Composable
internal fun ImportModeDialog(
    show: Boolean,
    title: String,
    message: String,
    onDismissRequest: () -> Unit,
    onModeSelected: (ClipboardImportMode) -> Unit,
) {
    if (!show) return
    SmartisanMenuDialog(
        onDismissRequest = onDismissRequest,
        title = title,
    ) {
        SmartisanMenuItem(
            text = stringResource(R.string.common_merge_import),
            onClick = { onModeSelected(ClipboardImportMode.Merge) },
        )
        SmartisanMenuItem(
            text = stringResource(R.string.common_replace_existing),
            onClick = { onModeSelected(ClipboardImportMode.Replace) },
        )
        SmartisanMenuItem(
            text = stringResource(R.string.common_cancel),
            onClick = onDismissRequest,
        )
    }
}
