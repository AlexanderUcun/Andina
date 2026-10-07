package com.andina.finanzas.domain.model

data class Pocket(
    val id: Long = 0,
    val name: String,
    val goal: Money,
    val currentBalance: Money = Money.ZERO,
    val icon: String = "savings",
    val colorHex: String = "#435C3C",
    val deadline: Long? = null,
    val archived: Boolean = false,
    val sortOrder: Int = 0
) {
    /**
     * Progreso normalizado entre 0.0 y 1.0
     */
    val progress: Float
        get() = if (goal.pesos > 0L) {
            (currentBalance.pesos.toFloat() / goal.pesos.toFloat()).coerceIn(0f, 1f)
        } else 0f

    val isCompleted: Boolean
        get() = goal.pesos > 0L && currentBalance.pesos >= goal.pesos

    val remaining: Money
        get() = if (currentBalance.pesos >= goal.pesos) Money.ZERO else goal - currentBalance
}
