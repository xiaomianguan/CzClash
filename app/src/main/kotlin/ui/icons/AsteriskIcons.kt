// Copyright 2026, AsteriskMETA contributors
// SPDX-License-Identifier: GPL-3.0

package ui.icons

import androidx.annotation.DrawableRes
import cc.wuersan008.smartisanx.ui.asset.SmartisanOriginalIcons
import cc.wuersan008.smartisanx.ui.R as SmartisanR

/**
 * Central icon set backed by the original Smartisan (smartisanx) drawables.
 *
 * The accessor paths (`Rounded.*`, `AutoMirrored.Rounded.*`) are kept from the previous
 * Material icon object so call sites do not change; each entry now resolves to a
 * `@DrawableRes` original asset whose selector carries its own pressed/disabled states.
 *
 * Where the ROM had no direct equivalent, the closest original glyph is used.
 */
internal object AsteriskIcons {
    object Rounded {
        @DrawableRes val Add = SmartisanR.drawable.standard_icon_common_add_selector
        @DrawableRes val Remove = SmartisanR.drawable.clear_text_selector
        @DrawableRes val Close = SmartisanR.drawable.standard_icon_cancel_selector
        @DrawableRes val Clear = SmartisanR.drawable.button_clear_text
        @DrawableRes val Check = SmartisanR.drawable.standard_icon_complete_selector
        @DrawableRes val CheckCircle = SmartisanR.drawable.standard_icon_complete_selector
        @DrawableRes val Delete = SmartisanR.drawable.standard_icon_hignlight_delete_selector
        @DrawableRes val Edit = SmartisanR.drawable.setting_item_icon_pencil
        @DrawableRes val EditNote = SmartisanR.drawable.setting_item_icon_pencil
        @DrawableRes val MoreVert = SmartisanR.drawable.btn_more_selector
        @DrawableRes val Search = SmartisanR.drawable.search_btn_selector
        @DrawableRes val Refresh = SmartisanR.drawable.tool_refresh_selector
        @DrawableRes val Settings = SmartisanR.drawable.standard_icon_settings_selector
        @DrawableRes val Tune = SmartisanR.drawable.setting_item_icon_pencil
        @DrawableRes val SortByAlpha = SmartisanR.drawable.secletor_setting_item_icon_shortcut
        @DrawableRes val FilterAlt = SmartisanR.drawable.selector_setting_item_navbar
        @DrawableRes val ContentCopy = SmartisanR.drawable.copy_icon
        @DrawableRes val Save = SmartisanR.drawable.commit_edit_done
        @DrawableRes val ExpandMore = SmartisanR.drawable.arrow_down_icon
        @DrawableRes val ChevronLeft = SmartisanR.drawable.next_page_arrow
        @DrawableRes val ChevronRight = SmartisanR.drawable.list_arrow_right
        @DrawableRes val Warning = SmartisanR.drawable.secletor_setting_item_icon_about
        @DrawableRes val Info = SmartisanR.drawable.secletor_setting_item_icon_about
        @DrawableRes val Lock = SmartisanR.drawable.secletor_setting_item_icon_keyguard
        @DrawableRes val Notifications = SmartisanR.drawable.secletor_setting_item_icon_notification
        @DrawableRes val Language = SmartisanR.drawable.secletor_setting_item_icon_language
        @DrawableRes val History = SmartisanR.drawable.secletor_setting_item_icon_brightness
        @DrawableRes val Home = SmartisanR.drawable.secletor_setting_item_icon_launcher
        @DrawableRes val HomeWork = SmartisanR.drawable.secletor_setting_item_icon_launcher
        @DrawableRes val Description = SmartisanR.drawable.secletor_setting_item_icon_application
        @DrawableRes val Article = SmartisanR.drawable.secletor_setting_item_icon_application
        @DrawableRes val Folder = SmartisanR.drawable.secletor_setting_item_icon_application
        @DrawableRes val FolderOpen = SmartisanR.drawable.secletor_setting_item_icon_application
        @DrawableRes val FileDownload = SmartisanR.drawable.secletor_setting_item_icon_shortcut
        @DrawableRes val FileUpload = SmartisanR.drawable.secletor_setting_item_icon_shortcut
        @DrawableRes val Download = SmartisanR.drawable.secletor_setting_item_icon_shortcut
        @DrawableRes val Upload = SmartisanR.drawable.secletor_setting_item_icon_shortcut
        @DrawableRes val Sync = SmartisanR.drawable.tool_refresh_selector
        @DrawableRes val CloudSync = SmartisanR.drawable.secletor_setting_item_icon_shortcut
        @DrawableRes val Block = SmartisanR.drawable.secletor_setting_item_icon_keyguard
        @DrawableRes val Bluetooth = SmartisanR.drawable.icon_nav_bluetooth
        @DrawableRes val Wifi = SmartisanR.drawable.secletor_setting_item_icon_application
        @DrawableRes val CellTower = SmartisanR.drawable.secletor_setting_item_icon_application
        @DrawableRes val Dns = SmartisanR.drawable.secletor_setting_item_icon_application
        @DrawableRes val Http = SmartisanR.drawable.secletor_setting_item_icon_application
        @DrawableRes val Lan = SmartisanR.drawable.secletor_setting_item_icon_application
        @DrawableRes val Router = SmartisanR.drawable.secletor_setting_item_icon_application
        @DrawableRes val Route = SmartisanR.drawable.secletor_setting_item_icon_application
        @DrawableRes val SettingsEthernet = SmartisanR.drawable.secletor_setting_item_icon_application
        @DrawableRes val SettingsInputComponent = SmartisanR.drawable.secletor_setting_item_icon_application
        @DrawableRes val Cable = SmartisanR.drawable.secletor_setting_item_icon_application
        @DrawableRes val Usb = SmartisanR.drawable.secletor_setting_item_icon_application
        @DrawableRes val AccountTree = SmartisanR.drawable.secletor_setting_item_icon_application
        @DrawableRes val Hub = SmartisanR.drawable.secletor_setting_item_icon_application
        @DrawableRes val Memory = SmartisanR.drawable.secletor_setting_item_icon_brightness
        @DrawableRes val Storage = SmartisanR.drawable.secletor_setting_item_icon_brightness
        @DrawableRes val Speed = SmartisanR.drawable.secletor_setting_item_icon_brightness
        @DrawableRes val Terminal = SmartisanR.drawable.secletor_setting_item_icon_application
        @DrawableRes val Code = SmartisanR.drawable.secletor_setting_item_icon_application
        @DrawableRes val DataObject = SmartisanR.drawable.secletor_setting_item_icon_application
        @DrawableRes val DataUsage = SmartisanR.drawable.secletor_setting_item_icon_shortcut
        @DrawableRes val Policy = SmartisanR.drawable.secletor_setting_item_icon_keyguard
        @DrawableRes val Security = SmartisanR.drawable.secletor_setting_item_icon_keyguard
        @DrawableRes val VpnLock = SmartisanR.drawable.secletor_setting_item_icon_keyguard
        @DrawableRes val Public = SmartisanR.drawable.selector_setting_item_tnt_assist
        @DrawableRes val TravelExplore = SmartisanR.drawable.selector_setting_item_tnt_assist
        @DrawableRes val MyLocation = SmartisanR.drawable.selector_setting_item_tnt_assist
        @DrawableRes val QrCodeScanner = SmartisanR.drawable.secletor_setting_item_icon_application
        @DrawableRes val Apps = SmartisanR.drawable.secletor_setting_item_icon_launcher
        @DrawableRes val Widgets = SmartisanR.drawable.secletor_setting_item_icon_launcher
        @DrawableRes val GridView = SmartisanR.drawable.selector_setting_item_navbar
        @DrawableRes val ViewAgenda = SmartisanR.drawable.selector_setting_item_navbar
        @DrawableRes val ViewColumn = SmartisanR.drawable.selector_setting_item_navbar
        @DrawableRes val ViewModule = SmartisanR.drawable.selector_setting_item_navbar
        @DrawableRes val Visibility = SmartisanR.drawable.secletor_setting_item_icon_about
        @DrawableRes val PowerSettingsNew = SmartisanR.drawable.secletor_setting_item_icon_keyguard
        @DrawableRes val PlayArrow = SmartisanOriginalIcons.Play
        @DrawableRes val Pause = SmartisanOriginalIcons.PausePlain
        @DrawableRes val AutoAwesome = SmartisanR.drawable.setting_item_icon_pencil
        @DrawableRes val AutoFixHigh = SmartisanR.drawable.setting_item_icon_pencil
        @DrawableRes val Link = SmartisanR.drawable.cloud_sync_on
        @DrawableRes val LinkOff = SmartisanR.drawable.secletor_setting_item_icon_keyguard
        @DrawableRes val BugReport = SmartisanR.drawable.secletor_setting_item_icon_about
    }

