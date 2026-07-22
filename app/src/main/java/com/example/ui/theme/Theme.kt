package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color

private val DarkColorScheme =
  darkColorScheme(
    primary = HighDensityPrimaryContainer,
    secondary = HighDensitySecondary,
    tertiary = HighDensityTertiary,
    background = Color(0xFF121214),
    surface = Color(0xFF1E1E24),
    onBackground = Color(0xFFE2E2E6),
    onSurface = Color(0xFFE2E2E6),
    onPrimary = HighDensityPrimary,
    primaryContainer = HighDensityPrimary,
    onPrimaryContainer = HighDensityPrimaryContainer,
    outlineVariant = Color(0xFF2E2E36)
  )

private val LightColorScheme =
  lightColorScheme(
    primary = HighDensityPrimary,
    secondary = HighDensitySecondary,
    tertiary = HighDensityTertiary,
    background = HighDensityBackground,
    onBackground = HighDensityOnSurface,
    surface = HighDensitySurface,
    onSurface = HighDensityOnSurface,
    onPrimary = Color.White,
    primaryContainer = HighDensityPrimaryContainer,
    onPrimaryContainer = HighDensityOnPrimaryContainer,
    onSecondary = Color.White,
    onTertiary = HighDensityPrimary,
    surfaceVariant = HighDensityBackground,
    onSurfaceVariant = HighDensityOnSurfaceVariant,
    outlineVariant = HighDensityOutlineVariant
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Disable dynamic color by default to guarantee our High Density custom theme colors are applied
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }

      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
