// Copyright 2026, AsteriskMETA contributors
// SPDX-License-Identifier: GPL-3.0

package ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import cc.wuersan008.smartisanx.core.theme.LocalSmartisanColors
import cc.wuersan008.smartisanx.core.theme.LocalSmartisanTypography
import cc.wuersan008.smartisanx.core.utils.smartisanDrawableBackground
import cc.wuersan008.smartisanx.ui.asset.SmartisanDrawables
import cc.wuersan008.smartisanx.ui.input.SmartisanClearableField

/**
 * Smartisan text field for the classic `value` / `onValueChange` API.
 *
 * The label is rendered above the field (Smartisan settings style) and the input uses the
 * original Smartisan clearable field. `shape` / `inputTransformation` are accepted for call
 * compatibility and intentionally ignored.
 */
@Composable
internal fun AsteriskField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: (@Composable () -> Unit)? = null,
    supportingText: (@Composable () -> Unit)? = null,
    enabled: Boolean = true,
    singleLine: Boolean = true,
    @Suppress("UNUSED_PARAMETER") minLines: Int = 1,
    @Suppress("UNUSED_PARAMETER") isError: Boolean = false,
    @Suppress("UNUSED_PARAMETER") shape: Shape? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    @Suppress("UNUSED_PARAMETER") inputTransformation: InputTransformation? = null,
) {
    Column(modifier = modifier) {
        label?.let { Box(Modifier.padding(bottom = 6.dp)) { it() } }
        SmartisanClearableField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = singleLine,
            keyboardOptions = keyboardOptions,
        )
        supportingText?.let { Box(Modifier.padding(top = 4.dp)) { it() } }
    }
}

/**
 * Smartisan text field for the `TextFieldState` API, drawn on Compose Foundation's
 * `BasicTextField` with the Smartisan editor-row background instead of the Material
 * `OutlinedTextField`.
 */
@Composable
internal fun AsteriskStateField(
    state: TextFieldState,
    modifier: Modifier = Modifier,
    label: (@Composable () -> Unit)? = null,
    supportingText: (@Composable () -> Unit)? = null,
    enabled: Boolean = true,
    lineLimits: TextFieldLineLimits = TextFieldLineLimits.Default,
    inputTransformation: InputTransformation? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    @Suppress("UNUSED_PARAMETER") isError: Boolean = false,
    @Suppress("UNUSED_PARAMETER") shape: Shape? = null,
    @Suppress("UNUSED_PARAMETER") minLines: Int = 1,
    @Suppress("UNUSED_PARAMETER") singleLine: Boolean = false,
) {
    val colors = LocalSmartisanColors.current
    val typography = LocalSmartisanTypography.current
    Column(modifier = modifier) {
        label?.let { Box(Modifier.padding(bottom = 6.dp)) { it() } }
        BasicTextField(
            state = state,
            enabled = enabled,
            lineLimits = lineLimits,
            inputTransformation = inputTransformation,
            keyboardOptions = keyboardOptions,
            textStyle = typography.editorField.copy(color = colors.textPrimary),
            cursorBrush = SolidColor(colors.accent),
            modifier = Modifier
                .fillMaxWidth()
                .smartisanDrawableBackground(SmartisanDrawables.EditorRowSingle)
                .padding(horizontal = 10.dp, vertical = 12.dp),
        )
        supportingText?.let { Box(Modifier.padding(top = 4.dp)) { it() } }
    }
}
