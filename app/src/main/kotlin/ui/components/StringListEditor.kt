// Copyright 2026, AsteriskMETA contributors
// SPDX-License-Identifier: GPL-3.0

package ui.components

import ui.components.AsteriskField
import cc.wuersan008.smartisanx.ui.layout.SmartisanCard
import cc.wuersan008.smartisanx.core.theme.LocalSmartisanShapes
import cc.wuersan008.smartisanx.core.theme.LocalSmartisanTypography
import cc.wuersan008.smartisanx.core.theme.LocalSmartisanColors
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import ui.icons.AsteriskIcons as Icons
import cc.wuersan008.smartisanx.ui.basic.SmartisanIcon
import cc.wuersan008.smartisanx.ui.basic.SmartisanIconButton

import cc.wuersan008.smartisanx.ui.basic.SmartisanText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import app.R
import ui.theme.AsteriskShapeTokens
import ui.theme.AsteriskMotion

@Composable
internal fun StringListEditor(
    editorKey: Any?,
    title: String,
    values: List<String>,
    onValuesChange: (List<String>) -> Unit,
    emptyText: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    validateInput: (String) -> String? = { null },
    normalizeInput: (String) -> String = String::trim,
    onPendingChange: ((Boolean) -> Unit)? = null,
) {
    var input by remember(editorKey, title) { mutableStateOf("") }
    var editingIndex by remember(editorKey, title) { mutableIntStateOf(-1) }
    val editInput = key(editorKey, title) { rememberTextFieldState() }
    var showBulkEditor by remember(editorKey, title) { mutableStateOf(false) }
    var bulkInput by remember(editorKey, title) { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    val sanitizedValues = normalizeStringListValues(values, normalizeInput)
    val normalizedInput = normalizeInput(input)
    val inputError = normalizedInput.takeIf(String::isNotEmpty)?.let(validateInput)
    val canAdd = normalizedInput.isNotEmpty() && inputError == null
    val hasPendingInput = hasPendingStringListEdit(input, editingIndex, normalizeInput)
    val currentOnPendingChange by rememberUpdatedState(onPendingChange)

    LaunchedEffect(editorKey, title) {
        input = ""
        editingIndex = -1
        editInput.setTextAndPlaceCursorAtEnd("")
        showBulkEditor = false
    }

    LaunchedEffect(hasPendingInput) {
        currentOnPendingChange?.invoke(hasPendingInput)
    }

    SmartisanCard(
        modifier = modifier.padding(horizontal = 16.dp).fillMaxWidth(),
        color = LocalSmartisanColors.current.surfaceRaised,
        shape = LocalSmartisanShapes.current.large,
    ) {
        Column(Modifier.fillMaxWidth().padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                SmartisanText(
                    text = title,
                    modifier = Modifier.weight(1f),
                    style = LocalSmartisanTypography.current.listRowPrimary,
                    color = LocalSmartisanColors.current.textPrimary,
                )
                AsteriskInfoChip(text = sanitizedValues.size.toString())
                SmartisanIconButton(
                    onClick = {
                        bulkInput = sanitizedValues.joinToString("\n")
                        showBulkEditor = true
                    },
                ) {
                    AsteriskIcon(Icons.Rounded.EditNote, stringResource(R.string.common_edit_all))
                }
            }
            description?.let { StringListStatusText(it) }
            Row(verticalAlignment = Alignment.CenterVertically) {
                StringListEditableField(
                    value = input,
                    onValueChange = { input = it },
                    isError = inputError != null,
                    supportingText = inputError,
                )
                Spacer(Modifier.width(4.dp))
                SmartisanIconButton(
                    enabled = canAdd,
                    onClick = {
                        val result = addStringListValue(
                            sanitizedValues, input, validateInput, normalizeInput,
                        )
                        if (result.error == null) {
                            onValuesChange(normalizeStringListValues(result.values, normalizeInput))
                            input = ""
                        }
                    },
                ) {
                    AsteriskIcon(Icons.Rounded.Add, stringResource(R.string.common_add))
                }
            }
            AnimatedVisibility(
                visible = hasPendingInput,
                enter = AsteriskMotion.contentEnter(),
                exit = AsteriskMotion.contentExit(),
                label = "string-list-pending-value",
            ) {
                StringListStatusText(stringResource(R.string.string_list_pending_value))
            }
            if (sanitizedValues.isEmpty()) StringListStatusText(emptyText)
            sanitizedValues.forEachIndexed { index, value ->
                val editing = editingIndex == index
                val actionMotion = AsteriskMotion.fastSpatial<Float>()
                val editError = if (editing) {
                    normalizeInput(editInput.text.toString()).takeIf(String::isNotEmpty)?.let(validateInput)
                        ?: if (normalizeInput(editInput.text.toString()).isEmpty()) {
                            stringResource(R.string.string_list_item_empty)
                        } else {
                            null
                        }
                } else {
                    null
                }
                key(stringListItemKey(index)) {
                    val focusRequester = remember { FocusRequester() }
                    LaunchedEffect(editing) {
                        if (editing) focusRequester.requestFocus()
                    }
                    SmartisanCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 6.dp),
                        color = LocalSmartisanColors.current.surfaceRaised,
                        shape = AsteriskShapeTokens.InnerContainer,
                    ) {
                        Column {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(min = 56.dp)
                                    .padding(horizontal = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                StringListItemField(
                                    editing = editing,
                                    value = value,
                                    editState = editInput,
                                    isError = editError != null,
                                    focusRequester = focusRequester,
                                )
                                AnimatedContent(
                                    targetState = editing,
                                    modifier = Modifier.width(96.dp),
                                    transitionSpec = AsteriskMotion.scaleSwap(actionMotion),
                                    contentAlignment = Alignment.CenterEnd,
                                    label = "string-list-actions",
                                ) { isEditing ->
                                    Row(modifier = Modifier.fillMaxWidth()) {
                                        if (isEditing) {
                                            StringListAction(
                                                icon = Icons.Rounded.Check,
                                                description = stringResource(R.string.common_save),
                                                enabled = editError == null,
                                            ) {
                                                val result = editStringListValue(
                                                    sanitizedValues,
                                                    index,
                                                    editInput.text.toString(),
                                                    validateInput,
                                                    normalizeInput,
                                                )
                                                if (result.error == null) {
                                                    onValuesChange(
                                                        normalizeStringListValues(result.values, normalizeInput),
                                                    )
                                                    focusManager.clearFocus()
                                                    editingIndex = -1
                                                }
                                            }
                                            StringListAction(
                                                Icons.Rounded.Close,
                                                stringResource(R.string.common_cancel),
                                            ) {
                                                focusManager.clearFocus()
                                                editingIndex = -1
                                            }
                                        } else {
                                            StringListAction(
                                                Icons.Rounded.Edit,
                                                stringResource(R.string.common_edit),
                                            ) {
                                                editInput.setTextAndPlaceCursorAtEnd(value)
                                                editingIndex = index
                                            }
                                            StringListAction(
                                                Icons.Rounded.Delete,
                                                stringResource(R.string.common_delete),
                                            ) {
                                                onValuesChange(
                                                    sanitizedValues.filterIndexed { valueIndex, _ ->
                                                        valueIndex != index
                                                    },
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                            AnimatedVisibility(
                                visible = editing && editError != null,
                                enter = AsteriskMotion.contentEnter(),
                                exit = AsteriskMotion.contentExit(),
                                label = "string-list-edit-error",
                            ) {
                                editError?.let { StringListStatusText(it, error = true) }
                            }
                        }
                    }
                }
            }
        }
    }

    StringListBulkEditorSheet(
        show = showBulkEditor,
        title = title,
        value = bulkInput,
        onValueChange = { bulkInput = it },
        validateInput = validateInput,
        normalizeInput = normalizeInput,
        onDismissRequest = { showBulkEditor = false },
        onSave = { nextValues ->
            editingIndex = -1
            onValuesChange(normalizeStringListValues(nextValues, normalizeInput))
            showBulkEditor = false
        },
    )
}

@Composable
private fun RowScope.StringListItemField(
    editing: Boolean,
    value: String,
    editState: TextFieldState,
    isError: Boolean,
    focusRequester: FocusRequester,
) {
    if (editing) {
        val interactionSource = remember { MutableInteractionSource() }
        val focused by interactionSource.collectIsFocusedAsState()
        val borderColor = when {
            isError -> LocalSmartisanColors.current.warning
            focused -> LocalSmartisanColors.current.accent
            else -> LocalSmartisanColors.current.rowDivider
        }
        val borderWidth = if (focused) 2.dp else 1.dp
        BasicTextField(
            state = editState,
            modifier = Modifier
                .weight(1f)
                .heightIn(min = 56.dp)
                .focusRequester(focusRequester),
            lineLimits = TextFieldLineLimits.SingleLine,
            textStyle = LocalSmartisanTypography.current.body.copy(color = LocalSmartisanColors.current.textPrimary),
            cursorBrush = SolidColor(LocalSmartisanColors.current.accent),
            interactionSource = interactionSource,
            decorator = { innerTextField ->
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.CenterStart,
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 56.dp)
                            .expandHorizontallyBy(StringListItemEditorBorderExtension)
                            .background(
                                color = LocalSmartisanColors.current.surfaceRaised,
                                shape = AsteriskShapeTokens.InnerContainer,
                            )
                            .border(
                                width = borderWidth,
                                color = borderColor,
                                shape = AsteriskShapeTokens.InnerContainer,
                            ),
                    )
                    innerTextField()
                }
            },
        )
    } else {
        SmartisanText(
            text = value,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = LocalSmartisanTypography.current.body,
            color = LocalSmartisanColors.current.textPrimary,
        )
    }
}

private fun Modifier.expandHorizontallyBy(extra: androidx.compose.ui.unit.Dp): Modifier = layout { measurable, constraints ->
    val extraPx = extra.roundToPx()
    val placeable = measurable.measure(
        constraints.copy(
            minWidth = constraints.minWidth + extraPx * 2,
            maxWidth = constraints.maxWidth + extraPx * 2,
        ),
    )
    layout(width = constraints.maxWidth, height = placeable.height) {
        placeable.placeRelative(x = -extraPx, y = 0)
    }
}

private val StringListItemEditorBorderExtension = 8.dp

@Composable
private fun RowScope.StringListEditableField(
    value: String,
    onValueChange: (String) -> Unit,
    isError: Boolean,
    supportingText: String? = null,
) {
    Box(modifier = Modifier.weight(1f)) {
        AsteriskField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = isError,
            supportingText = supportingText?.let { message -> ({ SmartisanText(message) }) },
            shape = AsteriskShapeTokens.InnerContainer,
        )
    }
}

