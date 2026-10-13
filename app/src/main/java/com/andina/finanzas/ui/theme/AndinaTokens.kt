package com.andina.finanzas.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/**
 * Tokens cromáticos oficiales del sistema "Platino Mineral & Oro Café" (Andina Platinum Light Luxury).
 * Diseñado conforme a los estándares WCAG 2.1 AA/AAA sin fondos oscuros pesados.
 */
@Immutable
data class AndinaColors(
    // 3.1 Superficies
    val canvasTop: Color = Color(0xFFF8F9FA),
    val canvasBottom: Color = Color(0xFFF1F3F5),
    val cardBackground: Color = Color(0xFFFFFFFF),
    val cardBorder: Color = Color(0xFFE2E8F0),
    val heroTop: Color = Color(0xFFE0E4E8),
    val heroBottom: Color = Color(0xFFCBD5E1),
    val specularBorderTop: Color = Color(0xFFFFFFFF),
    val specularBorderBottom: Color = Color(0xFFCBD5E1),
    val ambientShadow: Color = Color(0x141E2022), // 8%

    // 3.2 Tipografía y Textos (Todos >= 4.5:1 en todas las superficies)
    val textPrimary: Color = Color(0xFF1E2022),
    val textSecondary: Color = Color(0xFF4B525A),
    val textMoney: Color = Color(0xFF6E5220), // Oro bronce para texto corriente
    val textMoneyDisplay: Color = Color(0xFF8A6A2F), // Oro luminoso para cifras display >= 24sp
    val textLink: Color = Color(0xFF2563EB),

    // 3.3 Acentos y Estados
    val accentGold: Color = Color(0xFFC5A059),
    val accentMocha: Color = Color(0xFF4A3E3D),
    val accentBlueSoft: Color = Color(0xFF3B82F6),
    val statusSuccess: Color = Color(0xFF15803D),
    val statusWarning: Color = Color(0xFFB45309),
    val statusDanger: Color = Color(0xFF991B1B),
    val statusDangerFill: Color = Color(0xFFDC2626),

    // 3.4 Navegación de Lujo (Vivid Jewel Tones con alto contraste)
    val navHome: Color = Color(0xFFD4AF37),        // Oro Solar Champagne
    val navTransactions: Color = Color(0xFF2563EB), // Azul Zafiro Glaciar
    val navWallets: Color = Color(0xFF10B981),      // Esmeralda Viva
    val navConsultorio: Color = Color(0xFF8B5CF6),  // Amatista Cuarzo IA
    val navInactive: Color = Color(0xFF64748B)      // Platino Pizarra
) {
    val canvasGradient: Brush
        get() = Brush.verticalGradient(listOf(canvasTop, canvasBottom))

    val heroGradient: Brush
        get() = Brush.verticalGradient(listOf(heroTop, heroBottom))

    val fabGoldGradient: Brush
        get() = Brush.verticalGradient(listOf(Color(0xFFF7DE8B), Color(0xFFD4AF37), Color(0xFFA67C24)))
}

val LocalAndinaColors = staticCompositionLocalOf { AndinaColors() }

/**
 * Paleta categórica para gráficas (8 colores armonizados distinguibles).
 */
object AndinaChartColors {
    val Palette = listOf(
        Color(0xFF2563EB), // Azul Lago
        Color(0xFFC5A059), // Oro Frailejón
        Color(0xFF15803D), // Esmeralda
        Color(0xFF4A3E3D), // Moca Café
        Color(0xFF8B5CF6), // Amatista
        Color(0xFFEA580C), // Terracota
        Color(0xFF0284C7), // Glaciar
        Color(0xFF64748B)  // Platino Pizarra
    )
}

/**
 * Mapeador de compatibilidad para colores legados de la base de datos a la nueva paleta.
 */
object LegacyColorMapper {
    fun mapLegacyHex(hex: String): Color {
        return try {
            val clean = hex.removePrefix("#").uppercase()
            when (clean) {
                "4A154B", "700054" -> Color(0xFF8F14F7) // Nequi vibrante
                "EE2724", "B32D2D" -> Color(0xFFEE1222) // Daviplata
                "FDDA24", "C58934" -> Color(0xFFFDDA24) // Bancolombia
                "435C3C", "2C4427" -> Color(0xFF2C4427) // Efectivo Jade
                "D97706" -> Color(0xFFC5A059) // Café & Snacks -> Oro
                "2E7D32" -> Color(0xFF15803D) // Alimentación -> Esmeralda
                "1976D2" -> Color(0xFF2563EB) // Transporte -> Azul Lago
                "BA1A1A", "D32F2F" -> Color(0xFF991B1B) // Alerta
                else -> Color(android.graphics.Color.parseColor(hex))
            }
        } catch (_: Exception) {
            Color(0xFF2563EB)
        }
    }

    /**
     * Calcula automáticamente el color de contenido (onColor) de alto contraste.
     */
    fun contentColorFor(background: Color): Color {
        // Luminancia perceptiva aproximada W3C
        val luminance = 0.299 * background.red + 0.587 * background.green + 0.114 * background.blue
        return if (luminance > 0.5) Color(0xFF1E2022) else Color(0xFFFFFFFF)
    }
}
