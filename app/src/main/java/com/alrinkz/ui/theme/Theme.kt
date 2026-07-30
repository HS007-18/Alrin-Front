package com.alrinkz.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Premium Luxury Cyber Dark Palette
val ObsidianBackground = Color(0xFF07080D)
val CardBackground = Color(0xFF10121D)
val AccentNeonEmerald = Color(0xFF0DF2A5)
val AccentCyberGold = Color(0xFFE2B755)
val AccentCoolBlue = Color(0xFF3B82F6)

val TextPrimary = Color(0xFFF1F5F9)
val TextSecondary = Color(0xFF94A3B8)
val MutedBorder = Color(0xFF1E293B)

private val DarkColorScheme = darkColorScheme(
    primary = AccentNeonEmerald,
    onPrimary = Color(0xFF021B10),
    primaryContainer = Color(0xFF102A20),
    onPrimaryContainer = AccentNeonEmerald,
    secondary = AccentCyberGold,
    onSecondary = Color(0xFF231802),
    secondaryContainer = Color(0xFF38290B),
    onSecondaryContainer = AccentCyberGold,
    tertiary = AccentCoolBlue,
    background = ObsidianBackground,
    onBackground = TextPrimary,
    surface = CardBackground,
    onSurface = TextPrimary,
    surfaceVariant = Color(0xFF1A1D2B),
    onSurfaceVariant = TextSecondary,
    outline = MutedBorder
)

@Composable
fun AlrinKZTheme(
    content: @Composable () -> Unit
) {
    // Force the custom premium luxury dark theme as Alrin is a dark-first terminal operating system!
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content
    )
}