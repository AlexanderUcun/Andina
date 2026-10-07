package com.andina.finanzas.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Representa un aporte (+) o retiro (-) entre una billetera y un bolsillo de ahorro.
 * Garantiza que el dinero transferido a un bolsillo reste de la billetera de origen
 * y sume al bolsillo sin duplicarse ni perderse.
 */
@Entity(
    tableName = "pocket_movements",
    indices = [
        Index("pocketId"),
        Index("walletId"),
        Index("occurredAt")
    ]
)
data class PocketMovementEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val pocketId: Long,
    val walletId: Long,
    val amount: Long, // Positivo para aportes a la meta, negativo para retiros hacia la billetera
    val occurredAt: Long = System.currentTimeMillis()
)
