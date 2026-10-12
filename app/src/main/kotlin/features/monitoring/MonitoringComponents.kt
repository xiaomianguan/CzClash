// Copyright 2026, AsteriskMETA contributors
// SPDX-License-Identifier: GPL-3.0

package features.monitoring

import ui.components.AsteriskIcon
import cc.wuersan008.smartisanx.ui.layout.SmartisanCard
import cc.wuersan008.smartisanx.core.theme.LocalSmartisanShapes
import cc.wuersan008.smartisanx.core.theme.LocalSmartisanTypography
import cc.wuersan008.smartisanx.core.theme.LocalSmartisanColors
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import cc.wuersan008.smartisanx.ui.basic.SmartisanText
import cc.wuersan008.smartisanx.ui.basic.SmartisanIcon
import cc.wuersan008.smartisanx.ui.basic.SmartisanIconButton
import ui.components.AsteriskSearchTopAppBar
import ui.components.AsteriskScaffold
import ui.components.AsteriskTopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import app.LocalNavigator
import app.R
import ui.components.AsteriskStatusCard
import ui.components.AsteriskValueRow
import ui.icons.AsteriskIcons as Icons

@Composable
internal fun MonitoringScaffold(
    title: String,
    outerPadding: PaddingValues,
    actions: @Composable () -> Unit = {},
    toolbar: @Composable () -> Unit = {},
    searchQuery: String? = null,
    onSearchQueryChange: (String) -> Unit = {},
    searchPlaceholder: String = "",
    content: @Composable (PaddingValues) -> Unit,
) {
    val navigator = LocalNavigator.current
    AsteriskScaffold(
        topBar = {
            Column {
                if (searchQuery != null) {
                    AsteriskSearchTopAppBar(
                        query = searchQuery,
                        onQueryChange = onSearchQueryChange,
                        placeholder = searchPlaceholder,
                        title = { SmartisanText(title) },
                        navigationIcon = {
                            SmartisanIconButton(onClick = navigator::pop) {
                                AsteriskIcon(
                                    res = Icons.AutoMirrored.Rounded.ArrowBack,
                                    contentDescription = stringResource(R.string.common_back),
                                )
                            }
                        },
                        actions = { actions() },
                    )
                } else {
                    AsteriskTopAppBar(
                        title = { SmartisanText(title) },
                        navigationIcon = {
                            SmartisanIconButton(onClick = navigator::pop) {
                                AsteriskIcon(
                                    res = Icons.AutoMirrored.Rounded.ArrowBack,
                                    contentDescription = stringResource(R.string.common_back),
                                )
                            }
                        },
                        actions = { actions() },
                    )
                }
                toolbar()
            }
        },
    ) { innerPadding ->
        content(
            PaddingValues(
                top = innerPadding.calculateTopPadding(),
                bottom = innerPadding.calculateBottomPadding() + outerPadding.calculateBottomPadding(),
            ),
        )
    }
}

@Composable
internal fun MonitoringSectionCard(
    title: String,
    modifier: Modifier = Modifier,
    headerContent: @Composable RowScope.() -> Unit = {},
    content: @Composable () -> Unit,
) {
    SmartisanCard(
        modifier = modifier.fillMaxWidth(),
        color = LocalSmartisanColors.current.surface,
        shape = LocalSmartisanShapes.current.medium,
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                SmartisanText(
                    text = title,
                    modifier = Modifier.weight(1f),
                    style = LocalSmartisanTypography.current.listItemPrimary,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                headerContent()
            }
            Box(modifier = Modifier.padding(top = 12.dp)) { content() }
        }
    }
}

@Composable
internal fun MonitoringStatusHeader(
    title: String,
    value: String,
    summary: String,
    modifier: Modifier = Modifier,
    compactStatus: Boolean = false,
    controls: @Composable RowScope.() -> Unit = {},
    metrics: @Composable RowScope.() -> Unit = {},
) {
    AsteriskStatusCard(
        modifier = modifier,
        status = summary,
        compactStatus = compactStatus,
        controls = controls,
    ) {
        SmartisanText(
            text = title,
            style = LocalSmartisanTypography.current.sectionTitle,
            color = LocalSmartisanColors.current.textSecondary,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SmartisanText(
                text = value,
                modifier = Modifier.weight(1f),
                style = LocalSmartisanTypography.current.title.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontFeatureSettings = "tnum",
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            metrics()
        }
    }
}

@Composable
internal fun MonitoringValueRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    verticalAlignment: Alignment.Vertical = Alignment.Top,
    trailing: @Composable (() -> Unit)? = null,
) {
    AsteriskValueRow(
        title = label,
        value = value,
        modifier = modifier,
        titleMinWidth = 112.dp,
        valueMaxLines = 3,
        verticalAlignment = verticalAlignment,
        trailing = trailing,
    )
}
