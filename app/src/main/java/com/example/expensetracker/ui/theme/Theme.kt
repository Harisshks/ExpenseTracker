package com.example.expensetracker.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowCompat.getInsetsController

val AppTeal = Color(0xFF2F6F73)

private val FixedColorScheme = lightColorScheme(
    primary = AppTeal,
    onPrimary = Color.White,

    background = Color.White,
    onBackground = Color.Black,

    surface = Color.White,
    onSurface = Color.Black,

    error = Color(0xFFB00020)
)

@Composable
fun ExpenseTrackerTheme(
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect { val window = (view.context as Activity).window
            window.statusBarColor = AppTeal.toArgb()
            getInsetsController(window, view)
                .isAppearanceLightStatusBars = false } }

    MaterialTheme(
        colorScheme = FixedColorScheme,
        typography = Typography,
        content = content
    )
}
