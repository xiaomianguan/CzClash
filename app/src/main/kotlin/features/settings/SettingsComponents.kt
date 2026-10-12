// Copyright 2026, AsteriskMETA contributors
// SPDX-License-Identifier: GPL-3.0

package features.settings

import cc.wuersan008.smartisanx.ui.layout.SmartisanListItem
import cc.wuersan008.smartisanx.ui.layout.SmartisanSectionTitle
import ui.components.AsteriskIcon
import cc.wuersan008.smartisanx.ui.layout.SmartisanGroup
import cc.wuersan008.smartisanx.ui.layout.SmartisanGroupRowPosition
import cc.wuersan008.smartisanx.core.theme.LocalSmartisanShapes
import cc.wuersan008.smartisanx.core.theme.LocalSmartisanTypography
import cc.wuersan008.smartisanx.core.theme.LocalSmartisanColors
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import cc.wuersan008.smartisanx.ui.basic.SmartisanIcon
import cc.wuersan008.smartisanx.ui.control.SmartisanSwitch
import cc.wuersan008.smartisanx.ui.basic.SmartisanText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import app.R
import app.modes.RunModeBpf2Socks
import app.modes.RunModeTproxy
import app.modes.RunModeTun2Socks
import ui.components.IconAccent
import ui.components.MaskedPreferenceIcon
import ui.components.AsteriskDropdownAnchor
import ui.components.AsteriskDropdownMenuItem
import ui.text.formatTemplate
import ui.icons.AsteriskIcons as Icons

internal val SettingsLogLevelOptions = listOf("debug", "info", "warning", "error", "silent")
private val SettingsTrailingValueMaxWidth = 160.dp
internal val LocalSettingsSearchQuery = compositionLocalOf { "" }
internal val LocalRawConfigState = compositionLocalOf { SettingsRawConfigState(enabled = false) }

@Composable
internal fun SmallTitle(text: String) = SettingsSectionTitle(text)

@Composable
internal fun ArrowPreference(
    title: String,
    onClick: () -> Unit,
    summary: String = "",
    icon: Int = Icons.Rounded.Tune,
    accent: IconAccent = IconAccent.Surface,
) = SettingsActionRow(title = title, summary = summary, icon = icon, onClick = onClick, accent = accent)

@Composable
internal fun SwitchPreference(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    summary: String = "",
    icon: Int = Icons.Rounded.Tune,
    accent: IconAccent = IconAccent.Surface,
) = SettingsSwitchRow(
    accent = accent,
    title = title,
    summary = summary,
    icon = icon,
    checked = checked,
    onCheckedChange = onCheckedChange,
)

@Composable
internal fun OverlayDropdownPreference(
    title: String,
    items: List<String>,
    selectedIndex: Int,
    onSelectedIndexChange: (Int) -> Unit,
    summary: String = "",
    icon: Int = Icons.Rounded.Tune,
    accent: IconAccent = IconAccent.Surface,
) = SettingsDropdownRow(
    accent = accent,
    title = title,
    summary = summary,
    icon = icon,
    items = items,
    selectedIndex = selectedIndex,
    onSelectedIndexChange = onSelectedIndexChange,
)

@Composable
internal fun SettingsSearchProvider(query: String, content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalSettingsSearchQuery provides query, content = content)
}

@Composable
internal fun SettingsRawConfigProvider(
    state: SettingsRawConfigState,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(LocalRawConfigState provides state, content = content)
}

@Composable
internal fun SettingsSectionTitle(text: String) {
    // Library group title: grey, with its own top/bottom rhythm.
    SmartisanSectionTitle(text = text)
}

@Composable
internal fun SettingsSectionCard(
    modifier: Modifier = Modifier,
    bottomPadding: Dp = 12.dp,
    content: @Composable ColumnScope.() -> Unit,
) {
    SmartisanGroup(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = bottomPadding),
        position = SmartisanGroupRowPosition.Single,
    ) {
        Column(content = content)
    }
}

@Composable
internal fun SettingsActionRow(
    title: String,
    icon: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    summary: String = "",
    value: String = "",
    accent: IconAccent = IconAccent.Surface,
) {
    if (!settingsRowMatchesQuery(title, summary, value)) return
    SettingsRow(
        accent = accent,
        title = title,
        icon = icon,
        summary = summary,
        value = value,
        modifier = modifier,
        onClick = onClick,
        trailing = {
            AsteriskIcon(
                res = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                contentDescription = null,
            )
        },
    )
}

@Composable
internal fun SettingsSwitchRow(
    title: String,
    icon: Int,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    summary: String = "",
    enabled: Boolean = true,
    accent: IconAccent = IconAccent.Surface,
) {
    if (!settingsRowMatchesQuery(title, summary, checked.toString())) return
    SettingsRow(
        accent = accent,
        title = title,
        icon = icon,
        summary = summary,
        modifier = modifier,
        enabled = enabled,
        onClick = { onCheckedChange(!checked) },
        trailing = {
            SmartisanSwitch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                enabled = enabled,
            )
        },
    )
}

