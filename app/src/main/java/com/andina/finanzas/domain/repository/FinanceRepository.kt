package com.andina.finanzas.domain.repository

import com.andina.finanzas.domain.model.Category
import com.andina.finanzas.domain.model.CategoryKind
import com.andina.finanzas.domain.model.FinancialSummary
import com.andina.finanzas.domain.model.Money
import com.andina.finanzas.domain.model.Pocket
import com.andina.finanzas.domain.model.Transaction
import com.andina.finanzas.domain.model.Wallet
import kotlinx.coroutines.flow.Flow

/**
 * Contrato central del repositorio de finanzas del dominio.
 * Desacopla la lógica de negocio y casos de uso de la persistencia directa en Room.
 */
interface FinanceRepository {

    // --- Billeteras y Libro Mayor ---
    fun observeWallets(): Flow<List<Wallet>>
    fun observeWallet(id: Long): Flow<Wallet?>
    suspend fun getWalletById(id: Long): Wallet?

    // --- Resumen Financiero Consolidado ---
    fun observeFinancialSummary(fromMillis: Long, toMillis: Long): Flow<FinancialSummary>

    // --- Transacciones ---
    fun observeRecentTransactions(limit: Int = 5): Flow<List<Transaction>>
    suspend fun getTransactionById(id: Long): Transaction?
    suspend fun registerTransaction(transaction: Transaction): Long
    suspend fun deleteTransaction(id: Long)

    // --- Bolsillos y Metas de Ahorro ---
    fun observePockets(): Flow<List<Pocket>>
    fun observePocket(id: Long): Flow<Pocket?>
    suspend fun getPocketById(id: Long): Pocket?
    suspend fun createPocket(pocket: Pocket): Long
    suspend fun contributeToPocket(pocketId: Long, walletId: Long, amount: Money): Boolean
    suspend fun withdrawFromPocket(pocketId: Long, walletId: Long, amount: Money): Boolean

    // --- Categorías ---
    fun observeCategories(kind: CategoryKind? = null): Flow<List<Category>>
    suspend fun getCategoryById(id: Long): Category?
}
