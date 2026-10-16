package com.andina.finanzas.domain.model

enum class DebtType {
    RECEIVABLE, // Me deben (Presté dinero)
    PAYABLE     // Yo debo (Pedí prestado / Fiado)
}

data class Debt(
    val id: Long = 0,
    val personName: String,
    val type: DebtType,
    val totalAmount: Money,
    val paidAmount: Money = Money(0L),
    val walletId: Long,
    val dueDateMillis: Long? = null,
    val createdAtMillis: Long = System.currentTimeMillis(),
    val note: String = "",
    val isSettled: Boolean = false
) {
    val remainingAmount: Money
        get() = Money((totalAmount.pesos - paidAmount.pesos).coerceAtLeast(0L))

    val progressFraction: Float
        get() = if (totalAmount.pesos > 0) {
            (paidAmount.pesos.toFloat() / totalAmount.pesos.toFloat()).coerceIn(0f, 1f)
        } else 0f
}
