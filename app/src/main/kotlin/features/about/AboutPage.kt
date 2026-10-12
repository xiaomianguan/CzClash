// Copyright 2026, AsteriskMETA contributors
// SPDX-License-Identifier: GPL-3.0

package features.about

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import app.LocalIsWideScreen
import app.LocalNavigator
import app.ProjectInfo
import app.R
import cc.wuersan008.smartisanx.ui.basic.SmartisanIcon
import cc.wuersan008.smartisanx.ui.basic.SmartisanIconButton
import cc.wuersan008.smartisanx.ui.basic.SmartisanText
import cc.wuersan008.smartisanx.ui.layout.SmartisanAboutStaticItem
import cc.wuersan008.smartisanx.ui.layout.SmartisanGroup
import cc.wuersan008.smartisanx.ui.layout.SmartisanGroupItem
import cc.wuersan008.smartisanx.ui.layout.SmartisanGroupRowPosition
import cc.wuersan008.smartisanx.ui.layout.SmartisanListVerticalGap
import cc.wuersan008.smartisanx.ui.asset.SmartisanDrawables
import ui.components.AsteriskIcon
import ui.components.AsteriskScaffold
import ui.components.AsteriskTopAppBar
import ui.icons.AsteriskIcons as Icons
import ui.layout.pageContentPaddingWithCutout
import ui.layout.pageListPadding

/**
 * 「关于」页，版式照抄 smartisanx 示例应用的关于页（原版 `about_settings_layout`）：
 * 顶部 logo 卡片 + 可点设置行 + 只读信息行。
 */
@Composable
fun AboutPage(
    padding: PaddingValues,
) {
    val isWideScreen = LocalIsWideScreen.current
    val navigator = LocalNavigator.current
    val uriHandler = LocalUriHandler.current

    AsteriskScaffold(
        topBar = {
            AsteriskTopAppBar(
                title = { SmartisanText(stringResource(R.string.about_title)) },
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
    ) { innerPadding ->
        val contentPadding = pageListPadding(
            pageContentPaddingWithCutout(
                innerPadding = innerPadding,
                outerPadding = padding,
                isWideScreen = isWideScreen,
            ),
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(contentPadding),
        ) {
            AboutLogoCard()

            SmartisanListVerticalGap()
            SmartisanGroup {
                SmartisanGroupItem(
                    position = SmartisanGroupRowPosition.Top,
                    title = stringResource(R.string.about_view_source),
                    trailing = { AboutRowArrow() },
                    onClick = { uriHandler.openUri(ProjectSourceUri) },
                )
                SmartisanGroupItem(
                    position = SmartisanGroupRowPosition.Bottom,
                    title = stringResource(R.string.about_join_telegram),
                    trailing = { AboutRowArrow() },
                    onClick = { uriHandler.openUri(TelegramChannelUri) },
                )
            }
            SmartisanListVerticalGap()

            SmartisanAboutStaticItem(
                title = "AndroidLibClashLite",
                summary = ProjectInfo.ANDROID_LIB_CLASH_LITE_VERSION,
            )
            SmartisanAboutStaticItem(
                title = "hev-socks5-tunnel",
                summary = ProjectInfo.HEV_SOCKS5_TUNNEL_VERSION,
            )
            SmartisanListVerticalGap()
        }
    }
}

@Composable
private fun AboutRowArrow() {
    SmartisanIcon(
        res = SmartisanDrawables.ListItemArrow,
        contentDescription = null,
    )
}

private const val ProjectSourceUri = "https://github.com/Asterisk4Magisk/AsteriskMETA"
private const val TelegramChannelUri = "https://t.me/Asterisk4Magisk"
