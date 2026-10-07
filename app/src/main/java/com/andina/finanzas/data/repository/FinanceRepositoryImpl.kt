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
    private val ledgerDao: LedgerDao
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
}
