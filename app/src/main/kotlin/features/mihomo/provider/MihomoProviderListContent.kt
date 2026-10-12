// Copyright 2026, AsteriskMETA contributors
// SPDX-License-Identifier: GPL-3.0

package features.mihomo.provider

import ui.components.AsteriskIcon
import cc.wuersan008.smartisanx.ui.basic.SmartisanSurface
import cc.wuersan008.smartisanx.core.theme.LocalSmartisanShapes
import cc.wuersan008.smartisanx.core.theme.LocalSmartisanTypography
import cc.wuersan008.smartisanx.core.theme.LocalSmartisanColors
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import ui.components.AsteriskSpinner
import ui.components.AsteriskPopupMenu
import ui.components.AsteriskPopupMenuItem
import cc.wuersan008.smartisanx.ui.basic.SmartisanIcon
import cc.wuersan008.smartisanx.ui.basic.SmartisanIconButton
import ui.components.AsteriskProgressBar
import cc.wuersan008.smartisanx.ui.basic.SmartisanText
import ui.components.AsteriskLinkButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import app.R
import engine.mihomo.MihomoProviderDeclaration
import engine.mihomo.runtime.MihomoProviderSubscriptionInfo
import engine.mihomo.runtime.MihomoProxyProviderRuntimeDetail
import engine.mihomo.runtime.MihomoRuleProviderRuntimeSummary
import ui.components.AsteriskExpressiveCard
import ui.components.AsteriskInfoChip
import ui.components.AsteriskStatusCard
import ui.layout.pageListPadding
import ui.text.formatTemplate
import ui.theme.AsteriskMotion
import ui.theme.ExpressiveShapeRole
import utils.ReadableByteUnit
import utils.toReadableBytes
import utils.toReadableDateOrDash
import utils.toReadableDateTimeOrDash
import ui.icons.AsteriskIcons as Icons

