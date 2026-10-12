// Copyright 2026, AsteriskMETA contributors
// SPDX-License-Identifier: GPL-3.0

package ui

import android.animation.ValueAnimator
import android.app.Activity
import android.content.res.Resources
import android.database.ContentObserver
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.WindowCompat
import app.R
import app.modes.ColorModeDark
import app.modes.ColorModeLight
import app.modes.ColorModeSystem
import app.modes.normalizeColorMode
import cc.wuersan008.smartisanx.core.theme.LocalSmartisanColors
import cc.wuersan008.smartisanx.core.theme.SmartisanColors
import cc.wuersan008.smartisanx.core.theme.SmartisanTheme
import cc.wuersan008.smartisanx.core.theme.darkSmartisanColors
import cc.wuersan008.smartisanx.core.theme.lightSmartisanColors
import ui.theme.AsteriskSpacing
import ui.theme.LocalReduceMotion
import ui.theme.LocalSpacing
import ui.theme.resolveTheme

val LocalColorMode = compositionLocalOf { ColorModeSystem }
private val LocalResolvedDarkTheme = compositionLocalOf { false }

/** Standard Smartisan selection/accent blue. */
private val SmartisanAccentBlue = Color(0xFF3D70EF)

/**
 * Application theme entry point.
 *
 * Everything is rendered with the Smartisan (锤子) design language from the vendored
 * `smartisanx` library: [SmartisanTheme] provides the palette, typography (including the
 * original Smartisan Compact CNS font) and shapes that every component reads directly.
 */
@Composable
fun AppTheme(
    colorMode: Int = ColorModeLight,
    keyColor: Color? = null,
    systemDark: Boolean,
    content: @Composable () -> Unit,
) {
    SynchronizeSplashTheme(colorMode)
    val resolution = resolveTheme(
        colorMode = colorMode,
        systemDark = systemDark,
        supportsSystemDynamicColor = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S,
        hasCustomSeed = keyColor != null,
    )
    val context = LocalContext.current
    var animationsEnabled by remember(context) {
        mutableStateOf(systemAnimationsEnabled())
    }
    DisposableEffect(context.contentResolver) {
        val observer = object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean) {
                animationsEnabled = systemAnimationsEnabled()
            }
        }
        context.contentResolver.registerContentObserver(
            Settings.Global.getUriFor(Settings.Global.ANIMATOR_DURATION_SCALE),
            false,
            observer,
        )
        onDispose { context.contentResolver.unregisterContentObserver(observer) }
    }

    val baseColors = if (resolution.isDark) darkSmartisanColors() else lightSmartisanColors()
    val smartisanColors = baseColors.withAccentOverride(SmartisanAccentBlue)

    SmartisanTheme(colors = smartisanColors) {
        val colors = LocalSmartisanColors.current
        CompositionLocalProvider(
            LocalColorMode provides colorMode,
            LocalResolvedDarkTheme provides resolution.isDark,
            LocalReduceMotion provides !animationsEnabled,
            LocalSpacing provides AsteriskSpacing(),
        ) {
            SystemBarAppearance(
                backgroundColor = colors.pageBackground,
                isDark = resolution.isDark,
            )
            content()
        }
    }
}

/** Applies the user-selected seed colour as the Smartisan accent (or keeps the default). */
private fun SmartisanColors.withAccentOverride(accent: Color?): SmartisanColors {
    if (accent == null) return this
    return SmartisanColors(
        pageBackground = pageBackground,
        surface = surface,
        surfaceRaised = surfaceRaised,
        surfacePressed = surfacePressed,
        surfaceDisabled = surfaceDisabled,
        titleBarBackground = titleBarBackground,
        divider = divider,
        rowDivider = rowDivider,
        textPrimary = textPrimary,
        textSecondary = textSecondary,
        textTertiary = textTertiary,
        textDisabled = textDisabled,
        textHint = textHint,
        accent = accent,
        accentPressed = accent,
        accentDisabled = accent.copy(alpha = 0.4f),
        onAccent = onAccent,
        link = link,
        linkPressed = linkPressed,
        success = success,
        warning = warning,
        selectionBackground = selectionBackground,
        pressedHighlight = pressedHighlight,
        onPressedHighlight = onPressedHighlight,
        switchTrack = switchTrack,
        switchTrackStroke = switchTrackStroke,
        switchKnob = switchKnob,
        switchIndicator = switchIndicator,
        scrollbarThumb = scrollbarThumb,
        scrim = scrim,
        isLight = isLight,
    )
}

@Composable
private fun SynchronizeSplashTheme(colorMode: Int) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return
    val view = LocalView.current
    LaunchedEffect(view, colorMode) {
        val activity = view.context as? Activity ?: return@LaunchedEffect
        val themeId = when (normalizeColorMode(colorMode)) {
            ColorModeLight -> R.style.AppTheme_Starting_Light
            ColorModeDark -> R.style.AppTheme_Starting_Dark
            else -> Resources.ID_NULL
        }
        activity.splashScreen.setSplashScreenTheme(themeId)
    }
}

private fun systemAnimationsEnabled(): Boolean = ValueAnimator.areAnimatorsEnabled()

@Composable
fun isInDarkTheme(): Boolean = LocalResolvedDarkTheme.current

val KeyColors: List<Color> = listOf(
    Color(0xFF3482FF),
    Color(0xFF36D167),
    Color(0xFF7C4DFF),
    Color(0xFFFFB21D),
    Color(0xFFFF5722),
    Color(0xFFFEDFE1),
    Color(0xFF00BCD4),
    Color(0xFF6F4E37),
)

fun keyColorFor(index: Int): Color? = if (index <= 0) null else KeyColors.getOrNull(index - 1)

@Composable
private fun SystemBarAppearance(
    backgroundColor: Color,
    isDark: Boolean,
) {
    val view = LocalView.current
    if (view.isInEditMode) return
    SideEffect {
        val window = (view.context as? Activity)?.window ?: return@SideEffect
        window.setBackgroundDrawable(backgroundColor.toArgb().toDrawable())
        WindowCompat.getInsetsController(window, view).apply {
            isAppearanceLightStatusBars = !isDark
            isAppearanceLightNavigationBars = !isDark
        }
    }
}
