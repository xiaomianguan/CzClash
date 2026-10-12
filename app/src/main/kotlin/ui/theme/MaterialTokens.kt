package ui.theme

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

internal object AsteriskShapeTokens {
    // Smartisan is a square, right-angled design language: cards, rows and chips keep
    // only a hairline rounding.
    val ExtraSmallContainerRadius = 2.dp
    val SmallContainerRadius = 4.dp
    val InnerContainerRadius = 4.dp
    val ListCardRadius = 2.dp
    val PageCardRadius = 2.dp
    val SheetRadius = 10.dp
    val HeroContainerRadius = 8.dp

    val ExtraSmallContainer = RoundedCornerShape(ExtraSmallContainerRadius)
    val SmallContainer = RoundedCornerShape(SmallContainerRadius)
    val InnerContainer = RoundedCornerShape(InnerContainerRadius)
    val ListCard = RoundedCornerShape(ListCardRadius)
    val PageCard = RoundedCornerShape(PageCardRadius)
    val HeroContainer = RoundedCornerShape(HeroContainerRadius)
    val Sheet = RoundedCornerShape(
        topStart = SheetRadius,
        topEnd = SheetRadius,
    )
    val Pill = CircleShape
}

@Immutable
internal data class AsteriskSpacing(
    val extraSmall: Dp = 4.dp,
    val small: Dp = 8.dp,
    val medium: Dp = 12.dp,
    val large: Dp = 16.dp,
    val largePlus: Dp = 20.dp,
    val extraLarge: Dp = 24.dp,
    val huge: Dp = 32.dp,
)

internal val LocalSpacing = staticCompositionLocalOf { AsteriskSpacing() }
