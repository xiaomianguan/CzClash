// Copyright 2026, AsteriskMETA contributors
// SPDX-License-Identifier: GPL-3.0

package features.logs

import ui.components.AsteriskIcon
import cc.wuersan008.smartisanx.core.interaction.smartisanClickable
import cc.wuersan008.smartisanx.ui.basic.SmartisanSurface
import cc.wuersan008.smartisanx.ui.layout.SmartisanCard
import cc.wuersan008.smartisanx.core.theme.LocalSmartisanShapes
import cc.wuersan008.smartisanx.core.theme.LocalSmartisanTypography
import cc.wuersan008.smartisanx.core.theme.LocalSmartisanColors
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import cc.wuersan008.smartisanx.ui.basic.SmartisanIcon
import cc.wuersan008.smartisanx.ui.basic.SmartisanText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import app.R
import ui.components.AsteriskChipTone
import ui.components.AsteriskInfoChip
import ui.icons.AsteriskIcons as Icons

@Composable
internal fun LogEmptyCard(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        SmartisanSurface(
            shape = LocalSmartisanShapes.current.medium,
            color = LocalSmartisanColors.current.surfaceRaised,
        ) {
            AsteriskIcon(
                res = Icons.AutoMirrored.Rounded.Article,
                contentDescription = null,
                modifier = Modifier.padding(18.dp),
                tint = LocalSmartisanColors.current.textPrimary,
            )
        }
        SmartisanText(
            text = stringResource(R.string.common_empty),
            style = LocalSmartisanTypography.current.listItemPrimary,
            color = LocalSmartisanColors.current.textSecondary,
        )
    }
}

@Composable
internal fun LogEntryCard(
    entry: CoreLogEntry,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    SmartisanCard(
        modifier = modifier
            .fillMaxWidth()
            .smartisanClickable(onClick = onClick),
        color = LocalSmartisanColors.current.surface,
    ) {
        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                SmartisanText(
                    text = formatCoreLogTime(entry.timestampMillis),
                    style = LocalSmartisanTypography.current.listItemSecondary,
                    color = LocalSmartisanColors.current.textSecondary,
                )
                LogLevelChip(entry.level)
            }
            SmartisanText(
                text = entry.message,
                style = LocalSmartisanTypography.current.body.copy(fontFamily = FontFamily.Monospace),
                color = LocalSmartisanColors.current.textPrimary,
                modifier = Modifier.padding(top = 8.dp),
            )
        }
    }
}

@Composable
private fun LogLevelChip(level: String) {
    val normalized = level.trim().lowercase()
    val isError = normalized == "error"
    val isWarning = normalized == "warning" || normalized == "warn"
    AsteriskInfoChip(
        text = level.uppercase(),
        tone = when {
            isError -> AsteriskChipTone.Error
            isWarning -> AsteriskChipTone.Tertiary
            else -> AsteriskChipTone.Neutral
        },
    )
}
