package com.andina.finanzas.core.intelligence.triage

import com.andina.finanzas.ui.components.muggy.MuggyPose

/**
 * Estados formales del Sistema Experto de Triaje Financiero (ARQUITECTURA.md §10 Nivel 3).
 */
enum class TriageLevel {
    TRIAGE_HOLGADO,     // Score 80-100: Páramo en equilibrio, excedentes
    TRIAGE_PREVENTIVO,  // Score 60-79: Alerta temprana, moderar salidas
    TRIAGE_AJUSTE,      // Score 40-59: Desbordamiento en categorías, ajustar topes
    TRIAGE_CRITICO      // Score 0-39: Protocolo de emergencia, proteger liquidez
}

data class FinancialHealthScore(
    val totalScore: Int,                 // 0 a 100
    val budgetScore: Double,             // Componente presupuesto (máx 35 pts)
    val antExpenseScore: Double,         // Componente hormiga (máx 25 pts)
    val oxygenDaysScore: Double,         // Componente liquidez (máx 25 pts)
    val pocketsPaceScore: Double,        // Componente ahorro (máx 15 pts)
    val triageLevel: TriageLevel,
    val recommendedPose: MuggyPose
)

/**
 * Sistema Experto de Triaje Financiero Ponderado (ARQUITECTURA.md §10 Nivel 3).
 * Evalúa las 4 dimensiones contables en < 2 ms con 100% de rigor matemático.
 */
class FinancialHealthScorer {

    /**
     * Calcula el Score de Salud Financiera (0 - 100).
     * @param budgetConsumptionRatio Consumo del presupuesto mensual (ej. 0.65 = 65% gastado).
     * @param antExpenseRatio Proporción de gasto hormiga sobre gasto total (ej. 0.12 = 12%).
     * @param oxygenDays Días de oxígeno calculados.
     * @param hasActivePocketContributions Si el usuario aportó a metas en el periodo.
     */
    fun calculateScore(
        budgetConsumptionRatio: Double,
        antExpenseRatio: Double,
        oxygenDays: Int,
        hasActivePocketContributions: Boolean
    ): FinancialHealthScore {
        // 1. Consumo del presupuesto (35% ponderación)
        val budgetScore = when {
            budgetConsumptionRatio <= 0.60 -> 35.0
            budgetConsumptionRatio <= 0.80 -> 28.0
            budgetConsumptionRatio <= 1.00 -> 18.0
            budgetConsumptionRatio <= 1.20 -> 8.0
            else -> 0.0
        }

        // 2. Fricción por Gastos Hormiga (25% ponderación)
        val antExpenseScore = when {
            antExpenseRatio <= 0.05 -> 25.0
            antExpenseRatio <= 0.10 -> 20.0
            antExpenseRatio <= 0.20 -> 12.0
            antExpenseRatio <= 0.30 -> 5.0
            else -> 0.0
        }

        // 3. Días de Oxígeno / Liquidez (25% ponderación)
        val oxygenDaysScore = when {
            oxygenDays >= 30 -> 25.0
            oxygenDays >= 20 -> 20.0
            oxygenDays >= 12 -> 15.0
            oxygenDays >= 6  -> 8.0
            else -> 2.0
        }

        // 4. Ritmo de Aporte a Bolsillos (15% ponderación)
        val pocketsPaceScore = if (hasActivePocketContributions) 15.0 else 5.0

        val totalPoints = (budgetScore + antExpenseScore + oxygenDaysScore + pocketsPaceScore)
            .coerceIn(0.0, 100.0)
        val totalScore = totalPoints.toInt()

        val triageLevel = when {
            totalScore >= 80 -> TriageLevel.TRIAGE_HOLGADO
            totalScore >= 60 -> TriageLevel.TRIAGE_PREVENTIVO
            totalScore >= 40 -> TriageLevel.TRIAGE_AJUSTE
            else -> TriageLevel.TRIAGE_CRITICO
        }

        val recommendedPose = when (triageLevel) {
            TriageLevel.TRIAGE_HOLGADO -> MuggyPose.CELEBRATING
            TriageLevel.TRIAGE_PREVENTIVO -> MuggyPose.OBSERVING
            TriageLevel.TRIAGE_AJUSTE -> MuggyPose.REFLECTIVE
            TriageLevel.TRIAGE_CRITICO -> MuggyPose.REFLECTIVE
        }

        return FinancialHealthScore(
            totalScore = totalScore,
            budgetScore = budgetScore,
            antExpenseScore = antExpenseScore,
            oxygenDaysScore = oxygenDaysScore,
            pocketsPaceScore = pocketsPaceScore,
            triageLevel = triageLevel,
            recommendedPose = recommendedPose
        )
    }
}
