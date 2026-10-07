package com.andina.finanzas.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.andina.finanzas.data.local.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow

data class MonthSummaryTuple(
    val totalIncome: Long,
    val totalExpense: Long,
    val totalAnt: Long
)

data class CategorySpendTuple(
    val categoryId: Long,
    val total: Long
)

@Dao
interface TransactionDao {

    @Query("SELECT * FROM transactions ORDER BY occurredAt DESC LIMIT :limit")
    fun observeRecentTransactions(limit: Int = 5): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions ORDER BY occurredAt DESC")
    fun observeAllTransactions(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE walletId = :walletId OR toWalletId = :walletId ORDER BY occurredAt DESC")
    fun observeTransactionsByWallet(walletId: Long): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE occurredAt BETWEEN :from AND :to ORDER BY occurredAt DESC")
    fun observeTransactionsBetween(from: Long, to: Long): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE id = :id LIMIT 1")
    suspend fun getTransactionById(id: Long): TransactionEntity?

    @Query("SELECT * FROM transactions WHERE id = :id LIMIT 1")
    fun observeTransactionById(id: Long): Flow<TransactionEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransactions(transactions: List<TransactionEntity>): List<Long>

    @Update
    suspend fun updateTransaction(transaction: TransactionEntity)

    @Delete
    suspend fun deleteTransaction(transaction: TransactionEntity)

    @Query("DELETE FROM transactions WHERE id = :id")
    suspend fun deleteTransactionById(id: Long)

    @Query("SELECT * FROM transactions ORDER BY occurredAt DESC")
    suspend fun getAllTransactions(): List<TransactionEntity>

    @Query("DELETE FROM transactions")
    suspend fun deleteAllTransactions()

    // --- Consultas contables de agregación ---

    @Query("""
        SELECT
          COALESCE(SUM(CASE WHEN type = 'INGRESO' THEN amount END), 0) AS totalIncome,
          COALESCE(SUM(CASE WHEN type = 'GASTO' THEN amount END), 0) AS totalExpense,
          COALESCE(SUM(CASE WHEN type = 'GASTO' AND isAntExpense = 1 THEN amount END), 0) AS totalAnt
        FROM transactions
        WHERE occurredAt BETWEEN :from AND :to
    """)
    fun observeMonthSummary(from: Long, to: Long): Flow<MonthSummaryTuple>

    @Query("""
        SELECT categoryId, SUM(amount) AS total
        FROM transactions
        WHERE type = 'GASTO' AND occurredAt BETWEEN :from AND :to AND categoryId IS NOT NULL
        GROUP BY categoryId
        ORDER BY total DESC
    """)
    fun observeSpendByCategory(from: Long, to: Long): Flow<List<CategorySpendTuple>>

    // --- PagingSource para Paging 3 (Fase 10) ---

    @Query("""
        SELECT * FROM transactions
        WHERE (:query = '' OR note LIKE '%' || :query || '%')
          AND (:type IS NULL OR type = :type)
          AND (:walletId IS NULL OR walletId = :walletId OR toWalletId = :walletId)
          AND (:categoryId IS NULL OR categoryId = :categoryId)
          AND (:onlyAntExpense = 0 OR isAntExpense = 1)
          AND (:fromMillis IS NULL OR occurredAt >= :fromMillis)
          AND (:toMillis IS NULL OR occurredAt <= :toMillis)
        ORDER BY occurredAt DESC
    """)
    fun pagingSource(
        query: String = "",
        type: String? = null,
        walletId: Long? = null,
        categoryId: Long? = null,
        onlyAntExpense: Boolean = false,
        fromMillis: Long? = null,
        toMillis: Long? = null
    ): PagingSource<Int, TransactionEntity>
}

