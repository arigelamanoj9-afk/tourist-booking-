package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = DarkCanyonPrimary,
    onPrimary = Color(0xFF5E170C),
    primaryContainer = SandstoneRedDark,
    onPrimaryContainer = Color(0xFFFFDAD3),
    secondary = DarkCanyonSecondary,
    onSecondary = Color(0xFF5A1C00),
    secondaryContainer = SunsetOrangeDark,
    onSecondaryContainer = Color(0xFFFFDBCE),
    tertiary = DarkCanyonTertiary,
    onTertiary = Color(0xFF003916),
    background = DarkCanyonBackground,
    surface = DarkCanyonSurface,
    onBackground = Color(0xFFEDE0DC),
    onSurface = Color(0xFFEDE0DC),
  )

private val LightColorScheme =
  lightColorScheme(
    primary = SandstoneRed,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFDAD3),
    onPrimaryContainer = Color(0xFF3F0400),
    secondary = SunsetOrange,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFDBCF),
    onSecondaryContainer = Color(0xFF380E00),
    tertiary = ForestGreen,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFAEF3BC),
    onTertiaryContainer = Color(0xFF00210A),
    background = SandCreamLight,
    surface = SandCreamLight,
    surfaceVariant = SandSurfaceVariant,
    onBackground = Color(0xFF231917),
    onSurface = Color(0xFF231917),
    outline = WarmSandOutline,
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false, // Use rich custom canyon palette
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
