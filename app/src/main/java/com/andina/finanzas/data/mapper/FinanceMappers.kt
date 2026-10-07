package com.andina.finanzas.data.mapper

import com.andina.finanzas.data.local.entity.CategoryEntity
import com.andina.finanzas.data.local.entity.PocketEntity
import com.andina.finanzas.data.local.entity.TransactionEntity
import com.andina.finanzas.data.local.entity.WalletEntity
import com.andina.finanzas.domain.model.Category
import com.andina.finanzas.domain.model.CategoryKind
import com.andina.finanzas.domain.model.Money
import com.andina.finanzas.domain.model.Pocket
import com.andina.finanzas.domain.model.Transaction
import com.andina.finanzas.domain.model.TransactionType
import com.andina.finanzas.domain.model.Wallet
import com.andina.finanzas.domain.model.WalletType

fun WalletEntity.toDomain(currentBalance: Money = Money.ZERO): Wallet = Wallet(
    id = id,
    name = name,
    type = WalletType.fromString(type),
    initialBalance = Money(initialBalance),
    currentBalance = currentBalance,
    colorHex = colorHex,
    archived = archived,
    sortOrder = sortOrder
)

fun Wallet.toEntity(): WalletEntity = WalletEntity(
    id = id,
    name = name,
    type = type.name,
    initialBalance = initialBalance.pesos,
    colorHex = colorHex,
    archived = archived,
    sortOrder = sortOrder
)

fun CategoryEntity.toDomain(): Category = Category(
    id = id,
    name = name,
    icon = icon,
    colorHex = colorHex,
    kind = CategoryKind.fromString(kind),
    isAntCandidate = isAntCandidate,
    isDefault = isDefault,
    archived = archived,
    sortOrder = sortOrder
)

fun Category.toEntity(): CategoryEntity = CategoryEntity(
    id = id,
    name = name,
    icon = icon,
    colorHex = colorHex,
    kind = kind.name,
    isAntCandidate = isAntCandidate,
    isDefault = isDefault,
    archived = archived,
    sortOrder = sortOrder
)

fun PocketEntity.toDomain(currentBalance: Money = Money.ZERO): Pocket = Pocket(
    id = id,
    name = name,
    goal = Money(goal),
    currentBalance = currentBalance,
    icon = icon,
    colorHex = colorHex,
    deadline = deadline,
    archived = archived,
    sortOrder = sortOrder
)

fun Pocket.toEntity(): PocketEntity = PocketEntity(
    id = id,
    name = name,
    goal = goal.pesos,
    icon = icon,
    colorHex = colorHex,
    deadline = deadline,
    archived = archived,
    sortOrder = sortOrder
)

fun TransactionEntity.toDomain(
    walletMap: Map<Long, WalletEntity> = emptyMap(),
    categoryMap: Map<Long, CategoryEntity> = emptyMap()
): Transaction {
    val wallet = walletMap[walletId]
    val toWallet = toWalletId?.let { walletMap[it] }
    val category = categoryId?.let { categoryMap[it] }

    return Transaction(
        id = id,
        type = TransactionType.fromString(type),
        amount = Money(amount),
        walletId = walletId,
        toWalletId = toWalletId,
        categoryId = categoryId,
        note = note,
        occurredAt = occurredAt,
        createdAt = createdAt,
        isAntExpense = isAntExpense,
        receiptUri = receiptUri,
        recurringRuleId = recurringRuleId,
        walletName = wallet?.name,
        toWalletName = toWallet?.name,
        categoryName = category?.name,
        categoryIcon = category?.icon,
        categoryColorHex = category?.colorHex
    )
}

fun Transaction.toEntity(): TransactionEntity = TransactionEntity(
    id = id,
    type = type.name,
    amount = amount.pesos,
    walletId = walletId,
    toWalletId = toWalletId,
    categoryId = categoryId,
    note = note,
    occurredAt = occurredAt,
    createdAt = createdAt,
    isAntExpense = isAntExpense,
    receiptUri = receiptUri,
    recurringRuleId = recurringRuleId
)
