package com.sashya.krushisetu.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val ColorSand = Color(0xFFF7E9D3)
private val ColorMint = Color(0xFF9ED9A7)

private val LightColorScheme = lightColorScheme(
    primary = LeafGreen,
    onPrimary = FieldCream,
    primaryContainer = LightLeafGreen,
    onPrimaryContainer = DeepText,
    secondary = SoilBrown,
    onSecondary = FieldCream,
    secondaryContainer = ColorSand,
    onSecondaryContainer = DeepText,
    tertiary = AlertOrange,
    onTertiary = FieldCream,
    background = FieldCream,
    onBackground = DeepText,
    surface = FieldCream,
    onSurface = DeepText,
    surfaceVariant = SoftSurface,
    onSurfaceVariant = MutedText,
    error = ErrorRed
)

private val DarkColorScheme = darkColorScheme(
    primary = ColorMint,
    onPrimary = DeepText,
    primaryContainer = LeafGreen,
    onPrimaryContainer = FieldCream,
    background = DeepText,
    onBackground = FieldCream,
    surface = Color(0xFF1E3023),
    onSurface = FieldCream
)

@Composable
fun KrushisetuTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography,
        content = content
    )
}
