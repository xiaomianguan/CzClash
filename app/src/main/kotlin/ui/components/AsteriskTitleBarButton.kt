// Copyright 2026, AsteriskMETA contributors
// SPDX-License-Identifier: GPL-3.0

package ui.components

import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cc.wuersan008.smartisanx.core.interaction.smartisanClickable
import cc.wuersan008.smartisanx.core.theme.LocalSmartisanColors
import cc.wuersan008.smartisanx.core.theme.LocalSmartisanTypography
import cc.wuersan008.smartisanx.ui.basic.SmartisanText

/**
 * Compact title-bar action button: the standard Smartisan blue vertical gradient
 * (`btn_text_color_blue` → `btn_text_color_blue_pressed`) with a white label,
 * matching the system title-bar `Add` action.
 */
@Composable
internal fun AsteriskTitleBarButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val colors = LocalSmartisanColors.current
    val typography = LocalSmartisanTypography.current
    val shape = RoundedCornerShape(4.dp)
    val brush = if (enabled) {
        Brush.verticalGradient(listOf(Color(0xFF5C89F2), Color(0xFF3D70EF)))
    } else {
        Brush.verticalGradient(listOf(colors.accentDisabled, colors.accentDisabled))
    }
    Box(
        modifier = modifier
            .height(32.dp)
            .clip(shape)
            .background(brush, shape)
            .smartisanClickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.Center,
    ) {
        SmartisanText(
            text = text,
            style = typography.button.copy(fontWeight = FontWeight.Medium),
            color = Color.White,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
