// Copyright 2026, AsteriskMETA contributors
// SPDX-License-Identifier: GPL-3.0

package features.resources

import ui.components.AsteriskIcon
import ui.components.AsteriskStateField
import cc.wuersan008.smartisanx.ui.basic.SmartisanSurface
import cc.wuersan008.smartisanx.ui.layout.SmartisanCard
import cc.wuersan008.smartisanx.core.theme.LocalSmartisanShapes
import cc.wuersan008.smartisanx.core.theme.LocalSmartisanTypography
import cc.wuersan008.smartisanx.core.theme.LocalSmartisanColors
import ui.components.AsteriskDropdownAnchor
import ui.components.AsteriskDropdownMenuItem
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import app.R
import app.ResourceFileStatus
import ui.components.AsteriskActionButton
import ui.components.AsteriskChipTone
import ui.components.AsteriskInfoChip
import ui.components.AsteriskModalBottomSheet
import ui.components.AsteriskStatusCard
import ui.components.AsteriskTonalButton
import ui.text.formatTemplate
import utils.ReadableByteUnit
import utils.toReadableBytes
import utils.toReadableDateTimeOrDash
import ui.icons.AsteriskIcons as Icons
import ui.theme.AsteriskShapeTokens

@Composable
internal fun settingsResourceFileSourceOptions() = listOf(
    stringResource(R.string.settings_resource_files_source_metacubex_github),
    stringResource(R.string.settings_resource_files_source_custom),
)

