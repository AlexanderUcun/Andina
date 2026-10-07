package com.andina.finanzas.domain.model

data class FinancialSummary(
    val totalNetWorth: Money = Money.ZERO,
    val totalInWallets: Money = Money.ZERO,
    val totalInPockets: Money = Money.ZERO,
    val monthlyIncome: Money = Money.ZERO,
    val monthlyExpense: Money = Money.ZERO,
    val monthlyAntExpense: Money = Money.ZERO,
    val monthlyNetBalance: Money = Money.ZERO
) {
    /**
     * Tasa de ahorro calculada: (Ingreso - Gasto) / Ingreso, en rango 0..1
     */
    val savingsRate: Float
        get() = if (monthlyIncome.pesos > 0L && monthlyExpense.pesos < monthlyIncome.pesos) {
            ((monthlyIncome.pesos - monthlyExpense.pesos).toFloat() / monthlyIncome.pesos.toFloat()).coerceIn(0f, 1f)
        } else 0f
}