@Composable
internal fun MihomoProviderManagementList(
    tab: MihomoProviderManagementTab,
    state: ProviderDeclarationsState,
    hasUsableProfile: Boolean,
    proxyRuntimeDetails: Map<String, MihomoProxyProviderRuntimeDetail>,
    ruleRuntimeSummaries: Map<String, MihomoRuleProviderRuntimeSummary>,
    ruleRuntimeLoading: Boolean,
    ruleRuntimeError: String,
    refreshingNames: Set<String>,
    refreshEnabled: Boolean,
    contentPadding: PaddingValues,
    listState: androidx.compose.foundation.lazy.LazyListState,
    onRetryDeclarations: () -> Unit,
    onRetryRuleRuntime: () -> Unit,
    onOpenProxy: (MihomoProviderDeclaration) -> Unit,
    onPreview: (MihomoProviderDeclaration) -> Unit,
    onRefresh: (MihomoProviderDeclaration) -> Unit,
) {
    val currentRuleSummaries = currentRuleProviderSummaries(
        declaredNames = state.providers.mapTo(mutableSetOf(), MihomoProviderDeclaration::name),
        summaries = ruleRuntimeSummaries,
    )
    val listPadding = pageListPadding(contentPadding)
    LazyColumn(
        state = listState,
        contentPadding = listPadding,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item(key = "${tab.name}_status") {
            when (tab) {
                MihomoProviderManagementTab.Proxy -> ProxyProviderStatusCard(
                    state = reduceProxyProviderFocusState(
                        providerCount = state.providers.size,
                        runtimeDetails = proxyRuntimeDetails.values,
                        loading = state.loading,
                        error = state.error,
                    ),
                )
                MihomoProviderManagementTab.Rule -> RuleProviderStatusCard(
                    providerCount = state.providers.size,
                    summaries = currentRuleSummaries.values,
                    loading = state.loading || ruleRuntimeLoading,
                    error = state.error.ifBlank { ruleRuntimeError },
                    onRetry = if (state.error.isNotBlank()) onRetryDeclarations else onRetryRuleRuntime,
                )
            }
        }
        when {
            state.loading -> item(key = "${tab.name}_loading") {
                ProviderMessageCard(text = stringResource(R.string.mihomo_dashboard_network_detection_checking))
            }

            state.error.isNotBlank() -> item(key = "${tab.name}_error") {
                ProviderMessageCard(
                    text = state.error,
                    actionText = stringResource(R.string.common_retry),
                    onAction = onRetryDeclarations,
                )
            }

            state.providers.isEmpty() -> item(key = "${tab.name}_empty") {
                ProviderMessageCard(
                    text = if (!hasUsableProfile) {
                        stringResource(R.string.mihomo_proxies_no_profile_summary)
                    } else if (tab == MihomoProviderManagementTab.Proxy) {
                        stringResource(R.string.mihomo_proxy_providers_empty)
                    } else {
                        stringResource(R.string.mihomo_rule_providers_empty)
                    },
                )
            }

            else -> items(
                items = state.providers,
                key = { provider -> provider.name },
            ) { provider ->
                val modifier = Modifier.animateItem(
                    fadeInSpec = AsteriskMotion.effects(),
                    placementSpec = AsteriskMotion.spatial(),
                    fadeOutSpec = AsteriskMotion.effects(),
                )
                when (tab) {
                    MihomoProviderManagementTab.Proxy -> MihomoProxyProviderCard(
                        modifier = modifier,
                        provider = provider,
                        runtimeDetail = proxyRuntimeDetails[provider.name],
                        refreshing = provider.name in refreshingNames,
                        refreshEnabled = refreshEnabled,
                        onClick = { onOpenProxy(provider) },
                        onAction = { action ->
                            when (action) {
                                MihomoProviderAction.Preview -> onPreview(provider)
                                MihomoProviderAction.Sync -> onRefresh(provider)
                            }
                        },
                    )
                    MihomoProviderManagementTab.Rule -> MihomoRuleProviderCard(
                        modifier = modifier,
                        provider = provider,
                        runtimeSummary = currentRuleSummaries[provider.name],
                        refreshing = provider.name in refreshingNames,
                        refreshEnabled = refreshEnabled,
                        onClick = { onPreview(provider) },
                        onAction = { action ->
                            when (action) {
                                MihomoProviderAction.Preview -> onPreview(provider)
                                MihomoProviderAction.Sync -> onRefresh(provider)
                            }
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun ProxyProviderStatusCard(
    state: ProxyProviderFocusState,
    modifier: Modifier = Modifier,
) {
    val title = stringResource(
        when (state.readiness) {
            ProviderReadiness.Loading -> R.string.mihomo_provider_focus_loading
            ProviderReadiness.Ready -> R.string.mihomo_provider_focus_ready
            ProviderReadiness.Empty -> R.string.mihomo_provider_focus_empty
            ProviderReadiness.Error -> R.string.mihomo_provider_focus_error
        },
    )
    val updated = if (state.updatedAtMillis > 0L) {
        stringResource(
            R.string.mihomo_provider_focus_updated,
            state.updatedAtMillis.toReadableDateTimeOrDash(),
        )
    } else {
        null
    }
    AsteriskStatusCard(
        modifier = modifier,
        status = updated,
        controls = {
            SmartisanText(
                text = stringResource(R.string.mihomo_provider_proxy_servers_count)
                    .formatTemplate("count" to state.nodeCount),
                style = LocalSmartisanTypography.current.listRowPrimary,
                fontWeight = FontWeight.SemiBold,
            )
        },
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AsteriskIcon(
                res = Icons.Rounded.CloudSync,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = LocalSmartisanColors.current.accent,
            )
            SmartisanText(
                text = title,
                style = LocalSmartisanTypography.current.listItemPrimary,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f),
            )
            AsteriskInfoChip(
                text = pluralStringResource(
                    R.plurals.mihomo_provider_focus_progress,
                    state.readyCount,
                    state.readyCount,
                    state.providerCount,
                ),
            )
        }
    }
}

@Composable
private fun RuleProviderStatusCard(
    providerCount: Int,
    summaries: Collection<MihomoRuleProviderRuntimeSummary>,
    loading: Boolean,
    error: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val runtimeIncomplete = !loading && providerCount > 0 && summaries.size < providerCount
    val readiness = when {
        error.isNotBlank() || runtimeIncomplete -> ProviderReadiness.Error
        loading -> ProviderReadiness.Loading
        providerCount == 0 -> ProviderReadiness.Empty
        else -> ProviderReadiness.Ready
    }
    val title = stringResource(
        when (readiness) {
            ProviderReadiness.Loading -> R.string.mihomo_provider_focus_loading
            ProviderReadiness.Ready -> R.string.mihomo_provider_focus_ready
            ProviderReadiness.Empty -> R.string.mihomo_provider_focus_empty
            ProviderReadiness.Error -> R.string.mihomo_provider_focus_error
        },
    )
    val updatedAtMillis = summaries.maxOfOrNull(MihomoRuleProviderRuntimeSummary::updatedAtMillis) ?: 0L
    val status = when {
        error.isNotBlank() || runtimeIncomplete -> stringResource(R.string.mihomo_rule_provider_runtime_unavailable)
        updatedAtMillis > 0L -> stringResource(
            R.string.mihomo_provider_focus_updated,
            updatedAtMillis.toReadableDateTimeOrDash(),
        )
        else -> null
    }
    val readyCount = summaries.size.coerceAtMost(providerCount)
    val ruleCount = summaries.sumOf(MihomoRuleProviderRuntimeSummary::ruleCount)
    AsteriskStatusCard(
        modifier = modifier,
        status = status,
        controls = {
            SmartisanText(
                text = pluralStringResource(
                    R.plurals.mihomo_rule_provider_rules_count,
                    ruleCount,
                    ruleCount,
                ),
                style = LocalSmartisanTypography.current.listRowPrimary,
                fontWeight = FontWeight.SemiBold,
            )
            if (error.isNotBlank() || runtimeIncomplete) {
                AsteriskLinkButton(onClick = onRetry) {
                    SmartisanText(stringResource(R.string.common_retry))
                }
            }
        },
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AsteriskIcon(
                res = Icons.Rounded.Description,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = LocalSmartisanColors.current.accent,
            )
            SmartisanText(
                text = title,
                style = LocalSmartisanTypography.current.listItemPrimary,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f),
            )
            AsteriskInfoChip(
                text = pluralStringResource(
                    R.plurals.mihomo_provider_focus_progress,
                    readyCount,
                    readyCount,
                    providerCount,
                ),
            )
        }
    }
}

@Composable
private fun MihomoProxyProviderCard(
    modifier: Modifier = Modifier,
    provider: MihomoProviderDeclaration,
    runtimeDetail: MihomoProxyProviderRuntimeDetail?,
    refreshing: Boolean,
    refreshEnabled: Boolean,
    onClick: () -> Unit,
    onAction: (MihomoProviderAction) -> Unit,
) {
    val vehicleText = runtimeDetail?.vehicleType?.ifBlank { null } ?: provider.vehicleType
    var menuExpanded by remember { mutableStateOf(false) }
    AsteriskExpressiveCard(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        role = ExpressiveShapeRole.ContentCard,
        containerColor = LocalSmartisanColors.current.surface,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 14.dp, end = 4.dp, top = 14.dp, bottom = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SmartisanSurface(
                shape = LocalSmartisanShapes.current.large,
                color = LocalSmartisanColors.current.surfaceRaised,
            ) {
                AsteriskIcon(
                    res = Icons.Rounded.CloudSync,
                    contentDescription = null,
                    tint = LocalSmartisanColors.current.textPrimary,
                    modifier = Modifier.padding(12.dp).size(24.dp),
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(
                modifier = Modifier.weight(1f),
            ) {
                SmartisanText(
                    text = provider.name,
                    style = LocalSmartisanTypography.current.listItemPrimary,
                    fontWeight = FontWeight.SemiBold,
                    color = LocalSmartisanColors.current.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                SmartisanText(
                    text = provider.sourceSummary.ifBlank { vehicleText },
                    style = LocalSmartisanTypography.current.body,
                    color = LocalSmartisanColors.current.textSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                runtimeDetail?.subscriptionInfo
                    ?.takeIf { info -> info.hasProviderTrafficInfo() }
                    ?.let { info -> MihomoProxyProviderTrafficInfo(info = info) }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    SmartisanText(
                        text = provider.proxyRuntimeSummaryText(runtimeDetail),
                        style = LocalSmartisanTypography.current.listItemSecondary,
                        color = LocalSmartisanColors.current.textSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f),
                    )
                    AsteriskInfoChip(text = vehicleText)
                }
            }
            Spacer(modifier = Modifier.width(4.dp))
            if (refreshing) {
                AsteriskSpinner(
                    size = 20.dp,
                    )
            }
            Box {
                SmartisanIconButton(onClick = { menuExpanded = true }) {
                    AsteriskIcon(
                        res = Icons.Rounded.MoreVert,
                        contentDescription = stringResource(R.string.mihomo_profile_actions),
                    )
                }
                AsteriskPopupMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false },
                ) {
                    AsteriskPopupMenuItem(
                        text = { SmartisanText(stringResource(R.string.mihomo_profile_preview)) },
                        onClick = {
                            menuExpanded = false
                            onAction(MihomoProviderAction.Preview)
                        },
                        leadingIcon = { AsteriskIcon(Icons.Rounded.Visibility, contentDescription = null) },
                    )
                    if (refreshEnabled && !refreshing) {
                        AsteriskPopupMenuItem(
                            text = { SmartisanText(stringResource(R.string.mihomo_profile_sync)) },
                            onClick = {
                                menuExpanded = false
                                onAction(MihomoProviderAction.Sync)
                            },
                            leadingIcon = { AsteriskIcon(Icons.Rounded.Sync, contentDescription = null) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MihomoRuleProviderCard(
    modifier: Modifier = Modifier,
    provider: MihomoProviderDeclaration,
    runtimeSummary: MihomoRuleProviderRuntimeSummary?,
    refreshing: Boolean,
    refreshEnabled: Boolean,
    onClick: () -> Unit,
    onAction: (MihomoProviderAction) -> Unit,
) {
    var menuExpanded by remember { mutableStateOf(false) }
    val metadata = provider.ruleMetadata
    val vehicleText = runtimeSummary?.vehicleType?.ifBlank { null } ?: provider.vehicleType
    val behaviorText = runtimeSummary?.behavior?.ifBlank { null } ?: metadata?.behavior.orEmpty()
    val formatText = runtimeSummary?.format?.ifBlank { null } ?: metadata?.format.orEmpty()
    val ruleCountText = runtimeSummary?.ruleCount?.let { count ->
        pluralStringResource(R.plurals.mihomo_rule_provider_rules_count, count, count)
    }
    val updatedText = runtimeSummary?.updatedAtMillis
        ?.takeIf { timestamp -> timestamp > 0L }
        ?.let { timestamp ->
            stringResource(
                R.string.mihomo_rule_provider_updated,
                timestamp.toReadableDateTimeOrDash(),
            )
        }
    val runtimeText = when {
        ruleCountText != null && updatedText != null -> stringResource(
            R.string.mihomo_provider_raw_metadata,
            ruleCountText,
            updatedText,
        )
        ruleCountText != null -> ruleCountText
        updatedText != null -> updatedText
        else -> stringResource(R.string.mihomo_rule_provider_runtime_unavailable)
    }

    AsteriskExpressiveCard(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        role = ExpressiveShapeRole.ContentCard,
        containerColor = LocalSmartisanColors.current.surface,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 14.dp, end = 4.dp, top = 14.dp, bottom = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SmartisanSurface(
                shape = LocalSmartisanShapes.current.large,
                color = LocalSmartisanColors.current.surfaceRaised,
            ) {
                AsteriskIcon(
                    res = Icons.Rounded.Description,
                    contentDescription = null,
                    tint = LocalSmartisanColors.current.textPrimary,
                    modifier = Modifier.padding(12.dp).size(24.dp),
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                SmartisanText(
                    text = provider.name,
                    style = LocalSmartisanTypography.current.listItemPrimary,
                    fontWeight = FontWeight.SemiBold,
                    color = LocalSmartisanColors.current.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                SmartisanText(
                    text = provider.sourceSummary.ifBlank { vehicleText },
                    style = LocalSmartisanTypography.current.body,
                    color = LocalSmartisanColors.current.textSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                SmartisanText(
                    text = runtimeText,
                    style = LocalSmartisanTypography.current.listItemSecondary,
                    color = LocalSmartisanColors.current.textSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 10.dp),
                )
                Row(
                    modifier = Modifier.padding(top = 10.dp).horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    ruleProviderChipLabels(
                        vehicle = vehicleText,
                        behavior = behaviorText,
                        format = formatText,
                    ).forEach { label ->
                        AsteriskInfoChip(text = label)
                    }
                }
            }
            Spacer(modifier = Modifier.width(4.dp))
            if (refreshing) {
                AsteriskSpinner(
                    size = 20.dp,
                    )
            }
            Box {
                SmartisanIconButton(onClick = { menuExpanded = true }) {
                    AsteriskIcon(
                        res = Icons.Rounded.MoreVert,
                        contentDescription = stringResource(R.string.mihomo_profile_actions),
                    )
                }
                AsteriskPopupMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false },
                ) {
                    AsteriskPopupMenuItem(
                        text = { SmartisanText(stringResource(R.string.mihomo_profile_preview)) },
                        onClick = {
                            menuExpanded = false
                            onAction(MihomoProviderAction.Preview)
                        },
                        leadingIcon = { AsteriskIcon(Icons.Rounded.Visibility, contentDescription = null) },
                    )
                    if (refreshEnabled && !refreshing) {
                        AsteriskPopupMenuItem(
                            text = { SmartisanText(stringResource(R.string.mihomo_profile_sync)) },
                            onClick = {
                                menuExpanded = false
                                onAction(MihomoProviderAction.Sync)
                            },
                            leadingIcon = { AsteriskIcon(Icons.Rounded.Sync, contentDescription = null) },
                        )
                    }
                }
            }
        }
    }
}

private enum class MihomoProviderAction {
    Preview,
    Sync,
}

@Composable
internal fun ProviderEmptyState(
    modifier: Modifier = Modifier,
    text: String = stringResource(R.string.common_empty),
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 40.dp),
        contentAlignment = Alignment.Center,
    ) {
        SmartisanText(
            text = text,
            style = LocalSmartisanTypography.current.body,
            color = LocalSmartisanColors.current.textSecondary,
        )
    }
}

@Composable
internal fun ProviderMessageCard(
    text: String,
    actionText: String? = null,
    onAction: (() -> Unit)? = null,
) {
    AsteriskExpressiveCard(
        modifier = Modifier.fillMaxWidth(),
        role = ExpressiveShapeRole.ContentCard,
        containerColor = LocalSmartisanColors.current.surface,
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            SmartisanText(
                text = text.ifBlank { stringResource(R.string.common_empty) },
                style = LocalSmartisanTypography.current.body,
                color = LocalSmartisanColors.current.textSecondary,
            )
            if (actionText != null && onAction != null) {
                AsteriskLinkButton(
                    onClick = onAction,
                    modifier = Modifier.align(Alignment.End).padding(top = 4.dp),
                ) {
                    SmartisanText(actionText)
                }
            }
        }
    }
}

private fun MihomoProviderSubscriptionInfo.hasProviderTrafficInfo(): Boolean {
    return upload > 0L || download > 0L || total > 0L || expire > 0L
}

@Composable
private fun MihomoProxyProviderTrafficInfo(
    info: MihomoProviderSubscriptionInfo,
) {
    val usedBytes = info.upload + info.download
    val progress = if (info.total > 0L) {
        (usedBytes.toDouble() / info.total.toDouble()).coerceIn(0.0, 1.0).toFloat()
    } else {
        0f
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp),
    ) {
        AsteriskProgressBar(
            progress =  progress ,
            modifier = Modifier.fillMaxWidth().height(6.dp),
        )
        SmartisanText(
            text = stringResource(R.string.mihomo_profile_traffic_summary)
                .formatTemplate(
                    "used" to usedBytes.toReadableBytes(maxUnit = ReadableByteUnit.GiB),
                    "total" to info.total.toProviderTrafficTotalText(),
                    "expire" to info.expire.toProviderExpireText(),
                ),
            style = LocalSmartisanTypography.current.listItemSecondary,
            color = LocalSmartisanColors.current.textSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 6.dp),
        )
    }
}

@Composable
private fun MihomoProviderDeclaration.proxyRuntimeSummaryText(
    runtimeDetail: MihomoProxyProviderRuntimeDetail?,
): String {
    val nodeText = runtimeDetail?.nodes?.size?.let { count ->
        stringResource(R.string.mihomo_provider_proxy_servers_count).formatTemplate("count" to count)
    }
    val updatedText = runtimeDetail?.updatedAtMillis
        ?.takeIf { timestamp -> timestamp > 0L }
        ?.toReadableDateTimeOrDash()
    return when {
        nodeText != null && updatedText != null -> stringResource(
            R.string.mihomo_provider_raw_metadata,
            nodeText,
            updatedText,
        )
        nodeText != null -> nodeText
        updatedText != null -> updatedText
        else -> providerType.name
    }
}

@Composable
private fun Long.toProviderTrafficTotalText(): String {
    return if (this > 0L) {
        toReadableBytes(maxUnit = ReadableByteUnit.GiB)
    } else {
        stringResource(R.string.mihomo_provider_traffic_unlimited)
    }
}

@Composable
private fun Long.toProviderExpireText(): String {
    return if (this > 0L) {
        (this * 1_000L).toReadableDateOrDash()
    } else {
        stringResource(R.string.mihomo_profile_expire_unlimited)
    }
}
