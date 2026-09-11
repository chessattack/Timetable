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

val AppBackground = Color(0xFFFAF9F2)
val PrimaryNavy = Color(0xFF263E75)
val TextNavy = Color(0xFF172047)

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryNavy,
    background = AppBackground,
    surface = AppBackground,
    onPrimary = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryNavy,
    background = AppBackground,
    surface = AppBackground,
    onPrimary = Color.White,
    onBackground = TextNavy,
    onSurface = TextNavy,
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
