// Copyright 2026, AsteriskMETA contributors
// SPDX-License-Identifier: GPL-3.0

package features.mihomo

import ui.components.AsteriskIcon
import cc.wuersan008.smartisanx.core.theme.LocalSmartisanColors
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import cc.wuersan008.smartisanx.core.interaction.smartisanClickable
import ui.components.AsteriskSpinner
import cc.wuersan008.smartisanx.ui.basic.SmartisanIcon
import cc.wuersan008.smartisanx.ui.basic.SmartisanText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import app.R
import ui.icons.AsteriskIcons as Icons

@Composable
internal fun MihomoDelayToolbar(
    enabled: Boolean,
    testing: Boolean,
    onDelayTest: () -> Unit,
    bottomPadding: Dp,
    modifier: Modifier = Modifier,
    canLocate: Boolean = false,
    onLocate: () -> Unit = {},
) {
    Row(
        modifier = modifier.padding(
            end = 20.dp,
            bottom = bottomPadding + MihomoFloatingToolbarBottomSpacing,
        ),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (canLocate) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(LocalSmartisanColors.current.surfaceRaised)
                    .smartisanClickable(onClick = onLocate),
                contentAlignment = Alignment.Center,
            ) {
                AsteriskIcon(
                    res = Icons.Rounded.MyLocation,
                    contentDescription = stringResource(R.string.mihomo_proxies_locate_selected),
                    tint = LocalSmartisanColors.current.textPrimary,
                    modifier = Modifier.size(24.dp),
                )
            }
        }
        Row(
            modifier = Modifier
                .clip(CircleShape)
                .background(LocalSmartisanColors.current.selectionBackground)
                .smartisanClickable(enabled = enabled, onClick = onDelayTest)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            if (testing) {
                AsteriskSpinner(
                    size = 20.dp,
                    color = LocalSmartisanColors.current.textPrimary,
                )
            } else {
                AsteriskIcon(
                    res = Icons.Rounded.Speed,
                    contentDescription = stringResource(R.string.mihomo_proxies_group_test),
                    tint = LocalSmartisanColors.current.textPrimary,
                )
            }
            SmartisanText(
                text = stringResource(R.string.mihomo_proxies_group_test),
                color = LocalSmartisanColors.current.textPrimary.copy(
                    alpha = if (enabled && !testing) 1f else 0.45f,
                ),
            )
        }
    }
}

private val MihomoFloatingToolbarBottomSpacing = 16.dp
