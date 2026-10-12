// Copyright 2026, AsteriskMETA contributors
// SPDX-License-Identifier: GPL-3.0

package features.proxy

import cc.wuersan008.smartisanx.core.theme.LocalSmartisanTypography
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.verticalScroll
import cc.wuersan008.smartisanx.ui.overlay.SmartisanDialogButton
import ui.components.AsteriskModalDialog
import cc.wuersan008.smartisanx.ui.basic.SmartisanText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import app.R
import engine.root.runtime.ProxyErrorBus
import engine.root.runtime.ProxyErrorExplanation
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Subscribes to [ProxyErrorBus] and renders the failure dialog whenever a non-null explanation
 * is published. Renders nothing while the bus slot is empty.
 *
 * Hosted at the app content level so the dialog is visible whichever destination the user is on
 * — proxy start failures are triggered from the home page toggle.
 *
 * The dialog carries everything needed for a bug report: the real error, a device/system
 * summary, the logs written by the failed attempt, and (only when a confirmed rule matched)
 * suggested actions. Copy exports the diagnostic data; the app's own suggestions are excluded.
 */
@Composable
internal fun ProxyErrorHost() {
    val explanation by ProxyErrorBus.observe().collectAsState(initial = null)
    val current = explanation
    if (current != null) {
        ProxyErrorDialog(
            explanation = current,
            // Pass the explanation that is actually on screen: if a newer failure replaced it
            // while this dialog was open, dismissing this one must not discard that failure.
            onDismiss = { ProxyErrorBus.acknowledge(current) },
        )
    }
}

@Composable
private fun ProxyErrorDialog(
    explanation: ProxyErrorExplanation,
    onDismiss: () -> Unit,
) {
    val context = LocalContext.current
    val timestamp = remember(explanation.occurredAtEpochMillis) {
        SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            .format(Date(explanation.occurredAtEpochMillis))
    }
    val detailsLabel = stringResource(R.string.proxy_error_dialog_details)
    val deviceLabel = stringResource(R.string.proxy_error_dialog_device_info)
    val serviceLogLabel = stringResource(R.string.proxy_error_dialog_service_log)
    val diagnosticsLabel = stringResource(R.string.proxy_error_dialog_diagnostics)
    val copiedLabel = stringResource(R.string.proxy_error_dialog_copied)
    val modeLine = stringResource(R.string.proxy_error_dialog_mode_title, explanation.mode)
    val timeLine = stringResource(R.string.proxy_error_dialog_occurred_at, timestamp)
    val suggestions = explanation.diagnostics.map { id -> stringResource(id) }

    // Copy carries every section shown in the dialog so the result is self-contained as a bug
    // report — the app's own suggestions included.
    val copyPayload = remember(
        explanation,
        suggestions,
        modeLine,
        timeLine,
        detailsLabel,
        deviceLabel,
        serviceLogLabel,
        diagnosticsLabel,
    ) {
        buildString {
            appendLine(modeLine)
            appendLine(timeLine)

            appendLine()
            appendLine(detailsLabel)
            appendLine(explanation.rawMessage)

            if (explanation.hasDeviceInfo) {
                appendLine()
                appendLine(deviceLabel)
                appendLine(explanation.deviceInfo)
            }

            if (explanation.hasServiceLog) {
                appendLine()
                appendLine(serviceLogLabel)
                appendLine(explanation.serviceLog)
            }

            if (suggestions.isNotEmpty()) {
                appendLine()
                appendLine(diagnosticsLabel)
                suggestions.forEach { appendLine("• $it") }
            }
        }.trimEnd()
    }

    AsteriskModalDialog(
        title = stringResource(R.string.proxy_error_dialog_title),
        onDismissRequest = onDismiss,
        buttons = {
            SmartisanDialogButton(
                text = stringResource(R.string.proxy_error_dialog_copy),
                onClick = {
                    copyToClipboard(context, copyPayload)
                    runCatching {
                        android.widget.Toast.makeText(context, copiedLabel, android.widget.Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.weight(1f).fillMaxHeight(),
                accent = false,
            )
            SmartisanDialogButton(
                text = stringResource(R.string.proxy_error_dialog_close),
                onClick = onDismiss,
                modifier = Modifier.weight(1f).fillMaxHeight(),
            )
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 380.dp)
                .verticalScroll(rememberScrollState()),
        ) {
                SmartisanText(
                    text = modeLine,
                    style = LocalSmartisanTypography.current.listRowPrimary,
                )
                Spacer(modifier = Modifier.height(8.dp))
                SmartisanText(
                    text = timeLine,
                    style = LocalSmartisanTypography.current.listItemSecondary,
                )

                SectionLabel(detailsLabel)
                MonospaceBlock(explanation.rawMessage)

                if (explanation.hasDeviceInfo) {
                    SectionLabel(deviceLabel)
                    MonospaceBlock(explanation.deviceInfo)
                }

                if (explanation.hasServiceLog) {
                    SectionLabel(serviceLogLabel)
                    MonospaceBlock(explanation.serviceLog)
                }

                if (suggestions.isNotEmpty()) {
                    SectionLabel(diagnosticsLabel)
                    suggestions.forEach { suggestion ->
                        SmartisanText(
                            text = "• $suggestion",
                            style = LocalSmartisanTypography.current.body,
                        )
                    }
                }
            }
        }
}

@Composable
private fun SectionLabel(text: String) {
    Spacer(modifier = Modifier.height(16.dp))
    SmartisanText(
        text = text,
        style = LocalSmartisanTypography.current.sectionTitle,
    )
    Spacer(modifier = Modifier.height(4.dp))
}

/** Selectable monospace text — log and system output are far easier to read aligned. */
@Composable
private fun MonospaceBlock(text: String) {
    SelectionContainer {
        SmartisanText(
            text = text,
            style = LocalSmartisanTypography.current.listItemSecondary.copy(fontFamily = FontFamily.Monospace),
        )
    }
}

private fun copyToClipboard(context: Context, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
    clipboard?.setPrimaryClip(ClipData.newPlainText("AsteriskMETA proxy error", text))
}
