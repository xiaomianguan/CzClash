// Copyright 2026, AsteriskMETA contributors
// SPDX-License-Identifier: GPL-3.0

package ui.components

import cc.wuersan008.smartisanx.core.theme.LocalSmartisanTypography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import cc.wuersan008.smartisanx.ui.control.SmartisanChip

@Composable
internal fun AsteriskFilterChip(
    selected: Boolean,
    onClick: () -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
) {
    SmartisanChip(
        text = label,
        modifier = modifier,
        selected = selected,
        enabled = enabled,
        onClick = onClick,
    )
}

@Composable
internal fun AsteriskInfoChip(
    text: String,
    modifier: Modifier = Modifier,
    emphasized: Boolean = false,
    tone: AsteriskChipTone = if (emphasized) AsteriskChipTone.Primary else AsteriskChipTone.Neutral,
    textStyle: TextStyle = LocalSmartisanTypography.current.listItemCaptionSmall,
) {
    SmartisanChip(
        text = text,
        modifier = modifier,
        selected = emphasized || tone == AsteriskChipTone.Primary,
    )
}

internal enum class AsteriskChipTone {
    Neutral,
    Primary,
    Secondary,
    Tertiary,
    Error,
}
