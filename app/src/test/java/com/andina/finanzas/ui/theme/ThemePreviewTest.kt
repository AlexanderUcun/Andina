package com.andina.finanzas.ui.theme

import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

/**
 * Quality Gate de Fase 02:
 * Verificación matemática de accesibilidad y ratios de contraste WCAG AA (>= 4.5:1)
 * para los pares cromáticos oficiales del sistema "Andean Mist & Moss".
 */
class ThemePreviewTest {

    private fun relativeLuminance(r: Int, g: Int, b: Int): Double {
        fun channel(v: Int): Double {
            val s = v / 255.0
            return if (s <= 0.04045) s / 12.92 else ((s + 0.055) / 1.055).pow(2.4)
        }
        return 0.2126 * channel(r) + 0.7152 * channel(g) + 0.0722 * channel(b)
    }

    private fun calculateContrastRatio(hex1: Long, hex2: Long): Double {
        val r1 = ((hex1 shr 16) and 0xFF).toInt()
        val g1 = ((hex1 shr 8) and 0xFF).toInt()
        val b1 = (hex1 and 0xFF).toInt()

        val r2 = ((hex2 shr 16) and 0xFF).toInt()
        val g2 = ((hex2 shr 8) and 0xFF).toInt()
        val b2 = (hex2 and 0xFF).toInt()

        val l1 = relativeLuminance(r1, g1, b1)
        val l2 = relativeLuminance(r2, g2, b2)

        val lighter = max(l1, l2)
        val darker = min(l1, l2)

        return (lighter + 0.05) / (darker + 0.05)
    }

    @Test
    fun `test WCAG AA contrast on primary surface`() {
        // Carbón Mineral (#1D1B19) sobre Superficie Niebla (#FFF8F4)
        val ratio = calculateContrastRatio(0x1D1B19, 0xFFF8F4)
        assertTrue(
            "El contraste de texto principal sobre superficie debe ser >= 4.5:1 (obtenido: $ratio)",
            ratio >= 4.5
        )
    }

    @Test
    fun `test WCAG AA contrast on primary button`() {
        // Blanco (#FFFFFF) sobre Verde Musgo Primario (#2C4427)
        val ratio = calculateContrastRatio(0xFFFFFF, 0x2C4427)
        assertTrue(
            "El contraste de texto sobre botón primario debe ser >= 4.5:1 (obtenido: $ratio)",
            ratio >= 4.5
        )
    }

    @Test
    fun `test WCAG AA contrast on primary container button`() {
        // Blanco (#FFFFFF) sobre Verde Musgo Contenedor (#435C3C)
        val ratio = calculateContrastRatio(0xFFFFFF, 0x435C3C)
        assertTrue(
            "El contraste sobre el botón central (+) debe ser >= 4.5:1 (obtenido: $ratio)",
            ratio >= 4.5
        )
    }

    @Test
    fun `test WCAG AA contrast in dark mode surface`() {
        // Texto Blanco Mineral (#E3E5DF) sobre Fondo Páramo Nocturno (#121411)
        val ratio = calculateContrastRatio(0xE3E5DF, 0x121411)
        assertTrue(
            "El contraste en modo oscuro debe ser >= 4.5:1 (obtenido: $ratio)",
            ratio >= 4.5
        )
    }

    @Test
    fun `test WCAG AA contrast for dark mode primary and gold highlight`() {
        // Verde Musgo Desaturado (#8BB582) sobre Fondo Páramo Nocturno (#121411)
        val primaryRatio = calculateContrastRatio(0x8BB582, 0x121411)
        assertTrue(
            "El contraste de AndeanDarkPrimary sobre superficie debe ser >= 4.5:1 (obtenido: $primaryRatio)",
            primaryRatio >= 4.5
        )

        // Reflejo Dorado (#D4AF37) sobre Fondo Páramo Nocturno (#121411)
        val goldRatio = calculateContrastRatio(0xD4AF37, 0x121411)
        assertTrue(
            "El contraste de reflejo dorado sobre superficie debe ser >= 4.5:1 (obtenido: $goldRatio)",
            goldRatio >= 4.5
        )
    }

    @Test
    fun `test WCAG AA contrast on error color`() {
        // Blanco (#FFFFFF) sobre Error Andino (#BA1A1A)
        val ratio = calculateContrastRatio(0xFFFFFF, 0xBA1A1A)
        assertTrue(
            "El contraste de error debe ser >= 4.5:1 (obtenido: $ratio)",
            ratio >= 4.5
        )
    }
}
