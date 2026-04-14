package com.example.pharmacystock

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// LIGHT MODE COLORS
private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF0A0F2C),
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onBackground = Color.Black,
    onSurface = Color.Black
)

// DARK MODE COLORS
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF0A0F2C),
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    onPrimary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White
)

@Composable
fun PharmacyStockTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {

    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}