package com.andina.finanzas

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Clase de aplicación raíz de Andina Finanzas.
 * Inicializa el grafo global de inyección de dependencias con Hilt.
 */
@HiltAndroidApp
class AndinaApp : Application()
