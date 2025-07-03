package io.github.zharotiai.help_i_cant_sing.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

private val SubtleBlue = Color(0x1F4285F4)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFFF9800),          // #FF9800 (same accent)
    secondary = Color(0xFFD4A58A),        // #D4A58A
    tertiary = Color(0xFF6E8B91),         // #6E8B91
    error = Color(0xFFE53935),            // #E53935
    background = Color(0xFF1E1E1E),       // Dark background
    surface = Color(0xFF2C2C2C),          // Slightly lighter dark surface
    onPrimary = Color.Black,               // Text/icons on primary (contrast with orange)
    onSecondary = Color.Black,             // Text/icons on secondary
    onTertiary = Color.Black,              // Text/icons on tertiary
    onBackground = Color(0xFFFAF3E0),     // Light text on dark background (neutral)
    onSurface = Color(0xFFFAF3E0)         // Light text on dark surface
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFFFF9800),          // #FF9800
    secondary = Color(0xFFD4A58A),        // #D4A58A
    tertiary = Color(0xFF6E8B91),         // #6E8B91
    error = Color(0xFFE53935),            // #E53935
    background = Color(0xFFFAF3E0),       // #FAF3E0 (neutral)
    surface = Color(0xFFFAF3E0),          // neutral, same as background
    onPrimary = Color.White,               // Text/icons on primary
    onSecondary = Color(0xFF3E2F22),      // A darker shade for text on secondary (customized)
    onTertiary = Color.White,              // Text/icons on tertiary
    onBackground = Color(0xFF3E2F22),     // Dark text on light background
    onSurface = Color(0xFF3E2F22)         // Dark text on light surface
)

@Composable
fun AppTheme(
    darkTheme: Boolean = false, // Default to light theme
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        // typography = Typography, // You can add custom typography here
        // shapes = Shapes,       // You can add custom shapes here
        content = content
    )
}

// Extension object to access custom colors not covered by Material3 ColorScheme
object AppColors {
    val subtleBlue = SubtleBlue
    // Add other custom colors here that don't fit into the Material3 ColorScheme
}