@Composable
internal fun StringListStatusText(
    text: String,
    modifier: Modifier = Modifier,
    error: Boolean = false,
) {
    SmartisanText(
        text = text,
        modifier = modifier.padding(horizontal = 4.dp, vertical = 4.dp),
        style = LocalSmartisanTypography.current.listItemSecondary,
        color = if (error) LocalSmartisanColors.current.warning else LocalSmartisanColors.current.textSecondary,
    )
}

@Composable
private fun StringListAction(
    icon: Int,
    description: String,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    SmartisanIconButton(onClick = onClick, enabled = enabled, modifier = Modifier.size(48.dp)) {
        AsteriskIcon(icon, description)
    }
}

@Composable
private fun StringListBulkEditorSheet(
    show: Boolean,
    title: String,
    value: String,
    onValueChange: (String) -> Unit,
    validateInput: (String) -> String?,
    normalizeInput: (String) -> String,
    onDismissRequest: () -> Unit,
    onSave: (List<String>) -> Unit,
) {
    val result = parseStringListBatch(value, validateInput, normalizeInput)
    val error = result.error?.let { stringResource(R.string.string_list_line_error, result.errorLine ?: 1, it) }
    AsteriskModalBottomSheet(
        show = show,
        onDismissRequest = onDismissRequest,
        title = title,
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
                enabled = error == null,
                onClick = { result.values?.let(onSave) },
            )
        },
    ) {
        Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp).padding(bottom = 24.dp)) {
            AsteriskField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth().heightIn(min = 220.dp).padding(top = 12.dp),
                minLines = 8,
                isError = error != null,
                supportingText = error?.let { message -> ({ SmartisanText(message) }) },
                shape = AsteriskShapeTokens.InnerContainer,
            )
        }
    }
}
