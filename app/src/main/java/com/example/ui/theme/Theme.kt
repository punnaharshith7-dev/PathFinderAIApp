package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val PathfinderDarkColorScheme = darkColorScheme(
    primary = PrimaryPurple,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF2D1B69),
    onPrimaryContainer = Color(0xFFEDE9FE),
    secondary = ElectricBlue,
    onSecondary = Color(0xFF082F49),
    secondaryContainer = Color(0xFF163B5C),
    onSecondaryContainer = Color(0xFFE0F2FE),
    tertiary = CyanAccent,
    onTertiary = Color(0xFF083344),
    tertiaryContainer = Color(0xFF154E63),
    onTertiaryContainer = Color(0xFFCFFAFE),
    background = NavyBackground,
    onBackground = DarkPrimaryText,
    surface = NavySecondaryBackground,
    onSurface = DarkPrimaryText,
    surfaceVariant = NavyCardBackground,
    onSurfaceVariant = DarkSecondaryText,
    outline = NavyBorderGlow,
    error = ErrorRose,
    onError = Color.White
)

private val PathfinderLightColorScheme = lightColorScheme(
    primary = LightPrimaryPurple,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFEDE9FE),
    onPrimaryContainer = Color(0xFF3B0764),
    secondary = LightElectricBlue,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE0F2FE),
    onSecondaryContainer = Color(0xFF0C4A6E),
    tertiary = LightCyanAccent,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFCFFAFE),
    onTertiaryContainer = Color(0xFF164E63),
    background = LightBackground,
    onBackground = LightPrimaryText,
    surface = LightCardBackground,
    onSurface = LightPrimaryText,
    surfaceVariant = LightSecondaryBackground,
    onSurfaceVariant = LightSecondaryText,
    outline = LightBorderSubtle,
    error = Color(0xFFE11D48),
    onError = Color.White
)

val PathfinderShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(24.dp)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) PathfinderDarkColorScheme else PathfinderLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = PathfinderShapes,
        content = content
    )
}
