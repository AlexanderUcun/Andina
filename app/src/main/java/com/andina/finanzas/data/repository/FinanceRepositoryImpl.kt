package com.andina.finanzas.data.repository

import com.andina.finanzas.data.local.dao.CategoryDao
import com.andina.finanzas.data.local.dao.LedgerDao
import com.andina.finanzas.data.local.dao.PocketDao
import com.andina.finanzas.data.local.dao.TransactionDao
import com.andina.finanzas.data.local.dao.WalletDao
import com.andina.finanzas.data.mapper.toDomain
import com.andina.finanzas.data.mapper.toEntity
import com.andina.finanzas.domain.model.Category
import com.andina.finanzas.domain.model.CategoryKind
import com.andina.finanzas.domain.model.FinancialSummary
import com.andina.finanzas.domain.model.Money
import com.andina.finanzas.domain.model.Pocket
import com.andina.finanzas.domain.model.Transaction
import com.andina.finanzas.domain.model.TransactionType
import com.andina.finanzas.domain.model.Wallet
import com.andina.finanzas.domain.repository.FinanceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementación de producción de FinanceRepository conectada al motor Room y Libro Mayor.
 *
 * Garantiza que:
 * 1. Todos los saldos mostrados provengan de las vistas reactivas de balance.
 * 2. Operaciones interbancarias y de bolsillos se ejecuten atómicamente mediante LedgerDao.
 */
