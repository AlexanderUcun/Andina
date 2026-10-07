package com.andina.finanzas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.andina.finanzas.ui.navigation.AndinaAppShell
import com.andina.finanzas.ui.theme.AndinaTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Actividad principal y punto de entrada visual de Andina Finanzas.
 *
 * Características:
 * - Edge-to-Edge nativo (`enableEdgeToEdge()`).
 * - Punto de entrada Hilt (`@AndroidEntryPoint`).
 * - Aplicación del sistema de diseño unificado [AndinaTheme].
 * - Inyección y renderizado del shell raíz [AndinaAppShell].
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AndinaTheme {
                AndinaAppShell()
            }
        }
    }
}
