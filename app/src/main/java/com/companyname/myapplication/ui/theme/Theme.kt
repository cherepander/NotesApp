package com.companyname.myapplication.ui.theme


import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorPalette = darkColorScheme(
    primary = androidx.compose.ui.graphics.Color(0xFF1E88E5),
    onPrimary = androidx.compose.ui.graphics.Color.White
)

private val LightColorPalette = lightColorScheme(
    primary = androidx.compose.ui.graphics.Color(0xFF2196F3),
    onPrimary = androidx.compose.ui.graphics.Color.White
)

@Composable
fun NotesAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColorPalette else LightColorPalette
    MaterialTheme(
        colorScheme = colors,
        content = content
    )
}
