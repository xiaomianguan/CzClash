// Copyright 2026, AsteriskMETA contributors
// SPDX-License-Identifier: GPL-3.0

package features.mihomo

import ui.components.AsteriskIcon
import ui.components.AsteriskStateField
import cc.wuersan008.smartisanx.ui.basic.SmartisanSurface
import cc.wuersan008.smartisanx.ui.layout.SmartisanCard
import cc.wuersan008.smartisanx.core.theme.LocalSmartisanShapes
import cc.wuersan008.smartisanx.core.theme.LocalSmartisanTypography
import cc.wuersan008.smartisanx.core.theme.LocalSmartisanColors
import androidx.compose.ui.window.Dialog
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.BoxWithConstraints
import ui.layout.codeEditorShowsSupportingContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import cc.wuersan008.smartisanx.ui.overlay.SmartisanConfirmDialog
import ui.components.AsteriskSpinner
import ui.components.AsteriskPopupMenu
import ui.components.AsteriskPopupMenuItem
import cc.wuersan008.smartisanx.ui.basic.SmartisanIcon
import cc.wuersan008.smartisanx.ui.basic.SmartisanIconButton

import ui.components.AsteriskScaffold
import cc.wuersan008.smartisanx.ui.basic.SmartisanText
import ui.components.AsteriskLinkButton
import ui.components.AsteriskTopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import app.DefaultMihomoOverrideScript
import app.DefaultMihomoOverrideScriptId
import app.DefaultMihomoProfileId
import app.LocalAppServices
import app.LocalAppStateStore
import app.LocalIsWideScreen
import app.LocalNavigator
import app.LocalUpdateAppState
import app.MihomoOverrideScriptState
import app.MihomoProfileState
import app.R
import app.collectAppState
import app.navigation.Route
import app.nextAvailableMihomoOverrideScriptId
import engine.mihomo.MihomoProfileEmptyErrorMessage
import engine.mihomo.MihomoProfileMissingErrorMessage
import engine.mihomo.MihomoProfileScriptDebugResult
import engine.mihomo.debugMihomoProfileScriptOverride
import features.settings.SettingsDropdownRow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ui.clipboard.setPlainText
import ui.components.AsteriskActionButton
import ui.components.AsteriskExpressiveCard
import ui.components.AsteriskExtendedFab
import ui.components.AsteriskFilterChip
import ui.components.AsteriskListRow
import ui.layout.pageContentPaddingWithCutout
import ui.layout.pageListPadding
import ui.text.formatTemplate
import ui.theme.AsteriskMotion
import ui.theme.ExpressiveShapeRole
import ui.icons.AsteriskIcons as Icons
import ui.theme.AsteriskShapeTokens

