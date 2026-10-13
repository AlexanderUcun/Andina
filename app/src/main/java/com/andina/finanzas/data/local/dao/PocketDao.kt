package com.andina.finanzas.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.andina.finanzas.data.local.entity.PocketEntity
import com.andina.finanzas.data.local.entity.PocketMovementEntity
import com.andina.finanzas.data.local.view.PocketBalanceView
import kotlinx.coroutines.flow.Flow

@Dao
interface PocketDao {

    @Query("SELECT * FROM pockets WHERE archived = 0 ORDER BY sortOrder ASC, id ASC")
    fun observeActivePockets(): Flow<List<PocketEntity>>

    @Query("SELECT * FROM pockets ORDER BY sortOrder ASC, id ASC")
    fun observeAllPockets(): Flow<List<PocketEntity>>

    @Query("SELECT * FROM pockets WHERE id = :id LIMIT 1")
    suspend fun getPocketById(id: Long): PocketEntity?

    @Query("SELECT * FROM pockets WHERE id = :id LIMIT 1")
    fun observePocketById(id: Long): Flow<PocketEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPocket(pocket: PocketEntity): Long

    @Update
    suspend fun updatePocket(pocket: PocketEntity)

    @Delete
    suspend fun deletePocket(pocket: PocketEntity)

    @Query("DELETE FROM pockets WHERE id = :id")
    suspend fun deletePocketById(id: Long)

    @Query("UPDATE pockets SET archived = 1 WHERE id = :id")
    suspend fun archivePocket(id: Long)

    @Query("UPDATE pockets SET archived = 0 WHERE id = :id")
    suspend fun unarchivePocket(id: Long)

    @Query("SELECT * FROM pockets ORDER BY sortOrder ASC, id ASC")
    suspend fun getAllPockets(): List<PocketEntity>

    @Query("DELETE FROM pockets")
    suspend fun deleteAllPockets()

    // --- Movimientos a Bolsillos (Aportes y Retiros) ---

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovement(movement: PocketMovementEntity): Long

    @Query("SELECT * FROM pocket_movements ORDER BY occurredAt DESC")
    suspend fun getAllPocketMovements(): List<PocketMovementEntity>

    @Query("DELETE FROM pocket_movements")
    suspend fun deleteAllMovements()

    @Query("SELECT * FROM pocket_movements WHERE pocketId = :pocketId ORDER BY occurredAt DESC")
    fun observeMovementsForPocket(pocketId: Long): Flow<List<PocketMovementEntity>>

    @Query("SELECT * FROM pocket_movements WHERE pocketId = :pocketId ORDER BY occurredAt DESC")
    suspend fun getMovementsForPocket(pocketId: Long): List<PocketMovementEntity>

    // --- Consultas al Ledger de Bolsillos (PocketBalanceView) ---

    @Query("SELECT * FROM pocket_balances")
    fun observePocketBalances(): Flow<List<PocketBalanceView>>

    @Query("SELECT * FROM pocket_balances WHERE pocketId = :pocketId LIMIT 1")
    fun observePocketBalanceById(pocketId: Long): Flow<PocketBalanceView?>

    @Query("SELECT * FROM pocket_balances WHERE pocketId = :pocketId LIMIT 1")
    suspend fun getPocketBalanceById(pocketId: Long): PocketBalanceView?

    @Query("SELECT COALESCE(SUM(currentBalance), 0) FROM pocket_balances")
    fun observeTotalPocketsBalance(): Flow<Long>
}
