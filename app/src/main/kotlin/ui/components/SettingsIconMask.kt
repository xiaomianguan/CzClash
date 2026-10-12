// Copyright 2026, AsteriskBOX contributors
// SPDX-License-Identifier: GPL-3.0

package ui.components

import cc.wuersan008.smartisanx.ui.basic.SmartisanSurface
import androidx.compose.foundation.layout.size
import cc.wuersan008.smartisanx.ui.basic.SmartisanIcon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cc.wuersan008.smartisanx.core.theme.LocalSmartisanColors
import ui.isInDarkTheme

/**
 * Semantic accent slot for a preference-row icon.
 *
 * In the Smartisan visual language row icons are flat line art rather than the Material
 * "dashboard tile" circles used previously, so this now only carries the icon tint.
 */
@Immutable
enum class IconAccent(
    internal val lightBg: Color,
    internal val darkBg: Color,
    internal val lightFg: Color,
) {
    /** Fallback: render using the theme's secondary text color. */
    Surface(Color.Unspecified, Color.Unspecified, Color.Unspecified),

    MaskBlue(Color(0xFFD0E4FF), Color(0xFFA1C9FF), Color(0xFF04409F)),
    MaskBlueVariant(Color(0xFFBDE9FF), Color(0xFF67D4FF), Color(0xFF004D68)),
    MaskPink(Color(0xFFFFD8EF), Color(0xFFFFAEE4), Color(0xFF8D0053)),
    MaskOrange(Color(0xFFFFDCC3), Color(0xFFFFB683), Color(0xFF753403)),
    MaskYellow(Color(0xFFFFE07C), Color(0xFFFCBD00), Color(0xFF6D3A01)),
    MaskGreen(Color(0xFFBEEFBB), Color(0xFF80DA88), Color(0xFF00522C)),
    MaskGrey(Color(0xFFE3E3E3), Color(0xFFC7C7C7), Color(0xFF474747)),
    MaskRed(Color(0xFFFFDADC), Color(0xFFFFB3AE), Color(0xFF8A1A16)),
    MaskPurple(Color(0xFFEEDCFE), Color(0xFFD9BAFD), Color(0xFF5629A4)),
}

/**
 * Preference-row leading icon: a flat monochrome 24dp glyph (Smartisan list rows use a
 * single grey glyph colour rather than per-item hues).
 */
@Composable
fun MaskedPreferenceIcon(
    icon: Int,
    @Suppress("UNUSED_PARAMETER") accent: IconAccent,
    modifier: Modifier = Modifier,
) {
    val colors = LocalSmartisanColors.current
    AsteriskIcon(
        res = icon,
        contentDescription = null,
        tint = colors.textSecondary,
        modifier = modifier.size(24.dp),
    )
}
