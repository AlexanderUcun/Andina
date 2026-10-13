package com.andina.finanzas.architecture

import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * Test guardián de arquitectura con "trinquete" (ratchet).
 *
 * Su función es impedir cualquier regresión en la dispersión de:
 * 1. Iconos Material fuera de ui/icons (meta: 0).
 * 2. Color(0x...) hardcodeados fuera de ui/theme (meta: 0).
 * 3. isSystemInDarkTheme() fuera de Theme.kt (meta: 0).
 * 4. Color.White / Color.Black fuera de ui/theme (meta: 0).
 *
 * Estos límites MÁXIMOS solo pueden reducirse a medida que cada lote migra sus archivos.
 */
class ArchitectureGuardTest {

    private val projectRoot = File("src/main/java/com/andina/finanzas")

    // Límites de la línea base auditada el 12-oct-2026.
    // Conforme avancen los lotes de la Fase 3B, estos números bajan hasta 0.
    private val maxIconsOutsideUiIcons = 430
    private val maxHardcodedColorsOutsideTheme = 243
    private val maxIsSystemInDarkThemeOutsideTheme = 8
    private val maxColorWhiteBlackOutsideTheme = 80

    @Test
    fun verifyIconsDoNotExceedRatchetThreshold() {
        val uiDir = File(projectRoot, "ui")
        val iconPattern = Regex("""Icons\.(Filled|Outlined|Rounded|AutoMirrored|Default)""")

        val count = uiDir.walkTopDown()
            .filter { it.isFile && it.extension == "kt" }
            .filterNot { it.invariantSeparatorsPath.contains("/ui/icons/") }
            .sumOf { file ->
                file.readLines().count { line -> iconPattern.containsMatchIn(line) }
            }

        assertTrue(
            "Regresión detectada: se encontraron $count usos de Icons fuera de ui/icons, superando el límite de $maxIconsOutsideUiIcons",
            count <= maxIconsOutsideUiIcons
        )
    }

    @Test
    fun verifyHardcodedColorsDoNotExceedRatchetThreshold() {
        val uiDir = File(projectRoot, "ui")
        val colorHexPattern = Regex("""Color\(0x""")

        val count = uiDir.walkTopDown()
            .filter { it.isFile && it.extension == "kt" }
            .filterNot { it.invariantSeparatorsPath.contains("/ui/theme/") }
            .sumOf { file ->
                file.readLines().count { line -> colorHexPattern.containsMatchIn(line) }
            }

        assertTrue(
            "Regresión detectada: se encontraron $count colores hardcodeados Color(0x...) fuera de ui/theme, superando el límite de $maxHardcodedColorsOutsideTheme",
            count <= maxHardcodedColorsOutsideTheme
        )
    }

    @Test
    fun verifyIsSystemInDarkThemeDoesNotExceedRatchetThreshold() {
        val uiDir = File(projectRoot, "ui")
        val darkThemePattern = Regex("""isSystemInDarkTheme\(\)""")

        val count = uiDir.walkTopDown()
            .filter { it.isFile && it.extension == "kt" && it.name != "Theme.kt" }
            .sumOf { file ->
                file.readLines().count { line -> darkThemePattern.containsMatchIn(line) }
            }

        assertTrue(
            "Regresión detectada: se encontraron $count llamadas a isSystemInDarkTheme() fuera de Theme.kt, superando el límite de $maxIsSystemInDarkThemeOutsideTheme",
            count <= maxIsSystemInDarkThemeOutsideTheme
        )
    }

    @Test
    fun verifyColorWhiteBlackDoesNotExceedRatchetThreshold() {
        val uiDir = File(projectRoot, "ui")
        val whiteBlackPattern = Regex("""Color\.(White|Black)""")

        val count = uiDir.walkTopDown()
            .filter { it.isFile && it.extension == "kt" }
            .filterNot { it.invariantSeparatorsPath.contains("/ui/theme/") }
            .sumOf { file ->
                file.readLines().count { line -> whiteBlackPattern.containsMatchIn(line) }
            }

        assertTrue(
            "Regresión detectada: se encontraron $count referencias a Color.White/Black fuera de ui/theme, superando el límite de $maxColorWhiteBlackOutsideTheme",
            count <= maxColorWhiteBlackOutsideTheme
        )
    }
}
