package com.andina.finanzas.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Representa una cuenta, billetera o medio de pago (ej. Nequi, Daviplata, Bancolombia, Efectivo).
 * Los saldos no se alteran directamente; se derivan del Ledger (WalletBalanceView).
 */
@Entity(tableName = "wallets")
data class WalletEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val type: String, // NEQUI, DAVIPLATA, BANCOLOMBIA, EFECTIVO, TARJETA_CREDITO, OTRA
    val initialBalance: Long = 0L,
    val colorHex: String,
    val archived: Boolean = false,
    val sortOrder: Int = 0
) {
    companion object {
        const val TYPE_NEQUI = "NEQUI"
        const val TYPE_DAVIPLATA = "DAVIPLATA"
        const val TYPE_BANCOLOMBIA = "BANCOLOMBIA"
        const val TYPE_EFECTIVO = "EFECTIVO"
        const val TYPE_TARJETA_CREDITO = "TARJETA_CREDITO"
        const val TYPE_OTRA = "OTRA"

        /**
         * Billeteras colombianas estándar para inicializar la base de datos local.
         */
        val DEFAULT_WALLETS = listOf(
            WalletEntity(
                name = "Nequi",
                type = TYPE_NEQUI,
                initialBalance = 0L,
                colorHex = "#4A154B", // Morado Nequi
                sortOrder = 0
            ),
            WalletEntity(
                name = "Daviplata",
                type = TYPE_DAVIPLATA,
                initialBalance = 0L,
                colorHex = "#EE2724", // Rojo Daviplata
                sortOrder = 1
            ),
            WalletEntity(
                name = "Bancolombia",
                type = TYPE_BANCOLOMBIA,
                initialBalance = 0L,
                colorHex = "#FDDA24", // Amarillo Bancolombia
                sortOrder = 2
            ),
            WalletEntity(
                name = "Efectivo",
                type = TYPE_EFECTIVO,
                initialBalance = 0L,
                colorHex = "#435C3C", // Verde Musgo Andino
                sortOrder = 3
            )
        )
    }
}
