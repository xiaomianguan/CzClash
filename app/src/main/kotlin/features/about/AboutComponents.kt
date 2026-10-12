// Copyright 2026, AsteriskMETA contributors
// SPDX-License-Identifier: GPL-3.0

package features.about

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.ProjectInfo
import app.R
import cc.wuersan008.smartisanx.core.theme.LocalSmartisanColors
import cc.wuersan008.smartisanx.core.theme.LocalSmartisanTypography
import cc.wuersan008.smartisanx.core.theme.SmartisanDimens
import cc.wuersan008.smartisanx.core.utils.smartisanShadowBackground
import cc.wuersan008.smartisanx.ui.asset.SmartisanDrawables
import cc.wuersan008.smartisanx.ui.basic.SmartisanSurface
import cc.wuersan008.smartisanx.ui.basic.SmartisanText

private const val AboutIconForegroundScale = 1.25f

/** About logo card: original group-card texture with the app mark, name and version. */
@Composable
internal fun AboutLogoCard(
    modifier: Modifier = Modifier,
) {
    val colors = LocalSmartisanColors.current
    val typography = LocalSmartisanTypography.current
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = SmartisanDimens.ListItemHorizontalMargin)
            .smartisanShadowBackground(
                backgroundRes = SmartisanDrawables.GroupRowSingle,
                shadowRes = SmartisanDrawables.GroupRowSingleShadow,
            )
            .height(180.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            AboutAppIcon(Modifier.size(88.dp))
            SmartisanText(
                text = ProjectInfo.PROJECT_NAME,
                style = typography.title.copy(
                    fontSize = 20.sp,
                    letterSpacing = 1.sp,
                    fontWeight = FontWeight.Medium,
                ),
                color = colors.accent,
            )
            SmartisanText(
                text = "v${ProjectInfo.VERSION_NAME} (${ProjectInfo.VERSION_CODE})",
                style = typography.caption,
                color = colors.textTertiary,
            )
        }
    }
}

@Composable
private fun AboutAppIcon(
    modifier: Modifier = Modifier,
) {
    val iconStyle = aboutIconStyle()
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        SmartisanSurface(
            modifier = Modifier.fillMaxSize(),
            color = iconStyle.containerColor,
        ) {
            Image(
                painter = painterResource(iconStyle.foregroundResId),
                contentDescription = ProjectInfo.PROJECT_NAME,
                contentScale = ContentScale.Fit,
                colorFilter = iconStyle.foregroundTint?.let(ColorFilter::tint),
                modifier = Modifier.fillMaxSize().scale(AboutIconForegroundScale),
            )
        }
    }
}

@Composable
private fun aboutIconStyle(): AboutIconStyle {
    return AboutIconStyle(
        foregroundResId = R.drawable.ic_launcher_monochrome,
        foregroundTint = LocalSmartisanColors.current.accent,
        containerColor = LocalSmartisanColors.current.surface,
    )
}

private data class AboutIconStyle(
    val foregroundResId: Int,
    val foregroundTint: Color?,
    val containerColor: Color,
)