@Composable
fun MihomoOverrideScriptListPage(
    padding: PaddingValues,
) {
    val appState by LocalAppStateStore.current.collectAppState()
    val isWideScreen = LocalIsWideScreen.current
    val navigator = LocalNavigator.current
    val updateAppState = LocalUpdateAppState.current
    val services = LocalAppServices.current
    val scope = rememberCoroutineScope()
    val deletedMessage = stringResource(R.string.mihomo_override_script_deleted)
    val duplicateNameTemplate = stringResource(R.string.mihomo_override_script_duplicate_name)
    var pendingDeletion by remember { mutableStateOf<MihomoOverrideScriptState?>(null) }

    fun deleteScript(script: MihomoOverrideScriptState) {
        updateAppState { state ->
            state.copy(
                mihomoOverrideScripts = state.mihomoOverrideScripts.filterNot { item -> item.id == script.id },
                mihomoProfiles = state.mihomoProfiles.map { profile ->
                    if (profile.overrideScriptId == script.id) {
                        profile.copy(overrideScriptId = DefaultMihomoOverrideScriptId)
                    } else {
                        profile
                    }
                },
            )
        }
        scope.launch {
            services.tipNotifier.show(deletedMessage)
        }
    }

    fun duplicateScript(script: MihomoOverrideScriptState) {
        updateAppState { state ->
            val nextId = state.nextAvailableMihomoOverrideScriptId()
            state.copy(
                mihomoOverrideScripts = state.mihomoOverrideScripts + script.copy(
                    id = nextId,
                    name = duplicateNameTemplate.formatTemplate("name" to script.name),
                ),
                nextMihomoOverrideScriptId = nextId + 1,
            )
        }
    }

    AsteriskScaffold(
        topBar = {
            AsteriskTopAppBar(
                title = { SmartisanText(stringResource(R.string.mihomo_override_scripts_title)) },
                navigationIcon = {
                    SmartisanIconButton(onClick = { navigator.pop() }) {
                        AsteriskIcon(
                            res = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = stringResource(R.string.common_back),
                        )
                    }
                },
            )
        },
        floatingActionButton = {
            AsteriskExtendedFab(
                onClick = { navigator.push(Route.MihomoOverrideScriptEdit(draftId = System.nanoTime())) },
                icon = Icons.Rounded.Add,
                text = stringResource(R.string.mihomo_override_script_add),
            )
        },
    ) { innerPadding ->
        val contentPadding = pageContentPaddingWithCutout(
            innerPadding = innerPadding,
            outerPadding = padding,
            isWideScreen = isWideScreen,
        )
        val listPadding = pageListPadding(contentPadding)

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = listPadding,
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            if (appState.mihomoOverrideScripts.isEmpty()) {
                item(key = "script_empty", contentType = "empty") {
                    MihomoOverrideScriptEmptyState()
                }
            } else {
                items(
                    items = appState.mihomoOverrideScripts,
                    key = { script -> script.id },
                ) { script ->
                    MihomoOverrideScriptCard(
                        script = script,
                        referenceCount = appState.mihomoProfiles.count { profile ->
                            profile.overrideScriptId == script.id
                        },
                        onEdit = { navigator.push(Route.MihomoOverrideScriptEdit(script.id)) },
                        onDuplicate = { duplicateScript(script) },
                        onDelete = { pendingDeletion = script },
                    )
                }
            }
        }
        pendingDeletion?.let { script ->
            val referenceCount = appState.mihomoProfiles.count { profile ->
                profile.overrideScriptId == script.id
            }
            SmartisanConfirmDialog(
                onDismissRequest = { pendingDeletion = null },
                title = stringResource(R.string.mihomo_override_script_delete_title),
                message = pluralStringResource(
                    R.plurals.mihomo_override_script_delete_message,
                    referenceCount,
                    script.name,
                    referenceCount,
                ),
                confirmText = stringResource(R.string.common_delete),
                dismissText = stringResource(R.string.common_cancel),
                onConfirm = { deleteScript(script) },
            )
        }
    }
}

@Composable
private fun MihomoOverrideScriptEmptyState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 56.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        SmartisanSurface(
            shape = LocalSmartisanShapes.current.medium,
            color = LocalSmartisanColors.current.surfaceRaised,
        ) {
            AsteriskIcon(
                res = Icons.Rounded.Code,
                contentDescription = null,
                modifier = Modifier.padding(18.dp).size(32.dp),
                tint = LocalSmartisanColors.current.textPrimary,
            )
        }
        SmartisanText(
            text = stringResource(R.string.common_empty),
            style = LocalSmartisanTypography.current.listItemPrimary,
            color = LocalSmartisanColors.current.textSecondary,
        )
        SmartisanText(
            text = stringResource(R.string.mihomo_override_script_empty_summary),
            style = LocalSmartisanTypography.current.body,
            color = LocalSmartisanColors.current.textSecondary,
        )
    }
}

