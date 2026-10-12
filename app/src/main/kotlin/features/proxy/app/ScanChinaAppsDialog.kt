// Copyright 2026, AsteriskMETA contributors
// SPDX-License-Identifier: GPL-3.0

package features.proxy.app

import cc.wuersan008.smartisanx.core.theme.LocalSmartisanTypography
import cc.wuersan008.smartisanx.core.theme.LocalSmartisanColors
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import ui.components.AsteriskProgressBar
import cc.wuersan008.smartisanx.ui.basic.SmartisanText
import cc.wuersan008.smartisanx.ui.overlay.SmartisanDialogButton
import ui.components.AsteriskModalDialog
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import app.R

@Composable
internal fun ScanChinaAppsDialog(
    progress: ScanProgressState?,
    onCancel: () -> Unit,
) {
    if (progress == null) return
    val listState = rememberLazyListState()
    var previousSize by remember { mutableIntStateOf(0) }

    LaunchedEffect(progress.matched.size) {
        if (progress.matched.isNotEmpty() && progress.matched.size > previousSize) {
            listState.scrollToItem(progress.matched.size - 1)
        }
        previousSize = progress.matched.size
    }

    AsteriskModalDialog(
        title = stringResource(R.string.proxy_app_list_scan_china_apps),
        onDismissRequest = {},
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 12.dp),
        buttons = {
            SmartisanDialogButton(
                text = stringResource(R.string.common_cancel),
                onClick = onCancel,
                modifier = Modifier.weight(1f).fillMaxHeight(),
                accent = false,
            )
        },
    ) {
        Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(360.dp)
                    .padding(top = 4.dp),
            ) {
                val progressText = stringResource(
                    R.string.proxy_app_list_scan_china_progress,
                    progress.scanned,
                    progress.total,
                    progress.matched.size,
                )
                SmartisanText(
                    text = progressText,
                    style = LocalSmartisanTypography.current.listItemSecondary,
                    color = LocalSmartisanColors.current.textSecondary,
                )
                Spacer(Modifier.height(8.dp))
                val ratio = if (progress.total > 0) {
                    progress.scanned.toFloat() / progress.total.toFloat()
                } else {
                    0f
                }
                AsteriskProgressBar(
                    progress =  ratio.coerceIn(0f, 1f) ,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                )
                if (progress.matched.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center,
                    ) {
                        SmartisanText(
                            text = stringResource(R.string.common_empty),
                            style = LocalSmartisanTypography.current.listItemSecondary,
                            color = LocalSmartisanColors.current.textSecondary,
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        state = listState,
                    ) {
                        items(
                            items = progress.matched,
                            key = { item -> item.key },
                        ) { item ->
                            SmartisanText(
                                text = "${item.label} (${item.packageName})",
                                style = LocalSmartisanTypography.current.body,
                                modifier = Modifier.padding(vertical = 4.dp),
                            )
                        }
                    }
                }
            }
        }
}

