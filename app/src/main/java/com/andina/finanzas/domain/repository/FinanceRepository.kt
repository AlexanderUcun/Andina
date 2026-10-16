package com.andina.finanzas.domain.repository

import com.andina.finanzas.domain.model.Category
import com.andina.finanzas.domain.model.CategoryKind
import com.andina.finanzas.domain.model.FinancialSummary
import com.andina.finanzas.domain.model.Money
import com.andina.finanzas.domain.model.Pocket
import com.andina.finanzas.domain.model.Transaction
import com.andina.finanzas.domain.model.Wallet
import kotlinx.coroutines.flow.Flow

import androidx.paging.PagingData
import com.andina.finanzas.domain.model.TransactionType

/**
 * Contrato central del repositorio de finanzas del dominio.
 * Desacopla la lógica de negocio y casos de uso de la persistencia directa en Room.
 */
interface FinanceRepository {

    // --- Billeteras y Libro Mayor ---
    fun observeWallets(): Flow<List<Wallet>>
    fun observeArchivedWallets(): Flow<List<Wallet>>
    fun observeWallet(id: Long): Flow<Wallet?>
    suspend fun getWalletById(id: Long): Wallet?
    suspend fun createWallet(wallet: Wallet): Long
    suspend fun updateWallet(wallet: Wallet)
    suspend fun archiveWallet(id: Long)
    suspend fun unarchiveWallet(id: Long)
    suspend fun deleteWallet(id: Long)

    // --- Resumen Financiero Consolidado ---
    fun observeFinancialSummary(fromMillis: Long, toMillis: Long): Flow<FinancialSummary>

    // --- Transacciones ---
    fun observeRecentTransactions(limit: Int = 5): Flow<List<Transaction>>
    fun getTransactionsPaged(
        query: String = "",
        type: TransactionType? = null,
        walletId: Long? = null,
        categoryId: Long? = null,
        onlyAntExpense: Boolean = false,
        fromMillis: Long? = null,
        toMillis: Long? = null
    ): Flow<PagingData<Transaction>>
    suspend fun getTransactionById(id: Long): Transaction?
    suspend fun registerTransaction(transaction: Transaction): Long
    suspend fun updateTransaction(transaction: Transaction)
    suspend fun restoreTransaction(transaction: Transaction): Long
    suspend fun deleteTransaction(id: Long)

    // --- Bolsillos y Metas de Ahorro ---
    fun observePockets(): Flow<List<Pocket>>
    fun observePocket(id: Long): Flow<Pocket?>
    suspend fun getPocketById(id: Long): Pocket?
    suspend fun createPocket(pocket: Pocket): Long
    suspend fun updatePocket(pocket: Pocket)
    suspend fun archivePocket(id: Long)
    suspend fun unarchivePocket(id: Long)
    suspend fun deletePocket(id: Long)
    suspend fun contributeToPocket(pocketId: Long, walletId: Long, amount: Money): Boolean
    suspend fun withdrawFromPocket(pocketId: Long, walletId: Long, amount: Money): Boolean

    // --- Categorías ---
    fun observeCategories(kind: CategoryKind? = null): Flow<List<Category>>
    suspend fun getCategoryById(id: Long): Category?
    suspend fun createCategory(category: Category): Long
    suspend fun updateCategory(category: Category)
    suspend fun deleteCategory(id: Long)

    // --- Deudas y Préstamos ("Me deben" & "Yo debo") ---
    fun observeDebts(): Flow<List<com.andina.finanzas.domain.model.Debt>>
    suspend fun createDebt(debt: com.andina.finanzas.domain.model.Debt, affectWalletBalance: Boolean): Result<Long>
    suspend fun registerDebtPayment(debtId: Long, paymentAmount: Money, walletId: Long, note: String = ""): Result<Unit>
    suspend fun deleteDebt(debtId: Long): Result<Unit>

    // --- Reinicio y Mantenimiento ---
    suspend fun resetDemoData()
}

