package com.dynogamer.studio.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val StudioColorScheme = darkColorScheme(
    primary = AccentRed,
    onPrimary = TextOnAccent,
    primaryContainer = AccentRedDark,
    onPrimaryContainer = TextPrimary,
    secondary = AccentRedDark,
    onSecondary = TextOnAccent,
    background = Background,
    onBackground = TextPrimary,
    surface = SurfaceSecondary,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceElevated,
    onSurfaceVariant = TextSecondary,
    error = StatusError,
    onError = TextOnAccent,
    outline = Divider
)

@Composable
fun CPMStudioTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = StudioColorScheme,
        typography = StudioTypography,
        content = content
    )
}
