package com.example.brinted.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import com.google.accompanist.systemuicontroller.rememberSystemUiController

private val ColorBlanco = androidx.compose.ui.graphics.Color.White

private val esquemaOscuro = darkColorScheme(
    primary = Morado,
    onPrimary = ColorBlanco,
    primaryContainer = MoradoOscuro,
    secondary = Cian,
    background = Fondo,
    surface = Tarjeta,
    onSurface = ColorBlanco
)

private val esquemaClaro = lightColorScheme(
    primary = Morado,
    onPrimary = ColorBlanco,
    secondary = Cian,
    background = ColorBlanco,
    surface = ColorBlanco,
    onSurface = Fondo
)

@Composable
fun BrintedTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) esquemaOscuro else esquemaClaro
    val vista = LocalView.current
    val systemUiController = rememberSystemUiController()

    if (!vista.isInEditMode) {
        SideEffect {
            val window = (vista.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            systemUiController.setStatusBarColor(color = colorScheme.background, darkIcons = !darkTheme)
            systemUiController.setNavigationBarColor(color = colorScheme.background, darkIcons = !darkTheme)
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Tipografia,
        content = content
    )
}
