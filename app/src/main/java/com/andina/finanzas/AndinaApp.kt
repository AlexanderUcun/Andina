package com.andina.finanzas

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Clase de aplicación raíz de Andina Finanzas.
 * Inicializa el grafo global de inyección de dependencias con Hilt y las librerías nativas de SQLCipher.
 */
@HiltAndroidApp
class AndinaApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Carga inicial de binarios C++ nativos de SQLCipher para cifrado de base de datos
        try {
            System.loadLibrary("sqlcipher")
        } catch (_: Throwable) {
            // Carga de respaldo
        }
    }
}
