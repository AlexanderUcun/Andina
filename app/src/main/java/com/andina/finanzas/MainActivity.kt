package com.andina.finanzas

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.fragment.app.FragmentActivity
import com.andina.finanzas.ui.navigation.AndinaAppShell
import com.andina.finanzas.ui.theme.AndinaTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Actividad principal y punto de entrada visual de Andina Finanzas.
 *
 * Características:
 * - Edge-to-Edge nativo (`enableEdgeToEdge()`).
 * - Punto de entrada Hilt (`@AndroidEntryPoint`).
 * - Soporte nativo para autenticación biométrica (`FragmentActivity`).
 * - Aplicación del sistema de diseño unificado [AndinaTheme].
 * - Inyección y renderizado del shell raíz [AndinaAppShell].
 */
@AndroidEntryPoint
class MainActivity : FragmentActivity() {
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
