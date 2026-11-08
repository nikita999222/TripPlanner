package com.example.tripplanner.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/*
 * Палитра Trip Planner: океанский синий + коралловый акцент на тёплом «песочном» фоне.
 */
private val LightColors = lightColorScheme(
    primary = Color(0xFF0B6E8A),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFD2EEF7),
    onPrimaryContainer = Color(0xFF062A36),
    secondary = Color(0xFFC2410C),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFFFDDD0),
    onSecondaryContainer = Color(0xFF3B1000),
    tertiary = Color(0xFF2F7A47),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFD3EFD9),
    onTertiaryContainer = Color(0xFF00210D),
    background = Color(0xFFF7F3EA),
    onBackground = Color(0xFF161C1F),
    surface = Color(0xFFF7F3EA),
    onSurface = Color(0xFF161C1F),
    surfaceVariant = Color(0xFFE8E4D9),
    onSurfaceVariant = Color(0xFF475055),
    outline = Color(0xFF788288),
    outlineVariant = Color(0xFFDBD6C8),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFFFFFFF),
    surfaceContainer = Color(0xFFFCF9F2),
    surfaceContainerHigh = Color(0xFFEFEBE0),
    surfaceContainerHighest = Color(0xFFE8E4D9)
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF5CC8E4),
    onPrimary = Color(0xFF00323F),
    primaryContainer = Color(0xFF004E61),
    onPrimaryContainer = Color(0xFFD2EEF7),
    secondary = Color(0xFFFF9B7A),
    onSecondary = Color(0xFF4A1700),
    secondaryContainer = Color(0xFF7A2C0C),
    onSecondaryContainer = Color(0xFFFFDDD0),
    tertiary = Color(0xFF8FD4A0),
    onTertiary = Color(0xFF00391A),
    tertiaryContainer = Color(0xFF14522D),
    onTertiaryContainer = Color(0xFFD3EFD9),
    background = Color(0xFF0F1A1F),
    onBackground = Color(0xFFE3E9EB),
    surface = Color(0xFF0F1A1F),
    onSurface = Color(0xFFE3E9EB),
    surfaceVariant = Color(0xFF263339),
    onSurfaceVariant = Color(0xFFA7B4BA),
    outline = Color(0xFF6E7C83),
    outlineVariant = Color(0xFF2D3C43),
    surfaceContainerLowest = Color(0xFF0B1317),
    surfaceContainerLow = Color(0xFF16252C),
    surfaceContainer = Color(0xFF16252C),
    surfaceContainerHigh = Color(0xFF1D2E36),
    surfaceContainerHighest = Color(0xFF263339)
)

private val BaseTypography = Typography()

private val AppTypography = Typography(
    displaySmall = BaseTypography.displaySmall.copy(
        fontWeight = FontWeight.Bold,
        letterSpacing = (-0.5).sp
    ),
    headlineMedium = BaseTypography.headlineMedium.copy(fontWeight = FontWeight.Bold),
    titleLarge = BaseTypography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
    titleMedium = BaseTypography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
    titleSmall = BaseTypography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
    labelLarge = BaseTypography.labelLarge.copy(fontWeight = FontWeight.Medium)
)

private val AppShapes = Shapes(
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(20.dp),
    large = RoundedCornerShape(28.dp)
)

@Composable
fun TripPlannerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = AppTypography,
        shapes = AppShapes,
        content = content
    )
}