@Singleton
class FinanceRepositoryImpl @Inject constructor(
    private val walletDao: WalletDao,
    private val categoryDao: CategoryDao,
    private val transactionDao: TransactionDao,
    private val pocketDao: PocketDao,
    private val ledgerDao: LedgerDao,
    private val debtDao: com.andina.finanzas.data.local.dao.DebtDao
) : FinanceRepository {

    override fun observeWallets(): Flow<List<Wallet>> {
        return combine(
            walletDao.observeActiveWallets(),
            walletDao.observeBalances()
        ) { wallets, balances ->
            val balanceMap = balances.associate { it.walletId to it.balance }
            wallets.map { entity ->
                val current = balanceMap[entity.id] ?: entity.initialBalance
                entity.toDomain(currentBalance = Money(current))
            }
        }
    }

    override fun observeArchivedWallets(): Flow<List<Wallet>> {
        return combine(
            walletDao.observeArchivedWallets(),
            walletDao.observeBalances()
        ) { wallets, balances ->
            val balanceMap = balances.associate { it.walletId to it.balance }
            wallets.map { entity ->
                val current = balanceMap[entity.id] ?: entity.initialBalance
                entity.toDomain(currentBalance = Money(current))
            }
        }
    }

    override fun observeWallet(id: Long): Flow<Wallet?> {
        return combine(
            walletDao.observeWalletById(id),
            walletDao.observeBalanceByWalletId(id)
        ) { walletEntity, balanceView ->
            walletEntity?.let { entity ->
                val current = balanceView?.balance ?: entity.initialBalance
                entity.toDomain(currentBalance = Money(current))
            }
        }
    }

    override suspend fun getWalletById(id: Long): Wallet? {
        val entity = walletDao.getWalletById(id) ?: return null
        val balance = walletDao.getBalanceByWalletId(id)?.balance ?: entity.initialBalance
        return entity.toDomain(currentBalance = Money(balance))
    }

    override suspend fun createWallet(wallet: Wallet): Long {
        return walletDao.insertWallet(wallet.toEntity())
    }

    override suspend fun updateWallet(wallet: Wallet) {
        walletDao.updateWallet(wallet.toEntity())
    }

    override suspend fun archiveWallet(id: Long) {
        walletDao.archiveWallet(id)
    }

    override suspend fun unarchiveWallet(id: Long) {
        walletDao.unarchiveWallet(id)
    }

    override suspend fun deleteWallet(id: Long) {
        walletDao.deleteWalletById(id)
    }

    override fun observeFinancialSummary(fromMillis: Long, toMillis: Long): Flow<FinancialSummary> {
        return combine(
            walletDao.observeTotalWalletsBalance(),
            pocketDao.observeTotalPocketsBalance(),
            transactionDao.observeMonthSummary(fromMillis, toMillis)
        ) { walletsTotal, pocketsTotal, monthSummary ->
            FinancialSummary(
                totalNetWorth = Money(walletsTotal + pocketsTotal),
                totalInWallets = Money(walletsTotal),
                totalInPockets = Money(pocketsTotal),
                monthlyIncome = Money(monthSummary.totalIncome),
                monthlyExpense = Money(monthSummary.totalExpense),
                monthlyAntExpense = Money(monthSummary.totalAnt),
                monthlyNetBalance = Money(monthSummary.totalIncome - monthSummary.totalExpense)
            )
        }
    }

    override fun observeRecentTransactions(limit: Int): Flow<List<Transaction>> {
        return combine(
            transactionDao.observeRecentTransactions(limit),
            walletDao.observeAllWallets(),
            categoryDao.observeActiveCategories()
        ) { txs, wallets, categories ->
            val walletMap = wallets.associateBy { it.id }
            val categoryMap = categories.associateBy { it.id }
            txs.map { it.toDomain(walletMap, categoryMap) }
        }
    }

    override suspend fun getTransactionById(id: Long): Transaction? {
        val entity = transactionDao.getTransactionById(id) ?: return null
        val wallet = walletDao.getWalletById(entity.walletId)
        val toWallet = entity.toWalletId?.let { walletDao.getWalletById(it) }
        val category = entity.categoryId?.let { categoryDao.getCategoryById(it) }

        val walletMap = listOfNotNull(wallet, toWallet).associateBy { it.id }
        val categoryMap = listOfNotNull(category).associateBy { it.id }
        return entity.toDomain(walletMap, categoryMap)
    }

    override suspend fun registerTransaction(transaction: Transaction): Long {
        return if (transaction.type == TransactionType.TRANSFERENCIA && transaction.toWalletId != null) {
            ledgerDao.transferBetweenWallets(
                fromWalletId = transaction.walletId,
                toWalletId = transaction.toWalletId,
                amount = transaction.amount.pesos,
                note = transaction.note,
                occurredAt = transaction.occurredAt
            )
        } else {
            transactionDao.insertTransaction(transaction.toEntity())
        }
    }

    override fun getTransactionsPaged(
        query: String,
        type: TransactionType?,
        walletId: Long?,
        categoryId: Long?,
        onlyAntExpense: Boolean,
        fromMillis: Long?,
        toMillis: Long?
    ): Flow<PagingData<Transaction>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                prefetchDistance = 10,
                enablePlaceholders = false
            )
        ) {
            transactionDao.pagingSource(
                query = query,
                type = type?.name,
                walletId = walletId,
                categoryId = categoryId,
                onlyAntExpense = onlyAntExpense,
                fromMillis = fromMillis,
                toMillis = toMillis
            )
        }.flow.map { pagingData ->
            val wallets = walletDao.getActiveWallets().associateBy { it.id }
            val categories = categoryDao.getActiveCategories().associateBy { it.id }
            pagingData.map { entity ->
                entity.toDomain(wallets, categories)
            }
        }
    }

    override suspend fun updateTransaction(transaction: Transaction) {
        transactionDao.updateTransaction(transaction.toEntity())
    }

    override suspend fun restoreTransaction(transaction: Transaction): Long {
        return transactionDao.insertTransaction(transaction.toEntity())
    }

    override suspend fun deleteTransaction(id: Long) {
        transactionDao.deleteTransactionById(id)
    }

    override fun observePockets(): Flow<List<Pocket>> {
        return combine(
            pocketDao.observeActivePockets(),
            pocketDao.observePocketBalances()
        ) { pockets, balances ->
            val balanceMap = balances.associate { it.pocketId to it.currentBalance }
            pockets.map { entity ->
                val current = balanceMap[entity.id] ?: 0L
                entity.toDomain(currentBalance = Money(current))
            }
        }
    }

    override fun observePocket(id: Long): Flow<Pocket?> {
        return combine(
            pocketDao.observePocketById(id),
            pocketDao.observePocketBalanceById(id)
        ) { entity, balanceView ->
            entity?.let {
                val current = balanceView?.currentBalance ?: 0L
                it.toDomain(currentBalance = Money(current))
            }
        }
    }

    override suspend fun getPocketById(id: Long): Pocket? {
        val entity = pocketDao.getPocketById(id) ?: return null
        val balance = pocketDao.getPocketBalanceById(id)?.currentBalance ?: 0L
        return entity.toDomain(currentBalance = Money(balance))
    }

    override suspend fun createPocket(pocket: Pocket): Long {
        return pocketDao.insertPocket(pocket.toEntity())
    }

    override suspend fun updatePocket(pocket: Pocket) {
        pocketDao.updatePocket(pocket.toEntity())
    }

    override suspend fun archivePocket(id: Long) {
        pocketDao.archivePocket(id)
    }

    override suspend fun unarchivePocket(id: Long) {
        pocketDao.unarchivePocket(id)
    }

    override suspend fun deletePocket(id: Long) {
        pocketDao.deletePocketById(id)
    }

    override suspend fun contributeToPocket(pocketId: Long, walletId: Long, amount: Money): Boolean {
        ledgerDao.contributeToPocket(
            pocketId = pocketId,
            walletId = walletId,
            amount = amount.pesos
        )
        return true
    }

    override suspend fun withdrawFromPocket(pocketId: Long, walletId: Long, amount: Money): Boolean {
        ledgerDao.withdrawFromPocket(
            pocketId = pocketId,
            walletId = walletId,
            amount = amount.pesos
        )
        return true
    }

    override fun observeCategories(kind: CategoryKind?): Flow<List<Category>> {
        return if (kind == null) {
            categoryDao.observeActiveCategories().map { list -> list.map { it.toDomain() } }
        } else {
            categoryDao.observeCategoriesByKind(kind.name).map { list -> list.map { it.toDomain() } }
        }
    }

    override suspend fun getCategoryById(id: Long): Category? {
        return categoryDao.getCategoryById(id)?.toDomain()
    }

    override suspend fun createCategory(category: Category): Long {
        return categoryDao.insertCategory(category.toEntity())
    }

    override suspend fun updateCategory(category: Category) {
        categoryDao.updateCategory(category.toEntity())
    }

    override suspend fun deleteCategory(id: Long) {
        categoryDao.deleteCategoryById(id)
    }

    override suspend fun resetDemoData() {
        // Elimina transacciones de prueba
        transactionDao.deleteAllTransactions()
        // Elimina movimientos de bolsillos
        pocketDao.deleteAllMovements()
        // Resetea saldos iniciales de billeteras a cero
        val activeWallets = walletDao.getActiveWallets()
        activeWallets.forEach { entity ->
            walletDao.updateWallet(entity.copy(initialBalance = 0L))
        }
    }

    // --- Deudas y Préstamos ("Me deben" & "Yo debo") ---
    override fun observeDebts(): Flow<List<com.andina.finanzas.domain.model.Debt>> {
        return debtDao.observeAllDebts().map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun createDebt(
        debt: com.andina.finanzas.domain.model.Debt,
        affectWalletBalance: Boolean
    ): Result<Long> {
        return try {
            val debtEntity = debt.toEntity()
            val debtId = debtDao.insertDebt(debtEntity)

            if (affectWalletBalance && debt.totalAmount.pesos > 0) {
                val now = System.currentTimeMillis()
                val tx = when (debt.type) {
                    com.andina.finanzas.domain.model.DebtType.RECEIVABLE -> {
                        // Presté dinero -> Sale dinero de la billetera (GASTO sin categoría)
                        com.andina.finanzas.data.local.entity.TransactionEntity(
                            type = com.andina.finanzas.data.local.entity.TransactionEntity.TYPE_GASTO,
                            amount = debt.totalAmount.pesos,
                            walletId = debt.walletId,
                            categoryId = null,
                            note = if (debt.note.isNotBlank()) "🤝 Préstamo a ${debt.personName} · ${debt.note}" else "🤝 Préstamo a ${debt.personName}",
                            occurredAt = now,
                            isAntExpense = false
                        )
                    }
                    com.andina.finanzas.domain.model.DebtType.PAYABLE -> {
                        // Me prestaron -> Entra dinero a la billetera (INGRESO sin categoría)
                        com.andina.finanzas.data.local.entity.TransactionEntity(
                            type = com.andina.finanzas.data.local.entity.TransactionEntity.TYPE_INGRESO,
                            amount = debt.totalAmount.pesos,
                            walletId = debt.walletId,
                            categoryId = null,
                            note = if (debt.note.isNotBlank()) "🤝 Préstamo recibido de ${debt.personName} · ${debt.note}" else "🤝 Préstamo recibido de ${debt.personName}",
                            occurredAt = now,
                            isAntExpense = false
                        )
                    }
                }
                ledgerDao.insertTransaction(tx)
            }

            Result.success(debtId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun registerDebtPayment(
        debtId: Long,
        paymentAmount: Money,
        walletId: Long,
        note: String
    ): Result<Unit> {
        return try {
            val existing = debtDao.getDebtById(debtId)
                ?: return Result.failure(IllegalArgumentException("Deuda no encontrada"))

            val newPaidAmount = (existing.paidAmountPesos + paymentAmount.pesos).coerceAtMost(existing.totalAmountPesos)
            val isNowSettled = newPaidAmount >= existing.totalAmountPesos

            val updatedDebt = existing.copy(
                paidAmountPesos = newPaidAmount,
                isSettled = isNowSettled
            )
            debtDao.updateDebt(updatedDebt)

            if (paymentAmount.pesos > 0) {
                val now = System.currentTimeMillis()
                val isReceivable = existing.type == com.andina.finanzas.domain.model.DebtType.RECEIVABLE.name
                val tx = if (isReceivable) {
                    // Me pagaron parte de lo que me debían -> Entra dinero (INGRESO)
                    com.andina.finanzas.data.local.entity.TransactionEntity(
                        type = com.andina.finanzas.data.local.entity.TransactionEntity.TYPE_INGRESO,
                        amount = paymentAmount.pesos,
                        walletId = walletId,
                        categoryId = null,
                        note = if (note.isNotBlank()) "🤝 Abono de ${existing.personName} · $note" else "🤝 Abono de ${existing.personName}",
                        occurredAt = now,
                        isAntExpense = false
                    )
                } else {
                    // Aboné a mi deuda -> Sale dinero (GASTO)
                    com.andina.finanzas.data.local.entity.TransactionEntity(
                        type = com.andina.finanzas.data.local.entity.TransactionEntity.TYPE_GASTO,
                        amount = paymentAmount.pesos,
                        walletId = walletId,
                        categoryId = null,
                        note = if (note.isNotBlank()) "🤝 Pago de deuda a ${existing.personName} · $note" else "🤝 Pago de deuda a ${existing.personName}",
                        occurredAt = now,
                        isAntExpense = false
                    )
                }
                ledgerDao.insertTransaction(tx)
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteDebt(debtId: Long): Result<Unit> {
        return try {
            debtDao.deleteDebt(debtId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

private fun com.andina.finanzas.data.local.entity.DebtEntity.toDomain(): com.andina.finanzas.domain.model.Debt {
    return com.andina.finanzas.domain.model.Debt(
        id = id,
        personName = personName,
        type = if (type == com.andina.finanzas.domain.model.DebtType.PAYABLE.name) {
            com.andina.finanzas.domain.model.DebtType.PAYABLE
        } else {
            com.andina.finanzas.domain.model.DebtType.RECEIVABLE
        },
        totalAmount = Money(totalAmountPesos),
        paidAmount = Money(paidAmountPesos),
        walletId = walletId,
        dueDateMillis = dueDateMillis,
        createdAtMillis = createdAtMillis,
        note = note,
        isSettled = isSettled
    )
}

private fun com.andina.finanzas.domain.model.Debt.toEntity(): com.andina.finanzas.data.local.entity.DebtEntity {
    return com.andina.finanzas.data.local.entity.DebtEntity(
        id = id,
        personName = personName,
        type = type.name,
        totalAmountPesos = totalAmount.pesos,
        paidAmountPesos = paidAmount.pesos,
        walletId = walletId,
        dueDateMillis = dueDateMillis,
        createdAtMillis = createdAtMillis,
        note = note,
        isSettled = isSettled
    )
}

