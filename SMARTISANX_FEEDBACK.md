# smartisanx feedback — what's missing for a real app migration

Context: I ported a full Android app (a Mihomo/Clash client, ~200 Compose files) from
Material 3 to smartisanx. The library is a great start, but a lot of it had to be
re-implemented in the app because there is no library component for it. Below is
everything I had to fork/hand-roll, plus the smaller API gaps. If the library shipped
these, this migration would have been "replace imports" instead of weeks of work.

Ordered by how much work it cost.


## 1. A Material-free `Scaffold` with the `PaddingValues` contract

`SmartisanScaffold` takes `titleBar` / `bottomBar` slots and gives the content **no
padding**. Every existing app is built around `Scaffold { innerPadding -> ... }` and
threads that padding into list content padding. Because of this, I had to fork the whole
page skeleton.

Needed: a scaffold that
- accepts `topBar` / `bottomBar` composables **and** hands the content a `PaddingValues`
  (top = bar height, bottom = bottom bar height / nav inset);
- keeps the existing "content draws under the bars, insets added at the end" behaviour;
- ideally a `snackbarHost` slot.

## 2. A compact title-bar text action button (the blue "Add")

The resource is already in the library (`blue_button.xml` →
`button_blue.9.png`/`button_blue_down`, and `selector_title_button_ok_blue_background` →
`blue_background_button.9.png`), but **no Kotlin component uses it**. `SmartisanTitleBar`
only accepts icon actions, so there is no way to put a text action like the system
"Add" button in a title bar.

Also: `SmartisanTitleBar` reserves a fixed 72dp on each side for the centred title, so a
wide text action overlaps the title. The title should auto-shrink/ellipsise against the
actual action widths.

Needed: `SmartisanTitleBarTextAction(text, onClick, enabled)` using the existing blue
selector, and measure-based title/action collision handling.

## 3. An anchored popup / dropdown menu

The only menu is `SmartisanMenuDialog` (a bottom sheet). Material's `DropdownMenu`
(anchored to its trigger) has no equivalent, so dropdowns either became bottom sheets or
kept Material. Used ~10 times.

Needed: an anchored `DropdownMenu`-equivalent (popup at the anchor, original
`popup_win_*` / `menu_popup` assets already ship with the library).

## 4. Text fields for the new `TextFieldState` API

`SmartisanClearableField` / `SmartisanEditorRow` only accept `value` + `onValueChange`.
Modern Compose text fields use `TextFieldState` + `InputTransformation` +
`TextFieldLineLimits`. There is no way to use those APIs with a Smartisan-styled field,
so I had to build a `BasicTextField(state = ...)` decoration by hand.

Needed: state-based variants of the field components (or make the existing ones accept a
`TextFieldState`).

## 5. Pull-to-refresh

Nothing. I hand-rolled a nested-scroll pull-to-refresh with `SmartisanCircleProgress`.

Needed: a Smartisan-styled pull-to-refresh container.

## 6. A curated, semantic icon index (this is the single biggest gap)

`SmartisanIconSet` exposes 3617 entries by **original resource name**
(`secletor_setting_item_icon_launcher`, `tabbar_song_selector`, …). There is no semantic
mapping ("settings", "wifi", "dns", "vpn", "search"), so for every app icon I had to grep
the asset dump and guess. `SmartisanOriginalIcons` is small and music/clock-centric.
Worse: many of these assets are **pre-coloured** (the `tabbar_*_selector` are red, some
framework glyphs are blue/orange), so "use the original assets" fights any non-red accent.

Needed:
- A semantic index, e.g. `SmartisanIcons.settings / .wifi / .dns / .search / .back / …`
  pointing at the best original asset;
- **monochrome** variants (or a guarantee which assets are tintable) so they can follow the
  theme accent / text colour;
- a documented "settings list-row icon group" (the `listRow` group / `setting_item_icon_*`
  that your sample home uses) surfaced as a public, named set — right now you have to read
  `SamplePage.kt` to discover it.

## 7. The tab bar is accent-tinted and red-iconed

`SmartisanBottomBar` tints the selected label with `colors.accent`, and its intended icons
(`tabbar_*_selector`) are **red** in the asset. Smartisan system tab bars (Contacts, Call
log) are **monochrome with the active item darker**. There is no way to get that without
forking the component.

Needed: a monochrome variant (`selectedColor = textPrimary`, `unselectedColor =
textTertiary`, no accent) and monochrome tab glyphs; optionally a trailing/central action
slot.

## 8. Settings page primitives didn't compose

Building a settings page meant mixing `SmartisanGroup` + `SmartisanGroupItem` (for the
card texture) + `SmartisanSwitch` (no leading icon slot) + `SmartisanListItem`
(transparent background option not obvious). Every combination produced different
paddings, which is exactly what a settings page must not do.

Needed: one row family with identical geometry that all share:
- leading icon slot, title, summary, trailing value;
- variants: plain row, switch row, checkbox row, radio row, dropdown row, editor row —
  all with the **same** left slot (60dp) and vertical rhythm, and a mode to draw the row
  transparent inside a `SmartisanGroup` card.

## 9. Grouped lists are manual

`SmartisanGroupItem` requires the caller to compute `Top/Middle/Bottom/Single` for every
row, and there is no `LazyColumn` helper, so building a grouped list by hand is
error-prone. A helper like `SmartisanGroupedColumn { group { item { … } } }` (or an
automatic position resolver) would remove a lot of boilerplate.

## 10. Theme tokens

- Section headers: I had to guess that they are grey. Please document/expose the exact
  semantic (`textTertiary`?) and spacing.
- There is a default **red** accent and a separate blue. The "Smartisan blue" exists only
  as scattered resource values (`#5c89f2` / `#3d70ef` button pair, `#5079ff` progress,
  `#4285f4` chips). Expose these as named `SmartisanColors` fields (e.g.
  `accentBlue`, `accentBluePressed`, `linkBlue`) so apps don't sample them by hand.
- Dialogs use an always-light background (no night variant), so dark mode is broken for
  any dialog with custom content. Either ship a night dialog background or document the
  limitation loudly.

## 11. Smaller things

- `SmartisanIcon(imageVector = …)` accepts `tint`; the bitmap overload does **not**. There
  is no tinting story for bitmap icons (needed for "darker when selected").
- No linear *indeterminate* progress bar.
- No `Surface`/`Card` with `onClick` + elevation/tonal colour (Material `Surface { onClick }`
  / `Card { onClick }`).
- No tablet/`NavigationRail` navigation; the sample assumes phone bottom bars.
- No snackbar host/state pattern (there is `SmartisanSnackbar`, but no documented host).
- `SmartisanScaffold` doesn't expose the bottom content inset when a `bottomBar` is
  present, and it's easy to end up with the nav-bar inset double-counted when the app also
  handles window insets.

## 12. Sample app

Please add a **full settings page** sample (title bar + grouped rows with leading icons +
switch rows + a text action) that is the canonical recipe. The home sample is close, but a
settings page is the shape 90% of apps need, and it would have prevented most of the
padding/geometry guessing above.
