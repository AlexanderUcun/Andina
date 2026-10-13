package com.andina.finanzas.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.andina.finanzas.data.local.entity.WalletEntity
import com.andina.finanzas.data.local.view.WalletBalanceView
import kotlinx.coroutines.flow.Flow

@Dao
interface WalletDao {

    @Query("SELECT * FROM wallets WHERE archived = 0 ORDER BY sortOrder ASC, id ASC")
    fun observeActiveWallets(): Flow<List<WalletEntity>>

    @Query("SELECT * FROM wallets ORDER BY sortOrder ASC, id ASC")
    fun observeAllWallets(): Flow<List<WalletEntity>>

    @Query("SELECT * FROM wallets WHERE id = :id LIMIT 1")
    suspend fun getWalletById(id: Long): WalletEntity?

    @Query("SELECT * FROM wallets WHERE id = :id LIMIT 1")
    fun observeWalletById(id: Long): Flow<WalletEntity?>

    @Query("SELECT * FROM wallets WHERE archived = 0 ORDER BY sortOrder ASC, id ASC")
    suspend fun getActiveWallets(): List<WalletEntity>

    @Query("SELECT * FROM wallets ORDER BY sortOrder ASC, id ASC")
    suspend fun getAllWallets(): List<WalletEntity>

    @Query("DELETE FROM wallets")
    suspend fun deleteAllWallets()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWallet(wallet: WalletEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWallets(wallets: List<WalletEntity>): List<Long>

    @Update
    suspend fun updateWallet(wallet: WalletEntity)

    @Query("SELECT * FROM wallets WHERE archived = 1 ORDER BY sortOrder ASC, id ASC")
    fun observeArchivedWallets(): Flow<List<WalletEntity>>

    @Delete
    suspend fun deleteWallet(wallet: WalletEntity)

    @Query("DELETE FROM wallets WHERE id = :id")
    suspend fun deleteWalletById(id: Long)

    @Query("UPDATE wallets SET archived = 1 WHERE id = :id")
    suspend fun archiveWallet(id: Long)

    @Query("UPDATE wallets SET archived = 0 WHERE id = :id")
    suspend fun unarchiveWallet(id: Long)

    // --- Consultas al Libro Mayor (WalletBalanceView) ---

    @Query("SELECT * FROM wallet_balances")
    fun observeBalances(): Flow<List<WalletBalanceView>>

    @Query("SELECT * FROM wallet_balances WHERE walletId = :walletId LIMIT 1")
    fun observeBalanceByWalletId(walletId: Long): Flow<WalletBalanceView?>

    @Query("SELECT * FROM wallet_balances WHERE walletId = :walletId LIMIT 1")
    suspend fun getBalanceByWalletId(walletId: Long): WalletBalanceView?

    @Query("SELECT COALESCE(SUM(balance), 0) FROM wallet_balances")
    fun observeTotalWalletsBalance(): Flow<Long>
}
