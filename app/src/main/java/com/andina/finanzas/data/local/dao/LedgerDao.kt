package com.andina.finanzas.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Transaction
import com.andina.finanzas.data.local.entity.PocketMovementEntity
import com.andina.finanzas.data.local.entity.TransactionEntity

/**
 * Operaciones contables atómicas del libro mayor.
 * Cualquier operación que afecte múltiples balances se ejecuta bajo una transacción SQLite (@Transaction).
 */
@Dao
interface LedgerDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(tx: TransactionEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPocketMovement(movement: PocketMovementEntity): Long

    /**
     * Realiza una transferencia entre dos billeteras de forma atómica.
     */
    @Transaction
    suspend fun transferBetweenWallets(
        fromWalletId: Long,
        toWalletId: Long,
        amount: Long,
        note: String = "Transferencia entre cuentas",
        occurredAt: Long = System.currentTimeMillis()
    ): Long {
        require(amount > 0) { "El monto de la transferencia debe ser positivo" }
        require(fromWalletId != toWalletId) { "No se puede transferir a la misma billetera de origen" }

        val tx = TransactionEntity(
            type = TransactionEntity.TYPE_TRANSFERENCIA,
            amount = amount,
            walletId = fromWalletId,
            toWalletId = toWalletId,
            note = note,
            occurredAt = occurredAt,
            isAntExpense = false
        )
        return insertTransaction(tx)
    }

    /**
     * Aporta dinero a un bolsillo de ahorro desde una billetera.
     */
    @Transaction
    suspend fun contributeToPocket(
        pocketId: Long,
        walletId: Long,
        amount: Long,
        occurredAt: Long = System.currentTimeMillis()
    ): Long {
        require(amount > 0) { "El monto a aportar debe ser mayor a cero" }

        val movement = PocketMovementEntity(
            pocketId = pocketId,
            walletId = walletId,
            amount = amount, // Positivo incrementa el balance del bolsillo
            occurredAt = occurredAt
        )
        return insertPocketMovement(movement)
    }

    /**
     * Retira dinero de un bolsillo de ahorro hacia una billetera.
     */
    @Transaction
    suspend fun withdrawFromPocket(
        pocketId: Long,
        walletId: Long,
        amount: Long,
        occurredAt: Long = System.currentTimeMillis()
    ): Long {
        require(amount > 0) { "El monto a retirar debe ser mayor a cero" }

        val movement = PocketMovementEntity(
            pocketId = pocketId,
            walletId = walletId,
            amount = -amount, // Negativo reduce el balance del bolsillo
            occurredAt = occurredAt
        )
        return insertPocketMovement(movement)
    }
}
