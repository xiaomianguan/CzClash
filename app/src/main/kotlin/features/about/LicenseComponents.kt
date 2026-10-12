// Copyright 2026, AsteriskMETA contributors
// SPDX-License-Identifier: GPL-3.0

package features.about

import ui.components.AsteriskIcon
import cc.wuersan008.smartisanx.ui.basic.SmartisanSurface
import cc.wuersan008.smartisanx.core.theme.LocalSmartisanTypography
import cc.wuersan008.smartisanx.core.theme.LocalSmartisanColors
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import ui.icons.AsteriskIcons as Icons
import cc.wuersan008.smartisanx.ui.basic.SmartisanIcon
import cc.wuersan008.smartisanx.ui.basic.SmartisanText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import features.about.license.Library
import ui.components.AsteriskChipTone
import ui.components.AsteriskInfoChip
import ui.components.AsteriskPageCard
import ui.theme.AsteriskShapeTokens

@Composable
internal fun LibraryLicenseCard(
    library: Library,
    modifier: Modifier = Modifier,
) {
    val uriHandler = LocalUriHandler.current
    val hasWebsite = hasLibraryWebsite(library)
    val artifactVersion = library.artifactVersion?.takeIf(String::isNotBlank)
    AsteriskPageCard(
        modifier = modifier.fillMaxWidth(),
        onClick = if (hasWebsite) {
            {
                library.website?.takeIf(String::isNotBlank)?.let(uriHandler::openUri)
            }
        } else {
            null
        },
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SmartisanSurface(
                shape = AsteriskShapeTokens.SmallContainer,
                color = LocalSmartisanColors.current.surfaceRaised,
                contentColor = LocalSmartisanColors.current.accent,
            ) {
                AsteriskIcon(
                    res = Icons.Rounded.Widgets,
                    contentDescription = null,
                    tint = LocalSmartisanColors.current.accent,
                    modifier = Modifier.padding(10.dp).size(22.dp),
                )
            }
            androidx.compose.foundation.layout.Spacer(Modifier.size(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                SmartisanText(
                    text = library.name,
                    style = LocalSmartisanTypography.current.listItemPrimary,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                SmartisanText(
                    text = library.uniqueId,
                    style = LocalSmartisanTypography.current.listItemSecondary,
                    color = LocalSmartisanColors.current.textSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                if (library.licenses.isNotEmpty() || artifactVersion != null) {
                    Row(
                        modifier = Modifier.padding(top = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        if (library.licenses.isNotEmpty()) {
                            AsteriskInfoChip(
                                text = library.licenses.joinToString(),
                                tone = AsteriskChipTone.Secondary,
                            )
                        }
                        artifactVersion?.let { version ->
                            AsteriskInfoChip(text = version)
                        }
                    }
                }
            }
            if (hasWebsite) {
                AsteriskIcon(
                    res = Icons.AutoMirrored.Rounded.OpenInNew,
                    contentDescription = null,
                    tint = LocalSmartisanColors.current.textSecondary,
                    modifier = Modifier.padding(start = 12.dp),
                )
            }
        }
    }
}
