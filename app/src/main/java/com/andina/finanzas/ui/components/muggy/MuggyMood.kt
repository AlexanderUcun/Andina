package com.andina.finanzas.ui.components.muggy

import androidx.compose.ui.graphics.Color
import com.andina.finanzas.ui.theme.AndeanEmeraldLight
import com.andina.finanzas.ui.theme.AndeanVolcanicEmber
import com.andina.finanzas.ui.theme.FrailejonGold

/**
 * Estados Biomecánicos Emocionales de Muggy (Pilar 6).
 * Enlazados determinísticamente al [FinancialHealthScorer] local (Zero-Cloud).
 */
enum class MuggyMood(
    val auraColor: Color,
    val description: String
) {
    /** Score 80-100: Rostro sereno, respiración pausada, aura esmeralda radiante */
    HOLGADO(
        auraColor = AndeanEmeraldLight,
        description = "Muggy sereno y confiado"
    ),

    /** Score 60-79: Mirada atenta, parpadeo ocasional, aura dorada cálida */
    PREVENTIVO(
        auraColor = FrailejonGold,
        description = "Muggy atento con sus gafas"
    ),

    /** Score 40-59: Ceño analítico ante gastos hormiga, aura ámbar atenuada */
    AJUSTE(
        auraColor = FrailejonGold.copy(alpha = 0.7f),
        description = "Muggy reflexivo analizando egresos"
    ),

    /** Score 0-39: Expresión protectora y empática (sin regaños), aura ámbar de rescate */
    CRITICO(
        auraColor = AndeanVolcanicEmber.copy(alpha = 0.75f),
        description = "Muggy protector extendiendo la mano"
    );

    companion object {
        fun fromScore(score: Int): MuggyMood = when {
            score >= 80 -> HOLGADO
            score >= 60 -> PREVENTIVO
            score >= 40 -> AJUSTE
            else -> CRITICO
        }
    }
}
