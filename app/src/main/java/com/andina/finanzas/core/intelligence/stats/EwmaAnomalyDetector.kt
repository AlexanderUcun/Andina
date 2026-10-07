package com.andina.finanzas.core.intelligence.stats

import kotlin.math.sqrt

/**
 * Resultado de evaluación de gasto diario con EWMA.
 */
data class EwmaEvaluation(
    val currentExpense: Long,
    val baselineEwma: Long,
    val standardDeviation: Double,
    val isAnomaly: Boolean,
    val deviationZScore: Double
)

/**
 * Detector estadístico de anomalías de gasto diario mediante EWMA (ARQUITECTURA.md §10 Nivel 2).
 * Calcula Media Móvil Ponderada Exponencial y Desviación Típica sin saturar memoria.
 */
class EwmaAnomalyDetector(
    private val alpha: Double = 0.25,      // Factor de suavizado (mayor peso a días recientes)
    private val thresholdZ: Double = 2.0    // Umbral de desviación estándar (2 sigmas)
) {

    /**
     * Evalúa una serie cronológica de gastos diarios [dailyExpenses] y determina si el último día es anómalo.
     */
    fun evaluate(dailyExpenses: List<Long>): EwmaEvaluation {
        if (dailyExpenses.isEmpty()) {
            return EwmaEvaluation(0L, 0L, 0.0, false, 0.0)
        }
        if (dailyExpenses.size == 1) {
            val amount = dailyExpenses.first()
            return EwmaEvaluation(amount, amount, 0.0, false, 0.0)
        }

        var ewma = dailyExpenses.first().toDouble()
        var variance = 0.0

        // Iterar hasta el penúltimo elemento para construir la línea base
        for (i in 1 until dailyExpenses.size - 1) {
            val x = dailyExpenses[i].toDouble()
            val diff = x - ewma
            ewma = (alpha * x) + ((1.0 - alpha) * ewma)
            variance = (1.0 - alpha) * (variance + alpha * diff * diff)
        }

        val lastExpense = dailyExpenses.last().toDouble()
        val stdDev = sqrt(variance)

        val zScore = if (stdDev > 0.0) {
            (lastExpense - ewma) / stdDev
        } else {
            0.0
        }

        val isAnomaly = zScore > thresholdZ

        return EwmaEvaluation(
            currentExpense = lastExpense.toLong(),
            baselineEwma = ewma.toLong(),
            standardDeviation = stdDev,
            isAnomaly = isAnomaly,
            deviationZScore = zScore
        )
    }

    /**
     * Calcula el promedio ponderado de gasto diario en la serie.
     */
    fun calculateWeightedDailyAverage(dailyExpenses: List<Long>): Long {
        if (dailyExpenses.isEmpty()) return 0L
        var ewma = dailyExpenses.first().toDouble()
        for (i in 1 until dailyExpenses.size) {
            val x = dailyExpenses[i].toDouble()
            ewma = (alpha * x) + ((1.0 - alpha) * ewma)
        }
        return ewma.toLong().coerceAtLeast(1L)
    }
}
