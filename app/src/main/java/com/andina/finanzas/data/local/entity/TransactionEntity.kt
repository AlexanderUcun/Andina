package com.andina.finanzas.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Representa un movimiento financiero (Gasto, Ingreso o Transferencia).
 * Todos los montos se almacenan en pesos colombianos enteros (Long) para evitar errores de coma flotante.
 */
@Entity(
    tableName = "transactions",
    indices = [
        Index("occurredAt"),
        Index("walletId"),
        Index("toWalletId"),
        Index("categoryId")
    ]
)
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val type: String, // GASTO, INGRESO, TRANSFERENCIA
    val amount: Long, // Monto en pesos COP (siempre positivo)
    val walletId: Long, // Billetera origen
    val toWalletId: Long? = null, // Billetera destino (solo para TRANSFERENCIA)
    val categoryId: Long? = null,
    val note: String = "",
    val occurredAt: Long, // Milisegundos epoch del movimiento
    val createdAt: Long = System.currentTimeMillis(),
    val isAntExpense: Boolean = false,
    val receiptUri: String? = null,
    val recurringRuleId: Long? = null
) {
    companion object {
        const val TYPE_GASTO = "GASTO"
        const val TYPE_INGRESO = "INGRESO"
        const val TYPE_TRANSFERENCIA = "TRANSFERENCIA"
    }
}
