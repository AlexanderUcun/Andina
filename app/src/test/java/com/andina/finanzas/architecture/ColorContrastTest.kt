package com.andina.finanzas.architecture

import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

/**
 * Validador automatizado de ratio de contraste cromático WCAG 2.x.
 *
 * Verifica que todos los pares tipográficos de texto sobre superficies cumplan:
 * - WCAG AA Texto normal: >= 4.5:1
 * - WCAG AA Texto grande (>= 24sp / 18sp bold): >= 3.0:1
 */
class ColorContrastTest {

    private data class RgbColor(val r: Int, val g: Int, val b: Int) {
        companion object {
            fun fromHex(hex: String): RgbColor {
                val cleanHex = hex.removePrefix("#")
                val r = cleanHex.substring(0, 2).toInt(16)
                val g = cleanHex.substring(2, 4).toInt(16)
                val b = cleanHex.substring(4, 6).toInt(16)
                return RgbColor(r, g, b)
            }
        }

        /**
         * Calcula la luminancia relativa según estándar W3C WCAG 2.1.
         */
        fun relativeLuminance(): Double {
            fun channelLuminance(c: Int): Double {
                val s = c / 255.0
                return if (s <= 0.04045) s / 12.92 else ((s + 0.055) / 1.055).pow(2.4)
            }
            return 0.2126 * channelLuminance(r) + 0.7152 * channelLuminance(g) + 0.0722 * channelLuminance(b)
        }

        fun contrastRatioWith(other: RgbColor): Double {
            val l1 = relativeLuminance()
            val l2 = other.relativeLuminance()
            val lighter = max(l1, l2)
            val darker = min(l1, l2)
            return (lighter + 0.05) / (darker + 0.05)
        }
    }

    // Superficies oficiales de la paleta Platinum Light
    private val canvasLight = RgbColor.fromHex("#F8F9FA")
    private val cardWhite = RgbColor.fromHex("#FFFFFF")
    private val heroPlatinumTop = RgbColor.fromHex("#E0E4E8")
    private val heroPlatinumBottom = RgbColor.fromHex("#CBD5E1")

    // Colores de texto oficiales auditados
    private val slateInkPrimary = RgbColor.fromHex("#1E2022") // Títulos y saldo maestro
    private val mutedSlateSecondary = RgbColor.fromHex("#4B525A") // Subtítulos y fechas
    private val moneyBronze = RgbColor.fromHex("#6E5220") // Cifras monetarias en texto regular
    private val moneyDisplayGold = RgbColor.fromHex("#8A6A2F") // Cifras monetarias display grandes (>= 24sp)
    private val lakeBlueInteractive = RgbColor.fromHex("#2563EB") // Enlaces y botones interactivos
    private val dangerDark = RgbColor.fromHex("#991B1B") // Errores y sobregiro crítico (WCAG AA estricto)

    @Test
    fun verifyPrimaryTextContrastOnAllSurfaces() {
        val surfaces = listOf("Canvas" to canvasLight, "Card" to cardWhite, "HeroTop" to heroPlatinumTop, "HeroBottom" to heroPlatinumBottom)
        for ((name, surface) in surfaces) {
            val ratio = slateInkPrimary.contrastRatioWith(surface)
            assertTrue("slateInkPrimary sobre $name tiene ratio $ratio, requiere >= 4.5", ratio >= 4.5)
        }
    }

    @Test
    fun verifySecondaryTextContrastOnAllSurfaces() {
        val surfaces = listOf("Canvas" to canvasLight, "Card" to cardWhite, "HeroTop" to heroPlatinumTop, "HeroBottom" to heroPlatinumBottom)
        for ((name, surface) in surfaces) {
            val ratio = mutedSlateSecondary.contrastRatioWith(surface)
            assertTrue("mutedSlateSecondary sobre $name tiene ratio $ratio, requiere >= 4.5", ratio >= 4.5)
        }
    }

    @Test
    fun verifyMoneyBronzeTextContrastOnAllSurfaces() {
        val surfaces = listOf("Canvas" to canvasLight, "Card" to cardWhite, "HeroTop" to heroPlatinumTop, "HeroBottom" to heroPlatinumBottom)
        for ((name, surface) in surfaces) {
            val ratio = moneyBronze.contrastRatioWith(surface)
            assertTrue("moneyBronze sobre $name tiene ratio $ratio, requiere >= 4.5", ratio >= 4.5)
        }
    }

    @Test
    fun verifyMoneyDisplayGoldMeetsLargeTextContrastOnHero() {
        // Texto display grande (>= 24sp bold) requiere >= 3.0:1 según WCAG AA
        val ratioOnTop = moneyDisplayGold.contrastRatioWith(heroPlatinumTop)
        val ratioOnBottom = moneyDisplayGold.contrastRatioWith(heroPlatinumBottom)
        assertTrue("moneyDisplayGold sobre heroTop ($ratioOnTop) requiere >= 3.0", ratioOnTop >= 3.0)
        assertTrue("moneyDisplayGold sobre heroBottom ($ratioOnBottom) requiere >= 3.0", ratioOnBottom >= 3.0)
    }

    @Test
    fun verifyLakeBlueContrastOnCanvasAndCards() {
        val ratioCanvas = lakeBlueInteractive.contrastRatioWith(canvasLight)
        val ratioCard = lakeBlueInteractive.contrastRatioWith(cardWhite)
        assertTrue("lakeBlueInteractive sobre canvas ($ratioCanvas) requiere >= 4.5", ratioCanvas >= 4.5)
        assertTrue("lakeBlueInteractive sobre card ($ratioCard) requiere >= 4.5", ratioCard >= 4.5)
    }

    @Test
    fun verifyDangerTextContrastOnAllSurfaces() {
        val surfaces = listOf("Canvas" to canvasLight, "Card" to cardWhite, "HeroTop" to heroPlatinumTop, "HeroBottom" to heroPlatinumBottom)
        for ((name, surface) in surfaces) {
            val ratio = dangerDark.contrastRatioWith(surface)
            assertTrue("dangerDark sobre $name tiene ratio $ratio, requiere >= 4.5", ratio >= 4.5)
        }
    }
}