@Composable
private fun MihomoOverrideScriptCard(
    script: MihomoOverrideScriptState,
    referenceCount: Int,
    onEdit: () -> Unit,
    onDuplicate: () -> Unit,
    onDelete: () -> Unit,
) {
    var menuExpanded by remember { mutableStateOf(false) }
    val lineCount = remember(script.content) { script.content.count { it == '\n' } + 1 }
    AsteriskExpressiveCard(
        onClick = onEdit,
        modifier = Modifier.fillMaxWidth().heightIn(min = 66.dp),
        role = ExpressiveShapeRole.ContentCard,
        containerColor = LocalSmartisanColors.current.surface,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 14.dp, end = 4.dp, top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SmartisanSurface(
                shape = LocalSmartisanShapes.current.medium,
                color = LocalSmartisanColors.current.surfaceRaised,
            ) {
                AsteriskIcon(
                    res = Icons.Rounded.Code,
                    contentDescription = null,
                    modifier = Modifier.padding(10.dp).size(22.dp),
                    tint = LocalSmartisanColors.current.textPrimary,
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                SmartisanText(
                    text = script.name,
                    style = LocalSmartisanTypography.current.listItemPrimary,
                    fontWeight = FontWeight.SemiBold,
                    color = LocalSmartisanColors.current.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                SmartisanText(
                    text = listOf(
                        pluralStringResource(
                            R.plurals.mihomo_override_script_profile_count,
                            referenceCount,
                            referenceCount,
                        ),
                        pluralStringResource(
                            R.plurals.mihomo_override_script_line_count,
                            lineCount,
                            lineCount,
                        ),
                    ).joinToString(" · "),
                    style = LocalSmartisanTypography.current.listItemSecondary,
                    color = LocalSmartisanColors.current.textSecondary,
                    maxLines = 1,
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
                        text = { SmartisanText(stringResource(R.string.common_edit)) },
                        leadingIcon = { AsteriskIcon(Icons.Rounded.Edit, contentDescription = null) },
                        onClick = {
                            menuExpanded = false
                            onEdit()
                        },
                    )
                    AsteriskPopupMenuItem(
                        text = { SmartisanText(stringResource(R.string.common_copy)) },
                        leadingIcon = { AsteriskIcon(Icons.Rounded.ContentCopy, contentDescription = null) },
                        onClick = {
                            menuExpanded = false
                            onDuplicate()
                        },
                    )
                    AsteriskPopupMenuItem(
                        text = { SmartisanText(stringResource(R.string.common_delete)) },
                        leadingIcon = { AsteriskIcon(Icons.Rounded.Delete, contentDescription = null) },
                        onClick = {
                            menuExpanded = false
                            onDelete()
                        },
                    )
                }
            }
        }
    }
}

