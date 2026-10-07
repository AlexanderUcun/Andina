package com.andina.finanzas.domain.model

data class Wallet(
    val id: Long = 0,
    val name: String,
    val type: WalletType,
    val initialBalance: Money = Money.ZERO,
    val currentBalance: Money = Money.ZERO,
    val colorHex: String,
    val archived: Boolean = false,
    val sortOrder: Int = 0
)
