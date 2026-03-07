package com.vj.sampleaiassistant.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Created by Vijay on 02/03/2026.
 * https://github.com/thenameisvijay
 */

private val LightColors = lightColorScheme(
    primary = Color(0xFF00766C),
    onPrimary = Color.White,
    primaryContainer = Color(0xFF6AA59B),

    secondary = Color(0xFF4A6360),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFCCE8E5),
    onSecondaryContainer = Color(0xFF051F1E),

    background = Color(0xFFF4FBF9),
    onBackground = Color(0xFF191C1C),

    surface = Color(0xFFF4FBF9),
    onSurface = Color(0xFF191C1C),
    surfaceVariant = Color(0xFFDAE5E3),

    error = Color(0xFFB3261E),
    onError = Color.White
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF609E95),
    onPrimary = Color(0xFF003733),
    primaryContainer = Color(0xFF00504B),

    secondary = Color(0xFFB1CCCA),
    onSecondary = Color(0xFF1C3533),
    secondaryContainer = Color(0xFF334B49),
    onSecondaryContainer = Color(0xFFCCE8E5),

    background = Color(0xFF101414),
    onBackground = Color(0xFFE0E3E2),

    surface = Color(0xFF101414),
    onSurface = Color(0xFFE0E3E2),
    surfaceVariant = Color(0xFF3F4948),

    error = Color(0xFFF2B8B5),
    onError = Color(0xFF601410)
)

@Composable
fun ChatAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(), // Toggles based on system settings
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}