@Composable
fun MihomoOverrideScriptEditPage(
    padding: PaddingValues,
    scriptId: Int,
) {
    val appState by LocalAppStateStore.current.collectAppState()
    val isWideScreen = LocalIsWideScreen.current
    val navigator = LocalNavigator.current
    val updateAppState = LocalUpdateAppState.current
    val services = LocalAppServices.current
    val clipboard = LocalClipboard.current
    val scope = rememberCoroutineScope()
    val isNew = scriptId <= DefaultMihomoOverrideScriptId
    val targetScript = remember(appState.mihomoOverrideScripts, scriptId) {
        appState.mihomoOverrideScripts.firstOrNull { script -> script.id == scriptId }
    }
    val title = stringResource(
        if (isNew) {
            R.string.mihomo_override_script_add
        } else {
            R.string.mihomo_override_script_edit
        },
    )
    val nameRequiredMessage = stringResource(R.string.mihomo_override_script_name_required)
    val nameState = rememberTextFieldState(initialText = targetScript?.name.orEmpty())
    val scriptEditorState = remember(targetScript?.id, isNew) {
        MihomoCodeEditorState(targetScript?.content ?: DefaultMihomoOverrideScript)
    }
    var debugRunning by remember { mutableStateOf(false) }
    var debugResult by remember { mutableStateOf<MihomoProfileScriptDebugResult?>(null) }
    var debugProfileId by remember {
        mutableIntStateOf(
            appState.mihomoProfiles.firstOrNull { profile ->
                profile.id == appState.selectedMihomoProfileId
            }?.id ?: appState.mihomoProfiles.firstOrNull()?.id ?: DefaultMihomoProfileId,
        )
    }
    val copiedMessage = stringResource(R.string.logs_copied_to_clipboard)

    fun saveScript() {
        val cleanName = nameState.text.toString().trim()
        if (cleanName.isBlank()) {
            scope.launch { services.tipNotifier.show(nameRequiredMessage) }
            return
        }
        val canSave = isNew || targetScript != null
        if (!canSave) return
        val scriptContent = scriptEditorState.snapshotText()
        updateAppState { state ->
            if (isNew) {
                val scriptId = state.nextAvailableMihomoOverrideScriptId()
                val savedScript = MihomoOverrideScriptState(
                    id = scriptId,
                    name = cleanName,
                    content = scriptContent,
                )
                state.copy(
                    mihomoOverrideScripts = state.mihomoOverrideScripts + savedScript,
                    nextMihomoOverrideScriptId = scriptId + 1,
                )
            } else {
                state.copy(
                    mihomoOverrideScripts = state.mihomoOverrideScripts.map { script ->
                        if (script.id == scriptId) {
                            script.copy(
                                name = cleanName,
                                content = scriptContent,
                            )
                        } else {
                            script
                        }
                    },
                )
            }
        }
        navigator.pop()
    }
    fun runScriptDebug() {
        if (debugRunning) return
        val profile = appState.mihomoProfiles.firstOrNull { item -> item.id == debugProfileId }
        if (profile == null) {
            debugResult = MihomoProfileScriptDebugResult(error = MihomoProfileMissingErrorMessage)
            return
        }
        if (!profile.hasContent) {
            debugResult = MihomoProfileScriptDebugResult(error = MihomoProfileEmptyErrorMessage)
            return
        }
        scope.launch {
            debugRunning = true
            debugResult = null
            val scriptContent = scriptEditorState.snapshotText()
            try {
                debugResult = withContext(Dispatchers.Default) {
                    val rawProfile = services.mihomoProfileContentStore.read(profile)
                    debugMihomoProfileScriptOverride(
                        rawProfileContent = rawProfile,
                        scriptContent = scriptContent,
                    )
                }
            } finally {
                debugRunning = false
            }
        }
    }

    AsteriskScaffold(
        topBar = {
            AsteriskTopAppBar(
                title = { SmartisanText(title, maxLines = 1) },
                navigationIcon = {
                    SmartisanIconButton(onClick = { navigator.pop() }) {
                        AsteriskIcon(
                            res = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = stringResource(R.string.common_back),
                        )
                    }
                },
                actions = {
                    AsteriskLinkButton(
                        onClick = ::runScriptDebug,
                        enabled = !debugRunning,
                    ) {
                        if (debugRunning) {
                            AsteriskSpinner(
                                size = 18.dp,
                                )
                            Spacer(Modifier.width(6.dp))
                        } else {
                            AsteriskIcon(Icons.Rounded.BugReport, contentDescription = null)
                            Spacer(Modifier.width(6.dp))
                        }
                        SmartisanText(
                            stringResource(
                                if (debugRunning) {
                                    R.string.mihomo_override_script_debug_running
                                } else {
                                    R.string.mihomo_override_script_debug_run
                                },
                            ),
                        )
                    }
                    AsteriskLinkButton(
                        onClick = ::saveScript,
                    ) {
                        AsteriskIcon(Icons.Rounded.Save, contentDescription = null)
                        Spacer(Modifier.width(6.dp))
                        SmartisanText(stringResource(R.string.common_save))
                    }
                },
            )
        },
    ) { innerPadding ->
        val contentPadding = pageContentPaddingWithCutout(
            innerPadding = innerPadding,
            outerPadding = padding,
            isWideScreen = isWideScreen,
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 12.dp),
        ) {
            if (!isNew && targetScript == null) {
                Box(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    contentAlignment = Alignment.Center,
                ) {
                    SmartisanText(
                        text = stringResource(R.string.mihomo_profile_missing),
                        color = LocalSmartisanColors.current.textSecondary,
                    )
                }
            } else {
                key(targetScript?.id, isNew) {
                    AnimatedVisibility(
                        visible = codeEditorShowsSupportingContent(scriptEditorState.isFocused),
                        enter = AsteriskMotion.contentEnter(),
                        exit = AsteriskMotion.contentExit(),
                    ) {
                        Column {
                            AsteriskStateField(
                                shape = AsteriskShapeTokens.InnerContainer,
                                state = nameState,
                                label = { SmartisanText(stringResource(R.string.mihomo_override_script_name)) },
                                lineLimits = TextFieldLineLimits.SingleLine,
                                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                            )
                            DebugProfileSelector(
                                profiles = appState.mihomoProfiles,
                                selectedProfileId = debugProfileId,
                                onSelectedProfileIdChange = { debugProfileId = it },
                                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                            )
                        }
                    }
                    JavaScriptCodeEditor(
                        state = scriptEditorState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .imePadding(),
                    )
                }
            }
        }
        MihomoOverrideScriptDebugDialog(
            result = debugResult,
            onDismissRequest = { debugResult = null },
            onCopy = { result ->
                scope.launch {
                    clipboard.setPlainText(result.toClipboardReport())
                    services.tipNotifier.show(copiedMessage)
                }
            },
        )
    }
}

