package com.apphub.launcher.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Win11 调色板（精确对应原型里的 CSS 变量）
object Win11Palette {
    val AccentLight = Color(0xFF0067C0)           // Windows 蓝
    val AccentHoverLight = Color(0xFF1976D2)
    val AccentPressedLight = Color(0xFF0D47A1)
    val AccentSoftLight = Color(0x1F0067C0)
    val DangerLight = Color(0xFFC42B1C)
    val DangerSoftLight = Color(0x1AC42B1C)

    val BgLight = Color(0xFFF3F3F3)
    val CardLight = Color(0xFFFCFCFC)
    val CardHoverLight = Color(0xFFF5F5F5)
    val DividerLight = Color(0x1A000000)
    val BorderLight = Color(0x12000000)
    val BorderStrongLight = Color(0x24000000)
    val TextLight = Color(0xFF1A1A1A)
    val TextDimLight = Color(0xFF5C5C5C)
    val TextMuteLight = Color(0xFF8A8A8A)

    val AccentDark = Color(0xFF60CDFF)
    val AccentHoverDark = Color(0xFF7ED6FF)
    val AccentPressedDark = Color(0xFF40BAFF)
    val AccentSoftDark = Color(0x2560CDFF)
    val DangerDark = Color(0xFFFF8A80)
    val DangerSoftDark = Color(0x22FF8A80)

    val BgDark = Color(0xFF202020)
    val CardDark = Color(0xFF2A2A2A)
    val CardHoverDark = Color(0xFF333333)
    val DividerDark = Color(0x1FFFFFFF)
    val BorderDark = Color(0x12FFFFFF)
    val BorderStrongDark = Color(0x26FFFFFF)
    val TextDark = Color(0xFFF5F5F5)
    val TextDimDark = Color(0xFFC0C0C0)
    val TextMuteDark = Color(0xFF9E9E9E)
}

data class Win11Tokens(
    val background: Color,
    val card: Color,
    val cardHover: Color,
    val accent: Color,
    val accentHover: Color,
    val accentText: Color,      // accent 底色上的字（亮蓝底用白字，暗青蓝底用黑字——Win11 规范）
    val accentSoft: Color,
    val divider: Color,
    val border: Color,
    val borderStrong: Color,
    val text: Color,
    val textDim: Color,
    val textMute: Color,
    val danger: Color,
    val dangerSoft: Color,
    val tilePressed: Color,
)

val LightTokens = Win11Tokens(
    background = Win11Palette.BgLight,
    card = Win11Palette.CardLight,
    cardHover = Win11Palette.CardHoverLight,
    accent = Win11Palette.AccentLight,
    accentHover = Win11Palette.AccentHoverLight,
    accentText = Color.White,
    accentSoft = Win11Palette.AccentSoftLight,
    divider = Win11Palette.DividerLight,
    border = Win11Palette.BorderLight,
    borderStrong = Win11Palette.BorderStrongLight,
    text = Win11Palette.TextLight,
    textDim = Win11Palette.TextDimLight,
    textMute = Win11Palette.TextMuteLight,
    danger = Win11Palette.DangerLight,
    dangerSoft = Win11Palette.DangerSoftLight,
    tilePressed = Color(0x14000000),
)

val DarkTokens = Win11Tokens(
    background = Win11Palette.BgDark,
    card = Win11Palette.CardDark,
    cardHover = Win11Palette.CardHoverDark,
    accent = Win11Palette.AccentDark,
    accentHover = Win11Palette.AccentHoverDark,
    accentText = Color.Black,
    accentSoft = Win11Palette.AccentSoftDark,
    divider = Win11Palette.DividerDark,
    border = Win11Palette.BorderDark,
    borderStrong = Win11Palette.BorderStrongDark,
    text = Win11Palette.TextDark,
    textDim = Win11Palette.TextDimDark,
    textMute = Win11Palette.TextMuteDark,
    danger = Win11Palette.DangerDark,
    dangerSoft = Win11Palette.DangerSoftDark,
    tilePressed = Color(0x18FFFFFF),
)

val LocalWin11 = androidx.compose.runtime.staticCompositionLocalOf<Win11Tokens> {
    error("Win11 tokens not provided")
}

@Composable
fun AppHubTheme(
    dark: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val tokens = if (dark) DarkTokens else LightTokens
    val scheme = if (dark) {
        darkColorScheme(
            primary = tokens.accent,
            onPrimary = tokens.accentText,
            surface = tokens.card,
            onSurface = tokens.text,
            background = tokens.background,
            onBackground = tokens.text,
            outline = tokens.borderStrong,
            surfaceVariant = tokens.cardHover,
            error = tokens.danger,
        )
    } else {
        lightColorScheme(
            primary = tokens.accent,
            onPrimary = tokens.accentText,
            surface = tokens.card,
            onSurface = tokens.text,
            background = tokens.background,
            onBackground = tokens.text,
            outline = tokens.borderStrong,
            surfaceVariant = tokens.cardHover,
            error = tokens.danger,
        )
    }
    androidx.compose.runtime.CompositionLocalProvider(LocalWin11 provides tokens) {
        MaterialTheme(colorScheme = scheme, content = content)
    }
}
