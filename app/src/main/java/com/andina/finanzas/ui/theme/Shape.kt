package com.andina.finanzas.ui.theme

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// ============================================================================
// 📐 SISTEMA DE FORMAS Y RADIOS (SHAPES & BORDERS)
// ============================================================================

val CornerSmall = RoundedCornerShape(8.dp)
val CornerMedium = RoundedCornerShape(12.dp)
val CornerLarge = RoundedCornerShape(18.dp)
val CornerExtraLarge = RoundedCornerShape(24.dp)
val CornerSheet = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
val CornerPill = CircleShape

/**
 * Mapeo oficial de Material 3 Shapes para Andina.
 */
val AndinaShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = CornerSmall,        // 8.dp: Avatares, badges, mini-chips
    medium = CornerMedium,      // 12.dp: Botones secundarios, tarjetas compactas
    large = CornerLarge,        // 18.dp: Hero cards, FrostedGlassCard
    extraLarge = CornerExtraLarge // 24.dp: Modales, BottomSheets, CTA
)
