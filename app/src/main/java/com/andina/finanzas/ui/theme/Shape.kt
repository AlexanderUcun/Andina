package com.andina.finanzas.ui.theme

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// ============================================================================
// 📐 SISTEMA DE FORMAS Y RADIOS (SHAPES & BORDERS)
// ============================================================================

val CornerSmall = RoundedCornerShape(12.dp)
val CornerMedium = RoundedCornerShape(14.dp)
val CornerLarge = RoundedCornerShape(18.dp)
val CornerExtraLarge = RoundedCornerShape(24.dp)
val CornerSheet = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
val CornerPill = CircleShape

/**
 * Mapeo oficial de Material 3 Shapes para Andina (Escala estricta de 4 niveles).
 */
val AndinaShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = CornerSmall,        // 12.dp: Chips, badges y etiquetas pequeñas
    medium = CornerMedium,      // 14.dp: Filas interactivas, campos de entrada y cajas secundarias
    large = CornerLarge,        // 18.dp: Tarjetas estándar de sección
    extraLarge = CornerExtraLarge // 24.dp: Tarjetas Hero, BottomSheets, Modales
)
