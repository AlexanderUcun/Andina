package com.andina.finanzas.domain.model

data class Transaction(
    val id: Long = 0,
    val type: TransactionType,
    val amount: Money,
    val walletId: Long,
    val toWalletId: Long? = null,
    val categoryId: Long? = null,
    val note: String = "",
    val occurredAt: Long,
    val createdAt: Long = System.currentTimeMillis(),
    val isAntExpense: Boolean = false,
    val receiptUri: String? = null,
    val recurringRuleId: Long? = null,
    // Propiedades enriquecidas para presentación desacoplada en UI
    val walletName: String? = null,
    val toWalletName: String? = null,
    val categoryName: String? = null,
    val categoryIcon: String? = null,
    val categoryColorHex: String? = null
)
