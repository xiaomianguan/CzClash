// Copyright 2026, AsteriskMETA contributors
// SPDX-License-Identifier: GPL-3.0

package ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import cc.wuersan008.smartisanx.ui.control.SmartisanButtonTabGroup

@Immutable
internal data class AsteriskSegmentItem<T>(
    val value: T,
    val label: String,
    val icon: Int? = null,
)

/** Segmented choice backed by the Smartisan `ButtonTabGroup` (original segment selectors). */
@Composable
internal fun <T> AsteriskSegmentedControl(
    items: List<AsteriskSegmentItem<T>>,
    selectedValue: T,
    onSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    if (items.isEmpty()) return
    val selectedIndex = items.indexOfFirst { it.value == selectedValue }.coerceAtLeast(0)
    SmartisanButtonTabGroup(
        items = items.map { it.label },
        selectedIndex = selectedIndex,
        onSelectedChange = { index -> items.getOrNull(index)?.let { onSelected(it.value) } },
        modifier = modifier,
        disabledIndices = if (enabled) emptySet() else items.indices.toSet(),
    )
}