@Composable
internal fun ResourceOverviewCard(
    overview: ResourceOverviewState,
    sourceOptions: List<String>,
    selectedSource: Int,
    lastUpdatedAtMillis: Long,
    updating: Boolean,
    onSourceChange: (Int) -> Unit,
    onUpdate: () -> Unit,
    onCancel: () -> Unit,
    onSettings: () -> Unit,
    modifier: Modifier = Modifier,
    actionsEnabled: Boolean = true,
) {
    var sourceMenuExpanded by remember { mutableStateOf(false) }
    val safeSource = selectedSource.coerceIn(sourceOptions.indices)
    val sourceText = stringResource(R.string.settings_resource_files_source_value, sourceOptions[safeSource])
    val lastCheckText = stringResource(R.string.settings_resource_files_last_check)
        .formatTemplate("time" to lastUpdatedAtMillis.toReadableDateTimeOrDash())
    AsteriskStatusCard(
        modifier = modifier,
        status = "$sourceText\n$lastCheckText",
        controls = {
            if (updating) {
                AsteriskTonalButton(
                    text = stringResource(R.string.common_cancel),
                    icon = Icons.Rounded.Close,
                    onClick = onCancel,
                )
            } else {
                AsteriskTonalButton(
                    text = stringResource(R.string.settings_resource_files_update),
                    icon = Icons.Rounded.Refresh,
                    onClick = onUpdate,
                    enabled = actionsEnabled,
                )
            }
        },
    ) {
        // Move the hit-test bounds with the button, while keeping the title anchored.
        Box(modifier = Modifier.fillMaxWidth().offset(x = 12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth().offset(x = (-12).dp).padding(end = 48.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                AsteriskIcon(
                    res = Icons.Rounded.Storage,
                    contentDescription = null,
                    tint = LocalSmartisanColors.current.accent,
                )
                SmartisanText(
                    text = stringResource(R.string.settings_resource_files_overview),
                    modifier = Modifier.weight(1f),
                    style = LocalSmartisanTypography.current.listItemPrimary,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            // Keep the full touch target without letting it add space above the title.
            SmartisanIconButton(
                onClick = onSettings,
                modifier = Modifier
                    .matchParentSize()
                    .wrapContentSize(Alignment.CenterEnd, unbounded = true)
                    .size(48.dp),
            ) {
                AsteriskIcon(
                    res = Icons.Rounded.Settings,
                    contentDescription = stringResource(R.string.settings_title),
                    modifier = Modifier.size(20.dp),
                    tint = LocalSmartisanColors.current.textSecondary,
                )
            }
        }
        SmartisanText(
            text = pluralStringResource(
                R.plurals.settings_resource_files_overview_ready,
                overview.readyCount,
                overview.readyCount,
                overview.totalCount,
                overview.totalSizeBytes.toReadableBytes(maxUnit = ReadableByteUnit.GiB),
            ),
            style = LocalSmartisanTypography.current.title,
            fontWeight = FontWeight.SemiBold,
        )
        if (updating) {
            AsteriskSpinner(modifier = Modifier.fillMaxWidth(), size = 20.dp)
        }
        Box(modifier = Modifier.align(Alignment.End)) {
            AsteriskLinkButton(
                onClick = { sourceMenuExpanded = true },
                enabled = actionsEnabled && !updating,
            ) {
                SmartisanText(stringResource(R.string.settings_resource_files_source))
                Spacer(Modifier.width(4.dp))
                AsteriskDropdownAnchor(
                    expanded = sourceMenuExpanded,
                    onDismissRequest = { sourceMenuExpanded = false },
                ) {
                    sourceOptions.forEachIndexed { index, option ->
                        AsteriskDropdownMenuItem(
                            text = option,
                            selected = index == safeSource,
                            onClick = {
                                sourceMenuExpanded = false
                                onSourceChange(index)
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
internal fun CustomResourceSourceEditorSheet(
    show: Boolean,
    geoIpUrlState: TextFieldState,
    geoSiteUrlState: TextFieldState,
    mmdbUrlState: TextFieldState,
    asnUrlState: TextFieldState,
    directCidrIpv4UrlState: TextFieldState,
    directCidrIpv6UrlState: TextFieldState,
    onDismissRequest: () -> Unit,
    onSave: () -> Unit,
) {
    AsteriskModalBottomSheet(
        show = show,
        onDismissRequest = onDismissRequest,
        title = stringResource(R.string.settings_resource_files_source_custom_title),
        startAction = {
            AsteriskActionButton(
                text = stringResource(R.string.common_cancel),
                icon = Icons.Rounded.Close,
                onClick = onDismissRequest,
            )
        },
        endAction = {
            AsteriskActionButton(
                text = stringResource(R.string.common_save),
                icon = Icons.Rounded.Save,
                onClick = onSave,
            )
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 24.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 280.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                ResourceUrlField(geoIpUrlState, ResourceFileGeoIpName)
                ResourceUrlField(geoSiteUrlState, ResourceFileGeoSiteName)
                ResourceUrlField(mmdbUrlState, ResourceFileMmdbName)
                ResourceUrlField(asnUrlState, ResourceFileAsnName)
                ResourceUrlField(directCidrIpv4UrlState, ResourceFileDirectCidrIpv4Name)
                ResourceUrlField(directCidrIpv6UrlState, ResourceFileDirectCidrIpv6Name)
            }
        }
    }
}

@Composable
private fun ResourceUrlField(state: TextFieldState, label: String) {
    AsteriskStateField(
        shape = AsteriskShapeTokens.InnerContainer,
        state = state,
        label = { SmartisanText(label) },
        lineLimits = TextFieldLineLimits.SingleLine,
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
internal fun ResourceFileCard(
    fileName: String,
    status: ResourceFileStatus,
    onReplace: () -> Unit,
    modifier: Modifier = Modifier,
    updateState: ResourceFileUpdateDisplayState = ResourceFileUpdateDisplayState.Idle,
    onUpdate: (() -> Unit)? = null,
    onRestore: (() -> Unit)? = null,
    description: String? = null,
    actionsEnabled: Boolean = true,
) {
    val actions = buildList {
        onUpdate?.let { add(ResourceMenuEntry(ResourceDisplayAction.Update, it)) }
        add(ResourceMenuEntry(ResourceDisplayAction.Replace, onReplace))
        onRestore?.let { add(ResourceMenuEntry(ResourceDisplayAction.Restore, it)) }
    }
    ResourceFileCardSurface(
        fileName = fileName,
        status = status,
        modifier = modifier,
        description = description,
        updateState = updateState,
        actionsEnabled = actionsEnabled,
        actions = actions,
    )
}

private data class ResourceMenuEntry(
    val action: ResourceDisplayAction,
    val onClick: () -> Unit,
)

@Composable
private fun ResourceFileCardSurface(
    fileName: String,
    status: ResourceFileStatus,
    updateState: ResourceFileUpdateDisplayState,
    actionsEnabled: Boolean,
    actions: List<ResourceMenuEntry>,
    modifier: Modifier = Modifier,
    description: String? = null,
) {
    var menuExpanded by remember { mutableStateOf(false) }
    val updatingDescription = stringResource(R.string.settings_resource_file_updating)
    SmartisanCard(
        modifier = modifier.fillMaxWidth().heightIn(min = 84.dp),
        color = LocalSmartisanColors.current.surface,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 14.dp, end = 4.dp, top = 12.dp, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SmartisanSurface(
                shape = LocalSmartisanShapes.current.medium,
                color = LocalSmartisanColors.current.surfaceRaised,
            ) {
                AsteriskIcon(
                    res = resourceVisualKind(fileName).icon(),
                    contentDescription = null,
                    modifier = Modifier.padding(10.dp).size(22.dp),
                    tint = LocalSmartisanColors.current.accent,
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                SmartisanText(
                    text = fileName,
                    style = LocalSmartisanTypography.current.listItemPrimary,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                if (!description.isNullOrBlank()) {
                    SmartisanText(
                        text = description,
                        style = LocalSmartisanTypography.current.listItemSecondary,
                        color = LocalSmartisanColors.current.textSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                if (status.exists) {
                    SmartisanText(
                        text = status.sizeText(),
                        style = LocalSmartisanTypography.current.listItemSecondary,
                        color = LocalSmartisanColors.current.textSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    if (!status.isBundledCore) {
                        SmartisanText(
                            text = status.updatedAtText(),
                            style = LocalSmartisanTypography.current.listItemSecondary,
                            color = LocalSmartisanColors.current.textSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                } else {
                    SmartisanText(
                        text = stringResource(R.string.settings_resource_files_missing),
                        style = LocalSmartisanTypography.current.listItemSecondary,
                        color = LocalSmartisanColors.current.warning,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
            ResourceFileStatusChip(
                text = stringResource(
                    if (status.exists) {
                        R.string.settings_resource_files_ready
                    } else {
                        R.string.settings_resource_files_missing
                    },
                ),
                ready = status.exists,
            )
            Box {
                when (updateState) {
                    ResourceFileUpdateDisplayState.Idle -> SmartisanIconButton(
                        onClick = { menuExpanded = true },
                        enabled = actionsEnabled,
                    ) {
                        AsteriskIcon(
                            res = Icons.Rounded.MoreVert,
                            contentDescription = stringResource(R.string.mihomo_profile_actions),
                        )
                    }

                    ResourceFileUpdateDisplayState.Queued -> SmartisanIconButton(
                        onClick = {},
                        enabled = false,
                    ) {
                        AsteriskIcon(
                            res = Icons.Rounded.Refresh,
                            contentDescription = stringResource(
                                R.string.settings_resource_file_update_queued,
                            ),
                        )
                    }

                    ResourceFileUpdateDisplayState.Running -> SmartisanIconButton(
                        onClick = {},
                        enabled = false,
                    ) {
                        AsteriskSpinner(
                            modifier = Modifier
                                .size(20.dp)
                                .semantics { contentDescription = updatingDescription },
                            )
                    }
                }
                AsteriskPopupMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false },
                ) {
                    actions.forEach { entry ->
                        AsteriskPopupMenuItem(
                            text = { SmartisanText(entry.action.label()) },
                            leadingIcon = { AsteriskIcon(entry.action.icon(), contentDescription = null) },
                            onClick = {
                                menuExpanded = false
                                entry.onClick()
                            },
                        )
                    }
                }
            }
        }
    }
}

private fun ResourceVisualKind.icon(): Int {
    return when (this) {
        ResourceVisualKind.Core -> Icons.Rounded.Memory
        ResourceVisualKind.GeoIp -> Icons.Rounded.Public
        ResourceVisualKind.GeoSite -> Icons.Rounded.TravelExplore
        ResourceVisualKind.Database -> Icons.Rounded.Storage
        ResourceVisualKind.Asn -> Icons.Rounded.Hub
        ResourceVisualKind.Cidr -> Icons.Rounded.Route
        ResourceVisualKind.Custom -> Icons.Rounded.DataObject
    }
}

private fun ResourceDisplayAction.icon(): Int {
    return when (this) {
        ResourceDisplayAction.Update -> Icons.Rounded.Refresh
        ResourceDisplayAction.Replace -> Icons.Rounded.FileUpload
        ResourceDisplayAction.Restore -> Icons.Rounded.History
    }
}

@Composable
private fun ResourceDisplayAction.label(): String {
    return stringResource(
        when (this) {
            ResourceDisplayAction.Update -> R.string.common_update
            ResourceDisplayAction.Replace -> R.string.common_replace
            ResourceDisplayAction.Restore -> R.string.common_restore
        },
    )
}

@Composable
private fun ResourceFileStatusChip(text: String, ready: Boolean) {
    AsteriskInfoChip(
        text = text,
        tone = if (ready) AsteriskChipTone.Primary else AsteriskChipTone.Error,
    )
}

@Composable
private fun ResourceFileStatus.updatedAtText(): String {
    return stringResource(R.string.settings_resource_files_updated_at)
        .formatTemplate("time" to updatedAtMillis.toReadableDateTimeOrDash())
}

@Composable
private fun ResourceFileStatus.sizeText(): String {
    return stringResource(R.string.settings_resource_files_size)
        .formatTemplate("size" to sizeBytes.toReadableBytes(maxUnit = ReadableByteUnit.MiB))
}
