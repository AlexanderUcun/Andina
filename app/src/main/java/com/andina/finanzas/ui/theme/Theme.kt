package com.andina.finanzas.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Esquema de Colores Claro Oficial: "Páramo Diurno & Cristal de Cuarzo" (Light Mineral Glass)
private val LightColorScheme = lightColorScheme(
    primary = AndeanPrimary,
    onPrimary = AndeanOnPrimary,
    primaryContainer = AndeanPrimaryContainer,
    onPrimaryContainer = AndeanOnPrimaryContainer,
    secondary = AndeanSecondary,
    onSecondary = AndeanOnSecondary,
    secondaryContainer = AndeanSecondaryContainer,
    onSecondaryContainer = AndeanOnSecondaryContainer,
    surface = AndeanMistCanvasTop,
    onSurface = AndeanSlateInk,
    surfaceContainer = AndeanQuartzGlass,
    surfaceContainerHigh = Color.White,
    onSurfaceVariant = AndeanOnSurfaceVariant,
    outline = AndeanOutline,
    outlineVariant = AndeanFrostBorderBottom,
    error = AndeanError,
    onError = AndeanOnError,
    errorContainer = AndeanErrorContainer,
    onErrorContainer = AndeanOnErrorContainer
)

// Esquema de Colores Oscuro Oficial: "Páramo Nocturno" (OLED #121411 con niebla y reflejos dorados)
private val DarkColorScheme = darkColorScheme(
    primary = AndeanDarkPrimary,
    onPrimary = AndeanDarkSurface,
    primaryContainer = AndeanDarkPrimaryContainer,
    onPrimaryContainer = AndeanDarkOnSurface,
    secondary = AndeanDarkSecondary,
    onSecondary = AndeanDarkSurface,
    secondaryContainer = AndeanDarkSecondaryContainer,
    onSecondaryContainer = AndeanDarkSecondary,
    tertiary = AndeanDarkGoldHighlight,
    onTertiary = AndeanDarkSurface,
    surface = AndeanDarkSurface,
    onSurface = AndeanDarkOnSurface,
    surfaceContainer = AndeanDarkSurfaceContainer,
    surfaceContainerHigh = AndeanDarkSurfaceContainerHigh,
    surfaceContainerLowest = AndeanDarkSurfaceContainerLowest,
    onSurfaceVariant = AndeanDarkOnSurfaceVariant,
    outline = AndeanOutline,
    outlineVariant = AndeanDarkOutlineVariant,
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6)
)

/**
 * Tema principal de Andina.
 *
 * @param darkTheme Determina si se aplica la paleta Páramo Nocturno.
 * @param dynamicColor Desactivado por defecto (false) para preservar la identidad
 *                     artesanal y de marca del páramo andino sin ser sobreescrita por
 *                     los tonos del fondo de pantalla del sistema.
 */
@Composable
fun AndinaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                // Compatibilidad de barras transparentes para Edge-to-Edge nativo
                @Suppress("DEPRECATION")
                if (Build.VERSION.SDK_INT < 35) {
                    window.statusBarColor = Color.Transparent.toArgb()
                    window.navigationBarColor = Color.Transparent.toArgb()
                }
                val insetsController = WindowCompat.getInsetsController(window, view)
                insetsController.isAppearanceLightStatusBars = !darkTheme
                insetsController.isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AndinaTypography,
        shapes = AndinaShapes,
        content = content
    )
}