@Composable
internal fun SettingsDropdownRow(
    title: String,
    icon: Int,
    items: List<String>,
    selectedIndex: Int,
    onSelectedIndexChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    summary: String = "",
    accent: IconAccent = IconAccent.Surface,
    horizontalPadding: Dp = 16.dp,
) {
    if (items.isEmpty()) return
    val safeIndex = selectedIndex.coerceIn(items.indices)
    val value = items[safeIndex]
    if (!settingsRowMatchesQuery(title, summary, value, items)) return
    var expanded by remember { mutableStateOf(false) }
    SettingsRow(
        accent = accent,
        title = title,
        icon = icon,
        summary = summary,
        value = value,
        modifier = modifier,
        onClick = { expanded = !expanded },
        trailing = {
            AsteriskDropdownAnchor(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                menuModifier = Modifier.widthIn(min = 180.dp, max = 280.dp),
            ) {
                items.forEachIndexed { index, item ->
                    AsteriskDropdownMenuItem(
                        text = item,
                        selected = index == safeIndex,
                        onClick = {
                            expanded = false
                            onSelectedIndexChange(index)
                        },
                    )
                }
            }
        },
    )
}

@Composable
internal fun SettingsReadOnlyRow(
    title: String,
    value: String,
    source: String,
    modifier: Modifier = Modifier,
    icon: Int = Icons.Rounded.Lock,
    summary: String = "",
    accent: IconAccent = IconAccent.Surface,
) {
    if (!settingsRowMatchesQuery(title, summary, value)) return
    SettingsRow(
        accent = accent,
        title = title,
        icon = icon,
        summary = summary,
        value = value,
        modifier = modifier.semantics { contentDescription = "$title，$value，$source" },
        trailing = {
            SmartisanText(
                text = source,
                style = LocalSmartisanTypography.current.listItemSecondary,
                color = LocalSmartisanColors.current.accent,
            )
        },
    )
}

@Composable
private fun SettingsRow(
    title: String,
    icon: Int,
    modifier: Modifier = Modifier,
    summary: String = "",
    value: String = "",
    trailing: (@Composable () -> Unit)? = null,
    accent: IconAccent = IconAccent.Surface,
    enabled: Boolean = true,
    onClick: (() -> Unit)? = null,
) {
    // Use the library list row verbatim (60dp leading slot, 15sp/12.5sp text, 6dp trailing
    // margin); the section's group card provides the background texture, so the row itself
    // is transparent.
    SmartisanListItem(
        title = title,
        modifier = modifier,
        summary = summary.ifBlank { null },
        leading = { MaskedPreferenceIcon(icon = icon, accent = accent) },
        trailing = if (value.isNotBlank() || trailing != null) {
            {
                if (value.isNotBlank()) {
                    SmartisanText(
                        text = value,
                        modifier = Modifier.widthIn(max = SettingsTrailingValueMaxWidth),
                        style = LocalSmartisanTypography.current.listItemSecondary,
                        color = LocalSmartisanColors.current.textTertiary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                trailing?.invoke()
            }
        } else {
            null
        },
        enabled = enabled,
        rowBackgroundRes = null,
        onClick = onClick,
    )
}

@Composable
internal fun settingsRowMatchesQuery(
    title: String,
    summary: String,
    value: String,
    optionText: List<String> = emptyList(),
): Boolean =
    SettingsSearchItem(SettingsSectionId.Apps, title, summary, value, optionText)
        .matchesSettingsQuery(LocalSettingsSearchQuery.current)

@Composable
internal fun localProxySettingsSummary(
    runMode: Int,
    port: String,
    listenAllInterfaces: Boolean,
    transparentProxyPort: String,
    bpf2SocksBridgePort: String,
    socks5ProxyPort: String,
): String {
    val summary = if (listenAllInterfaces) {
        stringResource(R.string.settings_local_proxy_summary_all_interfaces)
    } else {
        stringResource(R.string.settings_local_proxy_summary_fixed)
    }
    val localProxySummary = summary.formatTemplate("port" to port)
    val inboundProxySummary = when (runMode) {
        RunModeTproxy -> stringResource(R.string.settings_local_proxy_summary_tproxy)
            .formatTemplate("port" to transparentProxyPort)
        RunModeTun2Socks -> stringResource(R.string.settings_local_proxy_summary_tun2socks)
            .formatTemplate("port" to socks5ProxyPort)
        RunModeBpf2Socks -> stringResource(R.string.settings_local_proxy_summary_bpf2socks)
            .formatTemplate("bridgePort" to bpf2SocksBridgePort, "socksPort" to socks5ProxyPort)
        else -> ""
    }
    return listOf(inboundProxySummary, localProxySummary).filter(String::isNotBlank).joinToString("，")
}
