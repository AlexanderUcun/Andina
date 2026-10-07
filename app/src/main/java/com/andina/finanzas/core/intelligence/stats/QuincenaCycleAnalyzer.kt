package com.andina.finanzas.core.intelligence.stats

import java.util.Calendar

enum class QuincenaHealthStatus {
    HOLGADO,      // Saldo proyectado holgado para llegar al pago
    EQUILIBRADO,  // Llega a tiempo dentro del margen
    AJUSTADO,     // El gasto actual consumirá la liquidez antes de la quincena
    CRITICO       // Saldo disponible insuficiente
}

/**
 * Reporte de análisis del Ciclo Quincena Andino y Días de Oxígeno.
 */
data class QuincenaReport(
    val currentDayOfMonth: Int,
    val targetPayoutDay: Int,
    val daysRemainingToPayout: Int,
    val daysElapsedInQuincena: Int,
    val currentLiquidBalance: Long,
    val dailyBurnRate: Long,
    val projectedExpenseToPayout: Long,
    val projectedBalanceAtPayout: Long,
    val oxygenDays: Int,
    val status: QuincenaHealthStatus
)

/**
 * Analizador del Ciclo Quincenal Colombiano y Runway Financiero (ARQUITECTURA.md §10 Nivel 2).
 * Modela la estacionalidad de pagos en Colombia (días 15 y 30) y proyecta la velocidad de quema de saldo.
 */
class QuincenaCycleAnalyzer {

    /**
     * Analiza el estado quincenal del usuario.
     * @param currentLiquidBalance Saldo disponible en cuentas/billeteras.
     * @param quincenaSpentExpenses Gasto acumulado en la quincena en curso.
     * @param weightedDailyAverage Gasto diario ponderado histórico (EWMA).
     * @param calendar Instancia de calendario para obtener el día del mes.
     */
    fun analyze(
        currentLiquidBalance: Long,
        quincenaSpentExpenses: Long,
        weightedDailyAverage: Long,
        calendar: Calendar = Calendar.getInstance()
    ): QuincenaReport {
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
        val maxDaysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

        val isFirstQuincena = dayOfMonth <= 15
        val targetPayoutDay = if (isFirstQuincena) 15 else maxDaysInMonth
        val daysElapsed = if (isFirstQuincena) dayOfMonth else (dayOfMonth - 15)
        val daysRemaining = (targetPayoutDay - dayOfMonth).coerceAtLeast(1)

        // Tasa de quema de saldo diaria (Burn Rate)
        val actualDailyBurn = if (daysElapsed > 0) {
            quincenaSpentExpenses / daysElapsed
        } else {
            weightedDailyAverage
        }
        val effectiveDailyBurn = if (actualDailyBurn > 0L) actualDailyBurn else weightedDailyAverage.coerceAtLeast(1L)

        // Proyección de gasto restante hasta el día de pago
        val projectedExpense = effectiveDailyBurn * daysRemaining
        val projectedBalanceAtPayout = currentLiquidBalance - projectedExpense

        // Cálculo de Días de Oxígeno (Runway Financiero)
        val divisor = if (effectiveDailyBurn > 0L) effectiveDailyBurn else 1L
        val oxygenDays = if (currentLiquidBalance > 0L) {
            (currentLiquidBalance / divisor).toInt()
        } else {
            0
        }

        val status = when {
            currentLiquidBalance <= 0L -> QuincenaHealthStatus.CRITICO
            projectedBalanceAtPayout < 0L -> QuincenaHealthStatus.AJUSTADO
            projectedBalanceAtPayout >= (currentLiquidBalance * 0.30) -> QuincenaHealthStatus.HOLGADO
            else -> QuincenaHealthStatus.EQUILIBRADO
        }

        return QuincenaReport(
            currentDayOfMonth = dayOfMonth,
            targetPayoutDay = targetPayoutDay,
            daysRemainingToPayout = daysRemaining,
            daysElapsedInQuincena = daysElapsed,
            currentLiquidBalance = currentLiquidBalance,
            dailyBurnRate = effectiveDailyBurn,
            projectedExpenseToPayout = projectedExpense,
            projectedBalanceAtPayout = projectedBalanceAtPayout,
            oxygenDays = oxygenDays,
            status = status
        )
    }
}