    object AutoMirrored {
        object Rounded {
            @DrawableRes val ArrowBack = SmartisanR.drawable.standard_icon_back_selector
            @DrawableRes val AltRoute = SmartisanR.drawable.secletor_setting_item_icon_application
            @DrawableRes val Article = SmartisanR.drawable.secletor_setting_item_icon_application
            @DrawableRes val CompareArrows = SmartisanR.drawable.cloud_sync_on
            @DrawableRes val Help = SmartisanR.drawable.setting_item_icon_about
            @DrawableRes val KeyboardArrowRight = SmartisanR.drawable.list_arrow_right
            @DrawableRes val OpenInNew = SmartisanR.drawable.next_page_arrow
            @DrawableRes val Send = SmartisanR.drawable.ic_cloud_uploading_01
            @DrawableRes val Sort = SmartisanR.drawable.icon_sort_normal
        }
    }

    /** Bottom tab-bar icons: monochrome original glyphs (selection shown by darker tint). */
    object Tab {
        @DrawableRes val Home = SmartisanR.drawable.secletor_setting_item_icon_launcher
        @DrawableRes val Proxies = SmartisanR.drawable.nav_bottom_bar_icon_sync_selector
        @DrawableRes val Profiles = SmartisanR.drawable.nav_bottom_bar_icon_add_selector
        @DrawableRes val Settings = SmartisanR.drawable.nav_bottom_bar_icon_settings_selector
    }
}