@Composable
private fun DebugProfileSelector(
    profiles: List<MihomoProfileState>,
    selectedProfileId: Int,
    onSelectedProfileIdChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (profiles.isEmpty()) {
        AsteriskListRow(
            title = stringResource(R.string.mihomo_override_script_debug_profile),
            horizontalPadding = 0.dp,
            summary = stringResource(R.string.mihomo_override_script_debug_profile_none),
            leadingIcon = Icons.Rounded.Description,
            enabled = false,
            modifier = modifier,
        )
        return
    }
    val selectedIndex = profiles.indexOfFirst { profile -> profile.id == selectedProfileId }.coerceAtLeast(0)
    SettingsDropdownRow(
        title = stringResource(R.string.mihomo_override_script_debug_profile),
        horizontalPadding = 0.dp,
        icon = Icons.Rounded.Description,
        items = profiles.map(MihomoProfileState::name),
        selectedIndex = selectedIndex,
        onSelectedIndexChange = { index -> onSelectedProfileIdChange(profiles[index].id) },
        modifier = modifier,
    )
}

@Composable
private fun MihomoOverrideScriptDebugDialog(
    result: MihomoProfileScriptDebugResult?,
    onDismissRequest: () -> Unit,
    onCopy: (MihomoProfileScriptDebugResult) -> Unit,
) {
    if (result == null) return
    var showOutput by remember(result) { mutableStateOf(false) }
    val consoleText = result.logs.takeIf(List<*>::isNotEmpty)
        ?.joinToString(separator = "\n") { log -> "[${log.level}] ${log.message}" }
        ?: stringResource(R.string.mihomo_override_script_debug_no_logs)
    val outputText = result.outputYaml?.takeIf(String::isNotBlank)
        ?: stringResource(R.string.mihomo_override_script_debug_no_output)
    val outputEditorState = remember(result, outputText) {
        MihomoCodeEditorState().also { state ->
            state.replaceText(outputText, placeCursorAtEnd = false)
        }
    }
    val outputEffectsMotion = AsteriskMotion.fastEffects<Float>()

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        SmartisanCard(
            modifier = Modifier.fillMaxWidth().widthIn(max = 880.dp).padding(24.dp),
            color = LocalSmartisanColors.current.surfaceRaised,
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    SmartisanText(
                        text = stringResource(R.string.mihomo_override_script_debug_title),
                        style = LocalSmartisanTypography.current.title,
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    SmartisanSurface(
                        shape = LocalSmartisanShapes.current.small,
                        color = if (result.success) {
                            LocalSmartisanColors.current.selectionBackground
                        } else {
                            LocalSmartisanColors.current.surfaceRaised
                        },
                    ) {
                        SmartisanText(
                            text = stringResource(
                                if (result.success) {
                                    R.string.mihomo_override_script_debug_success
                                } else {
                                    R.string.mihomo_override_script_debug_failed
                                },
                            ),
                            style = LocalSmartisanTypography.current.sectionTitle,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        )
                    }
                }
                BoxWithConstraints(
                    modifier = Modifier.fillMaxWidth().height(560.dp).weight(1f, fill = false),
                ) {
                    val diagnosticsMaxHeight = maxHeight * 0.35f
                    Column(modifier = Modifier.fillMaxSize()) {
                        Column(
                            modifier = Modifier.fillMaxWidth()
                                .heightIn(max = diagnosticsMaxHeight)
                                .verticalScroll(rememberScrollState()),
                        ) {
                            result.error?.takeIf(String::isNotBlank)?.let { error ->
                                DebugSection(
                                    title = stringResource(R.string.mihomo_override_script_debug_error),
                                    body = error,
                                    error = true,
                                    modifier = Modifier.padding(top = 14.dp),
                                )
                            }
                            result.summary?.let { summary ->
                                DebugSection(
                                    title = stringResource(R.string.mihomo_override_script_debug_summary_title),
                                    body = stringResource(
                                        R.string.mihomo_override_script_debug_summary,
                                        summary.inputProxyCount,
                                        summary.outputProxyCount,
                                        summary.inputProxyGroupCount,
                                        summary.outputProxyGroupCount,
                                        summary.inputRuleCount,
                                        summary.outputRuleCount,
                                    ),
                                    modifier = Modifier.padding(top = 10.dp),
                                )
                            }
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            AsteriskFilterChip(
                                selected = !showOutput,
                                onClick = { showOutput = false },
                                label = stringResource(R.string.mihomo_override_script_debug_logs),
                                leadingIcon = { AsteriskIcon(Icons.Rounded.Code, contentDescription = null) },
                            )
                            AsteriskFilterChip(
                                selected = showOutput,
                                onClick = { showOutput = true },
                                label = stringResource(R.string.mihomo_override_script_debug_output),
                                leadingIcon = { AsteriskIcon(Icons.Rounded.Description, contentDescription = null) },
                            )
                        }
                        AnimatedContent(
                            targetState = showOutput,
                            modifier = Modifier.fillMaxWidth().weight(1f).padding(top = 8.dp),
                            transitionSpec = AsteriskMotion.fadeThrough(outputEffectsMotion),
                            contentAlignment = Alignment.TopStart,
                            label = "script-debug-output",
                        ) { showingOutput ->
                            if (showingOutput) {
                                YamlCodeEditor(
                                    state = outputEditorState,
                                    readOnly = true,
                                    modifier = Modifier.fillMaxSize(),
                                )
                            } else {
                                SmartisanSurface(
                                    modifier = Modifier.fillMaxSize(),
                                    shape = LocalSmartisanShapes.current.large,
                                    color = LocalSmartisanColors.current.surfaceRaised,
                                ) {
                                    SelectionContainer {
                                        SmartisanText(
                                            text = consoleText,
                                            style = LocalSmartisanTypography.current.listItemSecondary.copy(fontFamily = FontFamily.Monospace),
                                            modifier = Modifier.verticalScroll(rememberScrollState()).padding(16.dp),
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
                ) {
                    AsteriskActionButton(
                        text = stringResource(R.string.mihomo_override_script_debug_copy),
                        icon = Icons.Rounded.ContentCopy,
                        onClick = { onCopy(result) },
                    )
                    AsteriskActionButton(
                        text = stringResource(R.string.common_complete),
                        icon = Icons.Rounded.Check,
                        onClick = onDismissRequest,
                    )
                }
            }
        }
    }
}

@Composable
private fun DebugSection(
    title: String,
    body: String,
    modifier: Modifier = Modifier,
    error: Boolean = false,
) {
    SmartisanSurface(
        modifier = modifier.fillMaxWidth(),
        shape = LocalSmartisanShapes.current.large,
        color = if (error) {
            LocalSmartisanColors.current.surfaceRaised
        } else {
            LocalSmartisanColors.current.surface
        },
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            SmartisanText(
                text = title,
                style = LocalSmartisanTypography.current.sectionTitle,
                color = if (error) {
                    LocalSmartisanColors.current.warning
                } else {
                    LocalSmartisanColors.current.accent
                },
            )
            SmartisanText(
                text = body,
                style = LocalSmartisanTypography.current.listItemSecondary.copy(fontFamily = FontFamily.Monospace),
                color = if (error) {
                    LocalSmartisanColors.current.warning
                } else {
                    LocalSmartisanColors.current.textPrimary
                },
                modifier = Modifier.padding(top = 4.dp),
            )
        }
    }
}

private fun MihomoProfileScriptDebugResult.toClipboardReport(): String {
    return buildString {
        appendLine(if (success) "Script debug: success" else "Script debug: failed")
        error?.takeIf(String::isNotBlank)?.let { error ->
            appendLine()
            appendLine("Error:")
            appendLine(error)
        }
        summary?.let { summary ->
            appendLine()
            appendLine("Summary:")
            appendLine("proxies: ${summary.inputProxyCount} -> ${summary.outputProxyCount}")
            appendLine("proxy-groups: ${summary.inputProxyGroupCount} -> ${summary.outputProxyGroupCount}")
            appendLine("rules: ${summary.inputRuleCount} -> ${summary.outputRuleCount}")
        }
        appendLine()
        appendLine("Console:")
        if (logs.isEmpty()) {
            appendLine("(empty)")
        } else {
            logs.forEach { log -> appendLine("[${log.level}] ${log.message}") }
        }
        outputYaml?.takeIf(String::isNotBlank)?.let { output ->
            appendLine()
            appendLine("Output YAML:")
            appendLine(output)
        }
    }
}
